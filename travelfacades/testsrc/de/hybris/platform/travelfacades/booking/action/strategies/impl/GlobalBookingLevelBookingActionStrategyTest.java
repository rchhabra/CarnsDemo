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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link GlobalBookingLevelBookingActionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GlobalBookingLevelBookingActionStrategyTest
{
	@InjectMocks
	GlobalBookingLevelBookingActionStrategy strategy;

	@Mock
	private Map<String, String> globalBookingActionTypeUrlMap;

	@Test
	public void testBookingAction()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final ReservationData reservation = new ReservationData();
		reservation.setCode("travel");
		globalReservationData.setReservationData(reservation);
		given(globalBookingActionTypeUrlMap.get(ActionTypeOption.CANCEL_BOOKING.toString()))
				.willReturn("/manage-booking/cancel-booking/{bookingReference}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.CANCEL_BOOKING, globalReservationData);
		Assert.assertEquals("/manage-booking/cancel-booking/travel", bookingActionDataList.get(0).getActionUrl());
	}

	@Test
	public void testBookingActionWithAccommodationReservation()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final AccommodationReservationData reservation = new AccommodationReservationData();
		reservation.setCode("accommodation");
		globalReservationData.setAccommodationReservationData(reservation);
		given(globalBookingActionTypeUrlMap.get(ActionTypeOption.CANCEL_BOOKING.toString()))
				.willReturn("/manage-booking/cancel-booking/{bookingReference}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.CANCEL_BOOKING, globalReservationData);
		Assert.assertEquals("/manage-booking/cancel-booking/accommodation", bookingActionDataList.get(0).getActionUrl());
	}

	@Test
	public void testBookingActionWithoutReservation()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		given(globalBookingActionTypeUrlMap.get(ActionTypeOption.CANCEL_BOOKING.toString()))
				.willReturn("/manage-booking/cancel-booking/{bookingReference}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.CANCEL_BOOKING, globalReservationData);
		Assert.assertEquals("/manage-booking/cancel-booking/", bookingActionDataList.get(0).getActionUrl());
	}

}
