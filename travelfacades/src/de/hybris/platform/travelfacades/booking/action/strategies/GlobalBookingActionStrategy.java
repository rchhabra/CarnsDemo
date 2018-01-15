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

package de.hybris.platform.travelfacades.booking.action.strategies;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.List;


/**
 * Strategy that expose the method to create and populate the List<BookingActionData> when creating a
 * BookingActionResponseData
 */
public interface GlobalBookingActionStrategy
{

	/**
	 * Applies the strategy for List<BookingActionData>
	 *
	 * @param bookingActionDataList
	 * 		the booking action data list
	 * @param actionType
	 * 		the action type
	 * @param globalReservationData
	 * 		the global reservation data
	 */
	void applyStrategy(List<BookingActionData> bookingActionDataList, ActionTypeOption actionType,
			GlobalTravelReservationData globalReservationData);

}
