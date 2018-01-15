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

import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler for {@link RoomStaysHandler} and {@link SelectedRoomStaysHandler}
 */
public abstract class AbstractRoomStaysHandler implements AccommodationDetailsHandler
{
	private AccommodationService accommodationService;
	private AbstractPopulatingConverter<AccommodationModel, RoomTypeData> roomTypeConverter;

	protected RoomStayData createRoomStayData(final AccommodationModel accommodation, final Integer roomStayRefNumber,
			final StayDateRangeData stayDateRange)
	{
		final RoomStayData roomStay = new RoomStayData();
		roomStay.setRoomTypes(getRoomTypeConverter().convertAll(Collections.singletonList(accommodation)));
		roomStay.setRoomStayRefNumber(roomStayRefNumber);
		roomStay.setCheckInDate(stayDateRange.getStartTime());
		roomStay.setCheckOutDate(stayDateRange.getEndTime());
		return roomStay;
	}

	protected ReservedRoomStayData createRoomStayData(final AccommodationModel accommodation,
			final StayDateRangeData stayDateRange, final RoomStayCandidateData roomStayCandidateData)
	{
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomTypes(getRoomTypeConverter().convertAll(Collections.singletonList(accommodation)));
		roomStay.setRoomStayRefNumber(roomStayCandidateData.getRoomStayCandidateRefNumber());
		roomStay.setCheckInDate(stayDateRange.getStartTime());
		roomStay.setCheckOutDate(stayDateRange.getEndTime());
		roomStay.setGuestCounts(roomStayCandidateData.getPassengerTypeQuantityList());
		roomStay.setServices(roomStayCandidateData.getServices());
		return roomStay;
	}

	/**
	 * @return the accommodationService
	 */
	protected AccommodationService getAccommodationService()
	{
		return accommodationService;
	}

	/**
	 * @param accommodationService
	 *           the accommodationService to set
	 */
	@Required
	public void setAccommodationService(final AccommodationService accommodationService)
	{
		this.accommodationService = accommodationService;
	}

	/**
	 * @return the roomTypeConverter
	 */
	protected AbstractPopulatingConverter<AccommodationModel, RoomTypeData> getRoomTypeConverter()
	{
		return roomTypeConverter;
	}

	/**
	 * @param roomTypeConverter
	 *           the roomTypeConverter to set
	 */
	@Required
	public void setRoomTypeConverter(final AbstractPopulatingConverter<AccommodationModel, RoomTypeData> roomTypeConverter)
	{
		this.roomTypeConverter = roomTypeConverter;
	}
}
