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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract Strategy implementing
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionStrategy} holding common
 * functionalities
 */
public abstract class AbstractAccommodationBookingActionStrategy implements AccommodationBookingActionStrategy
{
	private Map<String, String> accommodationBookingActionTypeUrlMap;
	private static final String BOOKING_REFERENCE_PLACEHOLDER = "\\{bookingReference}";

	/**
	 * This method populates the url of the BookingActionData based on the actionType and the current user type. The url
	 * is taken from the Map<String, String> accommodationBookingActionTypeUrlMap defined in the spring configuration.
	 *
	 * @param bookingActionData
	 *           as the bookingActionData with the url to be populated
	 * @param accommodationReservationData
	 *           as the reservationData used to populate the url
	 */
	public void populateUrl(final AccommodationBookingActionData bookingActionData,
			final AccommodationReservationData accommodationReservationData)
	{
		String url = getAccommodationBookingActionTypeUrlMap().get(bookingActionData.getActionType().toString());
		url = url.replaceAll(BOOKING_REFERENCE_PLACEHOLDER, accommodationReservationData.getCode());
		// replacing other placeholders with actual values when needed
		bookingActionData.setActionUrl(url);
	}


	/**
	 * 
	 * @return the accommodationBookingActionTypeUrlMap
	 */
	protected Map<String, String> getAccommodationBookingActionTypeUrlMap()
	{
		return accommodationBookingActionTypeUrlMap;
	}

	/**
	 * 
	 * @param accommodationBookingActionTypeUrlMap
	 *           the accommodationBookingActionTypeUrlMap to set
	 */
	@Required
	public void setAccommodationBookingActionTypeUrlMap(final Map<String, String> accommodationBookingActionTypeUrlMap)
	{
		this.accommodationBookingActionTypeUrlMap = accommodationBookingActionTypeUrlMap;
	}



}
