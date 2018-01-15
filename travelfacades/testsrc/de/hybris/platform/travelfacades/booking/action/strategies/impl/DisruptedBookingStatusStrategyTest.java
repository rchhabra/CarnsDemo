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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DisruptedBookingStatusStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DisruptedBookingStatusStrategyTest
{
	@InjectMocks
	DisruptedBookingStatusStrategy strategy;

	@Test
	public void testBookingActionWithEnabledBokingAction()
	{
		final BookingActionData action = new BookingActionData();
		action.setEnabled(true);
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(action);
		final ReservationData reservationData = new ReservationData();
		reservationData.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledBokingAction()
	{
		final BookingActionData action = new BookingActionData();
		action.setEnabled(false);
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(action);
		final ReservationData reservationData = new ReservationData();
		reservationData.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testDisruptedBooking()
	{
		final BookingActionData action = new BookingActionData();
		action.setEnabled(true);
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(action);
		final ReservationData reservationData = new ReservationData();
		reservationData.setBookingStatusCode(OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode());
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}
}
