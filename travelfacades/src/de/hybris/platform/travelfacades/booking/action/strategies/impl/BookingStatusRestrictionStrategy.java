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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>, setting it to false if the orderStatus is
 * CANCELLED or CANCELLING.
 */
public class BookingStatusRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private List<OrderStatus> notAllowedStatuses;

	private static final String ALTERNATIVE_MESSAGE = "booking.action.status.alternative.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final boolean disabled = getNotAllowedStatuses().stream()
				.anyMatch(status -> StringUtils.equalsIgnoreCase(status.getCode(), reservationData.getBookingStatusCode()));


		if (disabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}
	}

	/**
	 * @return notAllowedStatuses
	 */
	protected List<OrderStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	/**
	 * @param notAllowedStatuses
	 *           the notAllowedStatuses to set
	 */
	@Required
	public void setNotAllowedStatuses(final List<OrderStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}

}
