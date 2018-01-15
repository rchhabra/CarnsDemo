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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.AccommodationService;
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

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link AccommodationDetailsHandler} handling accommodation availability
 */
public class AccommodationAvailabilityHandler implements AccommodationDetailsHandler
{
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private AccommodationService accommodationService;
	private EnumerationService enumerationService;
	private AccommodationOfferingService accommodationOfferingService;

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

		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay -> roomStay.getRoomTypes().forEach(roomType -> {
			final AccommodationModel accommodation = (AccommodationModel) getProductService().getProductForCode(roomType.getCode());
			final Collection<RatePlanModel> ratePlans = accommodation.getRatePlan();
			if (CollectionUtils.isNotEmpty(ratePlans))
			{
				ratePlans.forEach(ratePlan -> {
					final List<Integer> stockLevels = new ArrayList<Integer>();
					Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate)).forEach(
							date -> collectAvailability(ratePlan, date, stockLevels, accommodationOfferingModel, accommodation));
					updateAccommodationData(ratePlan, roomStay, stockLevels);
				});
			}
		}));

	}

	/**
	 * This methods collect availabilities from accommodation and room rate products given a certain date. For the given
	 * rate plan linked to the given accommodation it finds the room rate product to be applied via date validation. Then
	 * retrieves stocklevel and adds the availability value to a list, initialized with the availability value for the
	 * given accommodation in that date. If a room rate product is forced to be out of stock, zero is added to the list
	 * If a room rate product is force to be in stock, it is ignored since it won't participate to availability
	 * calculation.
	 *
	 * @param ratePlan
	 *           the {@link RatePlanModel} currently taken into consideration
	 * @param date
	 *           the date
	 * @param stockLevels
	 *           the list of the availabilities retrieved both for accommodation and room rate products for the rate plan
	 *           taken into consideration
	 * @param accommodationOfferingModel
	 *           the {@link AccommodationOfferingModel}
	 * @param accommodation
	 *           the {@link AccommodationModel} currently analyzed
	 */
	protected void collectAvailability(final RatePlanModel ratePlan, final LocalDateTime date, final List<Integer> stockLevels,
			final AccommodationOfferingModel accommodationOfferingModel, final AccommodationModel accommodation)
	{
		final Date convertedDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		final Collection<WarehouseModel> warehouses = Collections.singletonList(accommodationOfferingModel);
		stockLevels.add(getCommerceStockService().getStockForDate(accommodation, convertedDate, warehouses));
		final Optional<ProductModel> optionalRoomRate = ratePlan.getProducts().stream()
				.filter(roomRate -> validateRoomRateAgainstDate(convertedDate, roomRate)).findAny();
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
	 *           to be validated
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
	 * @return true if the day of week in a given date is among the values of a list
	 */
	protected boolean isValidDayOfWeek(final Date date, final List<DayOfWeek> daysOfWeek)
	{
		final LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		final DayOfWeek dayOfWeek = enumerationService.getEnumerationValue(DayOfWeek.class, localDate.getDayOfWeek().toString());
		return daysOfWeek.contains(dayOfWeek);
	}

	/**
	 * This method creates a {@link RatePlanData} object and set the corresponding availability, calculated as the
	 * minimum among the values retrieved. The data object is then set against the relative {@link RoomTypeData}
	 *
	 * @param ratePlanModel
	 *           ratePlanModel to be created
	 * @param roomStay
	 *           to be updated
	 * @param stockLevels
	 *           list of values representing availability
	 */
	protected void updateAccommodationData(final RatePlanModel ratePlanModel, final RoomStayData roomStay,
			final List<Integer> stockLevels)
	{
		final Optional<RatePlanData> ratePlanData = roomStay.getRatePlans().stream()
				.filter(ratePlan -> StringUtils.equals(ratePlan.getCode(), ratePlanModel.getCode())).findAny();
		if (ratePlanData.isPresent() && CollectionUtils.isNotEmpty(stockLevels))
		{
			ratePlanData.get().setAvailableQuantity(stockLevels.stream().mapToInt(Integer::intValue).min().getAsInt());
		}
	}


	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	protected AccommodationService getAccommodationService()
	{
		return accommodationService;
	}

	@Required
	public void setAccommodationService(final AccommodationService accommodationService)
	{
		this.accommodationService = accommodationService;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

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
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}
}
