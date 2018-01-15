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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionStrategy}. The strategy
 * is used to create and populate the BookingActionDataList defined at a booking level.
 */
public class GlobalBookingLevelBookingActionStrategy implements GlobalBookingActionStrategy
{

	private Map<String, String> globalBookingActionTypeUrlMap;
	private static final String BOOKING_REFERENCE_PLACEHOLDER = "\\{bookingReference}";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ActionTypeOption actionType,
			final GlobalTravelReservationData globalReservationData)
	{
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setActionType(actionType);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		bookingActionData.setEnabled(true);
		populateUrl(bookingActionData, globalReservationData);

		bookingActionDataList.add(bookingActionData);
	}

	/**
	 * This method populates the url of the BookingActionData based on the actionType and the current user type. The url
	 * is taken from the Map<String, String> accommodationBookingActionTypeUrlMap defined in the spring configuration.
	 *
	 * @param bookingActionData
	 *           as the bookingActionData with the url to be populated
	 * @param globalReservationData
	 *           as the reservationData used to populate the url
	 */
	public void populateUrl(final BookingActionData bookingActionData, final GlobalTravelReservationData globalReservationData)
	{
		String url = getGlobalBookingActionTypeUrlMap().get(bookingActionData.getActionType().toString());
		String bookingReference = StringUtils.EMPTY;
		if (Optional.ofNullable(globalReservationData.getReservationData()).isPresent())
		{
			bookingReference = globalReservationData.getReservationData().getCode();
		}
		if (Optional.ofNullable(globalReservationData.getAccommodationReservationData()).isPresent())
		{
			bookingReference = globalReservationData.getAccommodationReservationData().getCode();
		}
		url = url.replaceAll(BOOKING_REFERENCE_PLACEHOLDER, bookingReference);
		// replacing other placeholders with actual values when needed
		bookingActionData.setActionUrl(url);
	}

	/**
	 * @return the globalBookingActionTypeUrlMap
	 */
	protected Map<String, String> getGlobalBookingActionTypeUrlMap()
	{
		return globalBookingActionTypeUrlMap;
	}

	/**
	 * @param globalBookingActionTypeUrlMap
	 *           the globalBookingActionTypeUrlMap to set
	 */
	@Required
	public void setGlobalBookingActionTypeUrlMap(final Map<String, String> globalBookingActionTypeUrlMap)
	{
		this.globalBookingActionTypeUrlMap = globalBookingActionTypeUrlMap;
	}

}
