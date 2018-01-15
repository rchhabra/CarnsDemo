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

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;

import java.util.Map;


/**
 * Strategy providing the behavior of the payment required for Changes dates functionality for AccommodationBooking
 */
public interface CalculatePaymentTypeForChangeDatesStrategy
{
	/**
	 * Provides the payment information for The change date functionality. It provides amount paid already paid for
	 * order, new amount to be paid/refund(if any) , the payment action required(REFUND,PAYABLE or SAME).
	 *
	 * @param accommodationReservationData
	 * @param accommodationAvailabilityResponse
	 * @return
	 */
	Map<String, String> calculate(AccommodationReservationData accommodationReservationData,
			AccommodationAvailabilityResponseData accommodationAvailabilityResponse);

}
