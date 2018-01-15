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
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link CancelCompleteBookingRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelCompleteBookingRestrictionStrategyTest
{
	@InjectMocks
	CancelCompleteBookingRestrictionStrategy strategy;

	@Test
	public void testBookingActionWithbothBookingsCancelled()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setActionType(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.CANCELLED.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.CANCELLED.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithTansportBookingsCancelled()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		bookingAction.setEnabled(false);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setActionType(ActionTypeOption.ADD_ROOM);
		accommodationBookingAction.setEnabled(true);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithAccommodationBookingsCancelled()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.AMEND_ANCILLARY);
		bookingAction.setEnabled(true);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setActionType(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		accommodationBookingAction.setEnabled(false);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithbothBookingsCancelledWithActiveOrder()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		bookingAction.setEnabled(true);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setEnabled(true);
		accommodationBookingAction.setActionType(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithbothDisabledBookingActions()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		bookingAction.setEnabled(false);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setEnabled(false);
		accommodationBookingAction.setActionType(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutBookingActions()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		bookingActionResponse.setBookingActions(Collections.EMPTY_LIST);
		bookingActionResponse.setAccommodationBookingActions(Collections.EMPTY_LIST);
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutGlobalBookingActions()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		bookingActionResponse.setBookingActions(Collections.EMPTY_LIST);
		bookingActionResponse.setAccommodationBookingActions(Collections.EMPTY_LIST);
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
	}

	@Test
	public void testBookingAction()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData gloabalBookingAction = new BookingActionData();
		gloabalBookingAction.setEnabled(true);
		gloabalBookingAction.setAlternativeMessages(new ArrayList<>());
		bookingActionDataList.add(gloabalBookingAction);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		final BookingActionData bookingAction = new BookingActionData();
		bookingAction.setActionType(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		bookingAction.setEnabled(true);
		bookingActionResponse.setBookingActions(Stream.of(bookingAction).collect(Collectors.toList()));
		final AccommodationBookingActionData accommodationBookingAction = new AccommodationBookingActionData();
		accommodationBookingAction.setEnabled(false);
		accommodationBookingAction.setActionType(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		bookingActionResponse.setAccommodationBookingActions(Stream.of(accommodationBookingAction).collect(Collectors.toList()));
		final ReservationData reservation = new ReservationData();
		reservation.setBookingStatusCode(OrderStatus.ACTIVE.getCode());
		globalReservationData.setReservationData(reservation);
		final AccommodationReservationData accommodationReservation = new AccommodationReservationData();
		accommodationReservation.setBookingStatusCode(OrderStatus.CANCELLED.getCode());
		globalReservationData.setAccommodationReservationData(accommodationReservation);
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

}
