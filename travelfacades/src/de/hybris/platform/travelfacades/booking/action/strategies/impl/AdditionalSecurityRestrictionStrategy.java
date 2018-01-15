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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. If the property filtered travellers is set to true in
 * the reservation data the traveller is able to see only himself in the booking details page. This means that the check-in all
 * button needs to be disabled.
 */
public class AdditionalSecurityRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.check.in.additional.security.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		if (reservationData.getFilteredTravellers())
		{
			enabledBookingActions.forEach(bookingActionData ->
			{
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}
	}
}
