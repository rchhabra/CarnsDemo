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
import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>, based on the payment type.
 */
public class PaymentTypeRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.amendment.not.permitted";

	private List<PaymentType> notAllowedPaymentTypes;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream().filter(BookingActionData::isEnabled)
				.collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final boolean disabled = getNotAllowedPaymentTypes().stream().filter(type -> type.equals(reservationData.getPaymentType()))
				.findFirst().isPresent();

		if (disabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}

	}

	/**
	 *
	 * @return notAllowedPaymentTypes
	 */
	protected List<PaymentType> getNotAllowedPaymentTypes()
	{
		return notAllowedPaymentTypes;
	}

	/**
	 *
	 * @param notAllowedPaymentTypes
	 *           the notAllowedPaymentTypes
	 */
	@Required
	public void setNotAllowedPaymentTypes(final List<PaymentType> notAllowedPaymentTypes)
	{
		this.notAllowedPaymentTypes = notAllowedPaymentTypes;
	}

}
