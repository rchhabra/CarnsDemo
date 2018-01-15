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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.accommodation.ServiceRateData;
import de.hybris.platform.commercefacades.accommodation.TimeSpanData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation reserved room stay handler. This only populates Services about the Reserved Room
 * Stay Data.
 */
public class AccommodationReservationReservedRoomStayServiceDetailsHandler implements AccommodationReservationHandler
{
	private BookingService bookingService;

	private TravelCommercePriceFacade travelCommercePriceFacade;

	private Converter<ProductModel, ProductData> productConverter;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * Handle.
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @param accommodationReservationData
	 *           the accommodation reservation data
	 */
	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		if (Objects.isNull(accommodationReservationData) || CollectionUtils.isEmpty(accommodationReservationData.getRoomStays()))
		{
			return;
		}

		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);

		entryGroups.forEach(entryGroup -> {

			final ReservedRoomStayData roomStay = accommodationReservationData.getRoomStays().stream()
					.filter(roomStayData -> Objects.equals(roomStayData.getRoomStayRefNumber(), entryGroup.getRoomStayRefNumber())).findFirst()
					.get();

			setServices(entryGroup, roomStay, abstractOrder);
		});


	}

	/**
	 * Sets services.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 * @param abstractOrder
	 *           the abstract order
	 */
	public void setServices(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay,
			final AbstractOrderModel abstractOrder)
	{
		final List<ServiceData> services = new ArrayList<ServiceData>();
		getEntries(entryGroup).stream().filter(entry -> isAServiceProduct(entry.getProduct())).forEach(entry -> {
			final ServiceData service = new ServiceData();

			service.setCode(entry.getProduct().getCode());
			service.setQuantity(entry.getQuantity().intValue());
			service.setRatePlanCode(roomStay.getRatePlans().get(0).getCode());

			final ServiceDetailData serviceDetails = new ServiceDetailData();
			serviceDetails.setProduct(getProductConverter().convert(entry.getProduct()));
			serviceDetails.setGuestCounts(roomStay.getGuestCounts());
			service.setServiceDetails(serviceDetails);

			service.setPrice(getServiceRate(service, roomStay, entry, abstractOrder));

			services.add(service);
		});
		roomStay.setServices(services);
	}

	/**
	 * Is a service product boolean.
	 *
	 * @param product
	 *           the product
	 * @return the boolean
	 */
	protected boolean isAServiceProduct(final ProductModel product)
	{
		return !(product instanceof RoomRateProductModel);
	}

	/**
	 * Gets service rate.
	 *
	 * @param serviceData
	 *           the service data
	 * @param reservedRoomStayData
	 *           the reserved room stay data
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the service rate
	 */
	protected ServiceRateData getServiceRate(final ServiceData serviceData, final ReservedRoomStayData reservedRoomStayData,
			final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final ServiceRateData serviceRateData = new ServiceRateData();

		final TimeSpanData timeSpan = new TimeSpanData();
		timeSpan.setStartDate(reservedRoomStayData.getCheckInDate());
		timeSpan.setEndDate(reservedRoomStayData.getCheckOutDate());
		serviceRateData.setTimeSpan(timeSpan);
		serviceRateData.setBasePrice(getBasePriceData(entry, abstractOrder));
		serviceRateData.setTotal(getTotalPriceData(entry, abstractOrder));

		return serviceRateData;
	}

	/**
	 * Gets the total price data.
	 *
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the total price data
	 */
	protected PriceData getTotalPriceData(final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final BigDecimal totalPriceValue = BigDecimal.valueOf(entry.getTotalPrice());
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPriceValue,
				abstractOrder.getCurrency().getIsocode());
	}



	/**
	 * Gets the base price data.
	 *
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the base price data
	 */
	protected PriceData getBasePriceData(final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final BigDecimal basePriceValue = BigDecimal.valueOf(entry.getBasePrice());
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, basePriceValue,
				abstractOrder.getCurrency().getIsocode());
	}

	/**
	 * Returns the list of AbstractOrderEntryModels for the given entryGroup that are active and with quantityStatus
	 * different from DEAD.
	 *
	 * @param entryGroup
	 *           as the entryGroup
	 * @return a list of AbstractOrderEntryModels
	 */
	protected List<AbstractOrderEntryModel> getEntries(final AccommodationOrderEntryGroupModel entryGroup)
	{
		return entryGroup.getEntries().stream().filter(
				entry -> BooleanUtils.isTrue(entry.getActive()) && !Objects.equals(OrderEntryStatus.DEAD, entry.getQuantityStatus()))
				.collect(Collectors.toList());
	}

	/**
	 * Gets booking service.
	 *
	 * @return the booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 *           the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * Gets product converter.
	 *
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 *           the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the price data factory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the price data factory
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
