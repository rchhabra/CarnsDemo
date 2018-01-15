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

package de.hybris.platform.travelfacades.booking.action.strategies;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.List;


/**
 * Strategy that expose the method to evaluate the value of the enabled property for the List<BookingActionData> when
 * creating a BookingActionResponseData.
 */
public interface BookingActionEnabledEvaluatorStrategy
{

	/**
	 * Applies the strategy for List<BookingActionData>
	 *
	 * @param bookingActionDataList
	 * 		the booking action data list
	 * @param reservationData
	 * 		the reservation data
	 */
	void applyStrategy(List<BookingActionData> bookingActionDataList, ReservationData reservationData);

}
