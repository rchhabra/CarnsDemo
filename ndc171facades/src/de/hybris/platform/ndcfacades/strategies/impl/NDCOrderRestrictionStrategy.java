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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Strategy to evaluate if the order is placed throw NDC and in case, if it contains PAY_LATER transaction attached to it to disable all the amendment actions
 */
public class NDCOrderRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "ndc.action.status.alternative.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final boolean ndcOrder = StringUtils
				.equalsIgnoreCase(SalesApplication.NDC.toString(), reservationData.getSalesApplication());

		if (ndcOrder && isPayLater(reservationData))
		{
			enabledBookingActions.forEach(bookingActionData ->
			{
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}
	}

	/**
	 * Checks if the reservation Data contains a PAY LATER transaction.
	 *
	 * @param reservationData
	 * @return
	 */
	protected boolean isPayLater(final ReservationData reservationData)
	{
		return reservationData.getPaymentTransaction().stream()
				.flatMap(paymentTransaction -> paymentTransaction.getPaymentTransactionEntryData().stream())
				.anyMatch(paymentTransactionEntry -> PaymentTransactionType.PAY_LATER.equals(paymentTransactionEntry.getType()));
	}
}
