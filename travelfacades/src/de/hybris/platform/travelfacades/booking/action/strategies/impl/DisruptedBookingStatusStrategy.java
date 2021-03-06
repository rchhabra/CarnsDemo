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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. If booking is in ACTIVE_DISRUPTED_PENDING status,
 * set available booking actions to true
 */
public class DisruptedBookingStatusStrategy implements BookingActionEnabledEvaluatorStrategy
{

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}
		if (!StringUtils.equalsIgnoreCase(OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode(),
				reservationData.getBookingStatusCode()))
		{
			enabledBookingActions.forEach(bookingActionData -> bookingActionData.setEnabled(Boolean.TRUE));
		}
	}

}
