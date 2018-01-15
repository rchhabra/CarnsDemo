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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation reserved room stay handler. This only populates the basic details(CheckInDate,
 * CheckOutDate, RoomStayPreferenceNumber, RatePlans, RoomType and Services) about the Reserved Room Stay Data.
 */
public class AccommodationReservationReservedRoomStayBasicDetailsHandler implements AccommodationReservationHandler
{
	private BookingService bookingService;

	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;

	private Converter<AccommodationModel, RoomTypeData> roomTypeConverter;

	/**
	 * Handle.
	 *
	 * @param abstractOrder
	 * 		the abstract order
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 */
	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		if (Objects.isNull(accommodationReservationData))
		{
			return;
		}

		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);

		final List<ReservedRoomStayData> roomStays = new ArrayList<ReservedRoomStayData>(CollectionUtils.size(entryGroups));
		entryGroups.forEach(entryGroup ->
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setCheckInDate(entryGroup.getStartingDate());
			roomStay.setCheckOutDate(entryGroup.getEndingDate());
			roomStay.setRoomStayRefNumber(entryGroup.getRoomStayRefNumber());
			roomStay.setRatePlans(Arrays.asList(getRatePlanConverter().convert(entryGroup.getRatePlan())));
			roomStay.setRoomTypes(Arrays.asList(getRoomTypeConverter().convert(entryGroup.getAccommodation())));
			roomStays.add(roomStay);
		});

		accommodationReservationData.setRoomStays(roomStays);
	}


	/**
	 * Gets rate plan converter.
	 *
	 * @return the rate plan converter
	 */
	protected Converter<RatePlanModel, RatePlanData> getRatePlanConverter()
	{
		return ratePlanConverter;
	}

	/**
	 * Sets rate plan converter.
	 *
	 * @param ratePlanConverter
	 * 		the rate plan converter
	 */
	@Required
	public void setRatePlanConverter(final Converter<RatePlanModel, RatePlanData> ratePlanConverter)
	{
		this.ratePlanConverter = ratePlanConverter;
	}

	/**
	 * Gets room type converter.
	 *
	 * @return the room type converter
	 */
	protected Converter<AccommodationModel, RoomTypeData> getRoomTypeConverter()
	{
		return roomTypeConverter;
	}

	/**
	 * Sets room type converter.
	 *
	 * @param roomTypeConverter
	 * 		the room type converter
	 */
	@Required
	public void setRoomTypeConverter(final Converter<AccommodationModel, RoomTypeData> roomTypeConverter)
	{
		this.roomTypeConverter = roomTypeConverter;
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
	 * 		the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

}
