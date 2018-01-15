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
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AccommodationBookingStatusRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationBookingStatusRestrictionStrategyTest
{

	@InjectMocks
	AccommodationBookingStatusRestrictionStrategy strategy;

	private List<OrderStatus> notAllowedStatuses;

	@Before
	public void setUp()
	{
		notAllowedStatuses = new ArrayList<>();
		notAllowedStatuses.add(OrderStatus.CANCELLED);
		notAllowedStatuses.add(OrderStatus.CANCELLING);
		notAllowedStatuses.add(OrderStatus.AMENDMENTINPROGRESS);
		notAllowedStatuses.add(OrderStatus.ACTIVE_DISRUPTED_PENDING);
		notAllowedStatuses.add(OrderStatus.ORDER_SPLIT);
		notAllowedStatuses.add(OrderStatus.PAST);
		strategy.setNotAllowedStatuses(notAllowedStatuses);
	}

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> enabledBookingActions = new ArrayList<>();
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		reservationData.setBookingStatusCode(OrderStatus.AMENDMENTINPROGRESS.getCode());
		strategy.applyStrategy(enabledBookingActions, reservationData);
		Assert.assertFalse(enabledBookingActions.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledAction()
	{
		final List<AccommodationBookingActionData> enabledBookingActions = new ArrayList<>();
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(false);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		reservationData.setBookingStatusCode(OrderStatus.AMENDMENTINPROGRESS.getCode());
		strategy.applyStrategy(enabledBookingActions, reservationData);
	}

	@Test
	public void testBookingActionWithAllowedStatus()
	{
		final List<AccommodationBookingActionData> enabledBookingActions = new ArrayList<>();
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		reservationData.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		strategy.applyStrategy(enabledBookingActions, reservationData);
		Assert.assertTrue(enabledBookingActions.get(0).isEnabled());
	}

}
