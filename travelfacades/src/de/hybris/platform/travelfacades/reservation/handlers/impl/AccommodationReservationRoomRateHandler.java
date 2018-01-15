/*
 *
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
 */

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation room rate handler.
 */
public class AccommodationReservationRoomRateHandler implements AccommodationReservationHandler
{
	private BookingService bookingService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		final String currencyIso = abstractOrder.getCurrency().getIsocode();

		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);


		for (final ReservedRoomStayData roomStay : accommodationReservationData.getRoomStays())
		{

			final List<RoomRateData> roomRates = new ArrayList<RoomRateData>();
			for (final AbstractOrderEntryModel entry : getEntries(entryGroups, abstractOrder, roomStay))
			{
				if (entry.getProduct() instanceof RoomRateProductModel)
				{
					final AccommodationOrderEntryInfoModel entryInfo = entry.getAccommodationOrderEntryInfo();
					entryInfo.getDates().forEach(date -> roomRates.add(createRoomRate(entry, date, currencyIso)));
				}
			}

			final Comparator<RoomRateData> byStartDate = (left,
					right) -> (left.getStayDateRange().getStartTime().before(right.getStayDateRange().getStartTime())) ? -1 : 1;

			roomRates.sort(byStartDate);
			roomStay.getRatePlans().get(0).setRoomRates(roomRates);
		}
	}

	/**
	 * Creates room rate
	 *
	 * @param entry
	 *
	 * @param date
	 *
	 * @param currencyIso
	 *
	 * @return created RoomRateData
	 */
	private RoomRateData createRoomRate(final AbstractOrderEntryModel entry, final Date date, final String currencyIso)
	{
		final RoomRateData roomRate = new RoomRateData();
		roomRate.setCode(entry.getProduct().getCode());
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(date);
		roomRate.setStayDateRange(stayDateRange);
		final RateData rate = new RateData();
		final Double basePrice = entry.getBasePrice();
		rate.setBasePrice(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(basePrice), currencyIso));
		final List<TaxData> taxes = entry.getTaxValues().stream()
				.map(taxValue -> createTaxDataFromValue(taxValue.getAppliedValue() / entry.getQuantity(), currencyIso))
				.collect(Collectors.toList());
		rate.setTaxes(taxes);
		final Double totalTax = taxes.stream().mapToDouble(taxData -> taxData.getPrice().getValue().doubleValue()).sum();
		rate.setTotalTax(createTaxDataFromValue(totalTax, currencyIso));
		final Double totalPrice = Double.sum(entry.getTotalPrice() / entry.getQuantity(), totalTax);
		rate.setActualRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalPrice), currencyIso));
		rate.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(basePrice + totalTax - totalPrice), currencyIso));
		roomRate.setRate(rate);
		return roomRate;
	}

	/**
	 * Creates tax data from value
	 *
	 * @param value
	 * @return
	 */
	protected TaxData createTaxDataFromValue(final Double value, final String currencyIso)
	{
		final TaxData taxData = new TaxData();
		taxData.setPrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(value), currencyIso));
		return taxData;
	}

	/**
	 * Returns the list of AbstractOrderEntryModels which are active and with quantityStatus not DEAD from the given
	 * entryGroups that matches the roomStayRefNumber
	 *
	 * @param entryGroups
	 *           the entry groups
	 * @param abstractOrder
	 *           the abstract order
	 * @param roomStay
	 *           the room stay
	 * @return the entries
	 */
	protected List<AbstractOrderEntryModel> getEntries(final List<AccommodationOrderEntryGroupModel> entryGroups,
			final AbstractOrderModel abstractOrder, final ReservedRoomStayData roomStay)
	{
		final Optional<AccommodationOrderEntryGroupModel> optionalEntryGroup = entryGroups.stream()
				.filter(entryGroup -> Objects.equals(entryGroup.getRoomStayRefNumber(), roomStay.getRoomStayRefNumber())).findFirst();
		if (optionalEntryGroup.isPresent())
		{
			return optionalEntryGroup.get().getEntries().stream()
					.filter(entry -> entry.getActive() && !Objects.equals(OrderEntryStatus.DEAD, entry.getQuantityStatus()))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
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
}
