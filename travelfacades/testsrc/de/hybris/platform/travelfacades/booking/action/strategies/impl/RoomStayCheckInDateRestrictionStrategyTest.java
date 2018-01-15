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
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link RoomStayCheckInDateRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomStayCheckInDateRestrictionStrategyTest
{
	@InjectMocks
	RoomStayCheckInDateRestrictionStrategy strategy;

	@Test
	public void testBookingActionWithFutureDate()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setCheckInDate(testData.createDate("21/12/2017"));
		reservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		reservationData.setCode("acc1");

		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithPastDate()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setCheckInDate(testData.createDate("18/12/2016"));
		reservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		reservationData.setCode("acc1");

		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
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

	private class TestDataSetUp
	{
		public Date createDate(final String date)
		{
			final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			Date obj = null;
			try
			{
				obj = format.parse(date);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return obj;
		}
	}

}
