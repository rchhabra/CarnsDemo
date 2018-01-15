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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link PaymentTypeRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTypeRestrictionStrategyTest
{
	@InjectMocks
	PaymentTypeRestrictionStrategy strategy;

	private List<PaymentType> notAllowedPaymentTypes;


	@Before
	public void setUp()
	{
		notAllowedPaymentTypes = new ArrayList<>();
		notAllowedPaymentTypes.add(PaymentType.COST_CENTER);
		strategy.setNotAllowedPaymentTypes(notAllowedPaymentTypes);
	}

	@Test
	public void testBookingActionWithNotAllowedPaymentType()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setPaymentType(PaymentType.COST_CENTER);
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithAllowedPaymentType()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setPaymentType(PaymentType.CREDIT_CARD);
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledAction()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

}
