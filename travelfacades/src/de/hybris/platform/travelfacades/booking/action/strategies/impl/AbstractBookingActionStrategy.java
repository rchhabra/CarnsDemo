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
*/

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract Strategy implementing
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.BookingActionStrategy}.
 */
public abstract class AbstractBookingActionStrategy implements BookingActionStrategy
{
	private Map<String, String> bookingActionTypeUrlMap;

	private static final String TRAVELLER_UID_PLACEHOLDER = "\\{travellerUid}";
	private static final String ORIGIN_DESTINATION_REF_NUMBER_PLACEHOLDER = "\\{originDestinationRefNumber}";
	private static final String ORDER_CODE_PLACEHOLDER = "\\{orderCode}";

	/**
	 * This method populates the url of the BookingActionData based on the actionType and the current user type. The url
	 * is taken from the Map<String, String> bookingActionTypeUrlMap defined in the spring configuration.
	 *
	 * @param bookingActionData
	 *           as the bookingActionData with the url to be populated
	 * @param reservationData
	 *           as the reservationData used to populate the url
	 */
	public void populateUrl(final BookingActionData bookingActionData, final ReservationData reservationData)
	{
		String url = getBookingActionTypeUrlMap().get(bookingActionData.getActionType().toString());
		url = replaceUrlPlaceholdersWithValues(reservationData, bookingActionData, url);
		bookingActionData.setActionUrl(url);
	}

	protected String replaceUrlPlaceholdersWithValues(final ReservationData reservationData,
			final BookingActionData bookingActionData, final String url)
	{
		String replaceUrl = url.replaceAll(ORDER_CODE_PLACEHOLDER, reservationData.getCode());
		replaceUrl = replaceUrl.replaceAll(ORIGIN_DESTINATION_REF_NUMBER_PLACEHOLDER,
				String.valueOf(bookingActionData.getOriginDestinationRefNumber()));
		if (bookingActionData.getTraveller() != null)
		{
			replaceUrl = replaceUrl.replaceAll(TRAVELLER_UID_PLACEHOLDER, bookingActionData.getTraveller().getUid());
		}

		return replaceUrl;
	}

	/**
	 * @return the bookingActionTypeUrlMap
	 */
	protected Map<String, String> getBookingActionTypeUrlMap()
	{
		return bookingActionTypeUrlMap;
	}

	/**
	 * @param bookingActionTypeUrlMap
	 *           the bookingActionTypeUrlMap to set
	 */
	@Required
	public void setBookingActionTypeUrlMap(final Map<String, String> bookingActionTypeUrlMap)
	{
		this.bookingActionTypeUrlMap = bookingActionTypeUrlMap;
	}
}
