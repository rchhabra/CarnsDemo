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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link RoomStayExpiredCheckOutDateRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomStayExpiredCheckOutDateRestrictionStrategyTest
{
	@InjectMocks
	RoomStayExpiredCheckOutDateRestrictionStrategy strategy;

	private List<OrderStatus> statusesToIgnore;

	@Before
	public void setUp()
	{
		statusesToIgnore = new ArrayList<>();
		statusesToIgnore.add(OrderStatus.PAST);
		strategy.setStatusesToIgnore(statusesToIgnore);
	}

	@Test
	public void testBookingAction()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setCheckOutDate(testData.createDate(2));
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		accommodationReservationData.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithPastOrderStatus()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setCheckOutDate(testData.createDate(2));
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		accommodationReservationData.setBookingStatusCode(OrderStatus.PAST.getCode());
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithCheckOutDateBeforeTodaysDate()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setCheckOutDate(testData.createDate(-2));
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		bookingActionDataList.add(actionData);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	private class TestDataSetUp
	{
		public Date createDate(final int amount)
		{
			return TravelDateUtils.addDays(new Date(), amount);
		}
	}
}
