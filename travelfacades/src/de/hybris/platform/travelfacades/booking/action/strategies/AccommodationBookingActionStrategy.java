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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.List;


/**
 * Strategy that expose the method to create and populate the List<BookingActionData>
 */
public interface AccommodationBookingActionStrategy
{

	/**
	 * Applies the strategy for List<BookingActionData>
	 *
	 * @param bookingActionDataList
	 * 		the booking action data list
	 * @param actionType
	 * 		the action type
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 */
	void applyStrategy(List<AccommodationBookingActionData> bookingActionDataList, ActionTypeOption actionType,
			AccommodationReservationData accommodationReservationData);

}
