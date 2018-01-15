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
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy that extends the
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.impl.AbstractAccommodationBookingActionStrategy}.
 * The strategy is used to create and populate the BookingActionDataList defined at an accommodation level.
 */
public class AccommodationBookingLevelBookingActionStrategy extends AbstractAccommodationBookingActionStrategy
{

	@Override
	public void applyStrategy(final List<AccommodationBookingActionData> bookingActionDataList, final ActionTypeOption actionType,
			final AccommodationReservationData reservationData)
	{
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setActionType(actionType);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		bookingActionData.setEnabled(true);
		populateUrl(bookingActionData, reservationData);

		bookingActionDataList.add(bookingActionData);
	}

}
