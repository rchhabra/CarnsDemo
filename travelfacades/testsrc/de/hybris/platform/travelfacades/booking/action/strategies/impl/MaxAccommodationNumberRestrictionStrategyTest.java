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
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link MaxAccommodationNumberRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MaxAccommodationNumberRestrictionStrategyTest
{
	@InjectMocks
	MaxAccommodationNumberRestrictionStrategy strategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay1 = new ReservedRoomStayData();
		final ReservedRoomStayData roomStay2 = new ReservedRoomStayData();
		final ReservedRoomStayData roomStay3 = new ReservedRoomStayData();
		reservationData.setRoomStays(Arrays.asList(roomStay1, roomStay2, roomStay3));
		reservationData.setCode("acc1");
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString())).willReturn(3);
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithRoomStaysLessThanMaxRooms()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay1 = new ReservedRoomStayData();
		final ReservedRoomStayData roomStay2 = new ReservedRoomStayData();
		reservationData.setRoomStays(Arrays.asList(roomStay1, roomStay2));
		reservationData.setCode("acc1");
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString())).willReturn(3);
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}
}
