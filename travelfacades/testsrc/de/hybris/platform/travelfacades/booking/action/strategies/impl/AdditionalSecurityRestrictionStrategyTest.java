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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AdditionalSecurityRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdditionalSecurityRestrictionStrategyTest
{
	@InjectMocks
	AdditionalSecurityRestrictionStrategy strategy;

	@Test
	public void testBookingAction()
	{
		final List<BookingActionData> enabledBookingActions = new ArrayList<>();
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setFilteredTravellers(Boolean.FALSE);
		strategy.applyStrategy(enabledBookingActions, reservationData);
		Assert.assertTrue(enabledBookingActions.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithFilteredTravellers()
	{
		final List<BookingActionData> enabledBookingActions = new ArrayList<>();
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setFilteredTravellers(Boolean.TRUE);
		strategy.applyStrategy(enabledBookingActions, reservationData);
		Assert.assertFalse(enabledBookingActions.get(0).isEnabled());
	}

	@Test
	public void testEmptyBookingAction()
	{
		final List<BookingActionData> enabledBookingActions = new ArrayList<>();
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(false);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final ReservationData reservationData = new ReservationData();
		strategy.applyStrategy(enabledBookingActions, reservationData);
	}

	@Test(expected = NullPointerException.class)
	public void testNullPointerBookingAction()
	{
		final List<BookingActionData> enabledBookingActions = new ArrayList<>();
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final ReservationData reservationData = new ReservationData();
		strategy.applyStrategy(enabledBookingActions, reservationData);
	}


}
