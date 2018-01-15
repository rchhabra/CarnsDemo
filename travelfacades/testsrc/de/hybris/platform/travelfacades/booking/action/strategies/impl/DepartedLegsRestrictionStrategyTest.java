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
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;

import java.util.ArrayList;
import java.util.Collections;
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
 * Unit Test for the implementation of {@link DepartedLegsRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DepartedLegsRestrictionStrategyTest
{
	@InjectMocks
	DepartedLegsRestrictionStrategy strategy;

	private List<TransportOfferingStatus> notAllowedStatuses;

	@Before
	public void setUp()
	{
		notAllowedStatuses = new ArrayList<>();
		notAllowedStatuses.add(TransportOfferingStatus.DEPARTED);
		notAllowedStatuses.add(TransportOfferingStatus.BOARDED);
		notAllowedStatuses.add(TransportOfferingStatus.CANCELLED);
		strategy.setNotAllowedStatuses(notAllowedStatuses);
	}

	@Test
	public void testBookingActionWithAllowedTOStatus()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		final OriginDestinationOptionData odOption = new OriginDestinationOptionData();
		final TransportOfferingData toData = new TransportOfferingData();
		odOption.setTransportOfferings(Stream.of(toData).collect(Collectors.toList()));
		reservationItinerary.setOriginDestinationOptions(Stream.of(odOption).collect(Collectors.toList()));
		reservationItem.setReservationItinerary(reservationItinerary);
		reservationData.setReservationItems(Stream.of(reservationItem).collect(Collectors.toList()));
		toData.setStatus(TransportOfferingStatus.DELAYED.getCode());
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithNotAllowedTOStatus()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		final OriginDestinationOptionData odOption = new OriginDestinationOptionData();
		final TransportOfferingData toData = new TransportOfferingData();
		odOption.setTransportOfferings(Stream.of(toData).collect(Collectors.toList()));
		reservationItinerary.setOriginDestinationOptions(Stream.of(odOption).collect(Collectors.toList()));
		reservationItem.setReservationItinerary(reservationItinerary);
		reservationData.setReservationItems(Stream.of(reservationItem).collect(Collectors.toList()));
		toData.setStatus(TransportOfferingStatus.DEPARTED.getCode());
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutTO()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		final OriginDestinationOptionData odOption = new OriginDestinationOptionData();
		odOption.setTransportOfferings(Collections.EMPTY_LIST);
		reservationItinerary.setOriginDestinationOptions(Stream.of(odOption).collect(Collectors.toList()));
		reservationItem.setReservationItinerary(reservationItinerary);
		reservationData.setReservationItems(Stream.of(reservationItem).collect(Collectors.toList()));
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutodOptions()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		reservationItinerary.setOriginDestinationOptions(Collections.EMPTY_LIST);
		reservationItem.setReservationItinerary(reservationItinerary);
		reservationData.setReservationItems(Stream.of(reservationItem).collect(Collectors.toList()));
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutReservationitems()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(Collections.EMPTY_LIST);
		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithoutActions()
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
