/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 *
 *
 */

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link AccommodationDetailsHandler} handling accommodation availability for deals
 */
public class DealAccommodationAvailabilityHandler implements AccommodationDetailsHandler
{
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private EnumerationService enumerationService;
	private AccommodationOfferingService accommodationOfferingService;
	private BundleTemplateService bundleTemplateService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);
		Instant instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getStartTime().getTime());
		final LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getEndTime().getTime());
		final LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay -> roomStay.getRoomTypes().forEach(roomType ->
		{
			final AccommodationModel accommodation = (AccommodationModel) getProductService().getProductForCode(roomType.getCode());
			final List<Integer> stockLevels = new ArrayList<>();
			Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate)).forEach(
					date -> collectAvailability(date, stockLevels, accommodationOfferingModel, accommodation,
							availabilityRequestData.getCriterion().getBundleTemplateId()));
			updateAccommodationData(roomStay, stockLevels);
		}));
	}

	/**
	 * This methods collect availabilities from accommodation and room rate products given a certain date. From the
	 * AccommodationBundleTemplate corresponding to the given accommodationBundleId it finds the room rate product to be applied
	 * via date validation. Then retrieves stocklevel and adds the availability value to a list, initialized with the availability
	 * value for the given accommodation in that date. If a room rate product is forced to be out of stock, zero is added to the
	 * list. If a room rate product is force to be in stock, it is ignored since it won't participate to availability calculation.
	 *
	 * @param date
	 * 		the date
	 * @param stockLevels
	 * 		the list of the availabilities retrieved both for accommodation and room rate products for the rate plan taken into
	 * 		consideration
	 * @param accommodationOfferingModel
	 * 		the {@link AccommodationOfferingModel}
	 * @param accommodation
	 * 		the {@link AccommodationModel} currently analyzed
	 * @param accommodationBundleId
	 * 		the id of the accommodationBundleTemplate
	 */
	protected void collectAvailability(final LocalDateTime date, final List<Integer> stockLevels, final AccommodationOfferingModel
			accommodationOfferingModel, final AccommodationModel accommodation, final String accommodationBundleId)
	{
		final Date convertedDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		final Collection<WarehouseModel> warehouses = Collections.singletonList(accommodationOfferingModel);
		stockLevels.add(getCommerceStockService().getStockForDate(accommodation, convertedDate, warehouses));

		final Optional<ProductModel> optionalRoomRate = getBundleTemplateService().getBundleTemplateForCode(accommodationBundleId)
				.getProducts().stream().filter(roomRate -> validateRoomRateAgainstDate(convertedDate, roomRate)).findAny();
		if (!optionalRoomRate.isPresent())
		{
			stockLevels.add(0);
			return;
		}
		final Long stockLevel = getCommerceStockService().getStockLevelQuantity(optionalRoomRate.get(), warehouses);
		if (Objects.nonNull(stockLevel))
		{
			stockLevels.add(stockLevel.intValue());
		}
	}

	/**
	 * This method validates a {@link RoomRateProductModel} against a date. The room rate is considered valid if it
	 * includes the given date and it is associated to the correct day of week.
	 *
	 * @param date
	 * @param roomRate
	 * 		to be validated
	 *
	 * @return true if the roomRate is valid, false otherwise
	 */
	protected boolean validateRoomRateAgainstDate(final Date date, final ProductModel roomRate)
	{
		final RoomRateProductModel roomRateProduct = (RoomRateProductModel) roomRate;
		for (final DateRangeModel dateRange : roomRateProduct.getDateRanges())
		{
			if (!date.before(dateRange.getStartingDate()) && !date.after(dateRange.getEndingDate())
					&& isValidDayOfWeek(date, roomRateProduct.getDaysOfWeek()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method performs day of week validation.
	 *
	 * @param date
	 * @param daysOfWeek
	 *
	 * @return true if the day of week in a given date is among the values of a list
	 */
	protected boolean isValidDayOfWeek(final Date date, final List<DayOfWeek> daysOfWeek)
	{
		final LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		final DayOfWeek dayOfWeek = getEnumerationService().getEnumerationValue(DayOfWeek.class,
				localDate.getDayOfWeek().toString());
		return daysOfWeek.contains(dayOfWeek);
	}

	/**
	 * This method updates the {@link RatePlanData} setting the corresponding availability, calculated as the
	 * minimum among the values retrieved. The data object is then set against the relative {@link RoomTypeData}
	 *
	 * @param roomStay
	 * 		the roomStay to be updated
	 * @param stockLevels
	 * 		the list of values representing availability
	 */
	protected void updateAccommodationData(final RoomStayData roomStay, final List<Integer> stockLevels)
	{
		final Optional<RatePlanData> ratePlanData = roomStay.getRatePlans().stream().findFirst();
		if (ratePlanData.isPresent() && CollectionUtils.isNotEmpty(stockLevels))
		{
			ratePlanData.get().setAvailableQuantity(stockLevels.stream().mapToInt(Integer::intValue).min().getAsInt());
		}
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 * 		the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 * 		the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * @param accommodationOfferingService
	 * 		the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	/**
	 * @return the bundleTemplateService
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * @param bundleTemplateService
	 * 		the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
