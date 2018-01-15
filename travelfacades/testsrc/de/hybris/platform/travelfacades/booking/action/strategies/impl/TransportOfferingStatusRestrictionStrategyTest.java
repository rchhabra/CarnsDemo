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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TransportOfferingStatusRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingStatusRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.leg.check.in.alternative.message";

	@InjectMocks
	TransportOfferingStatusRestrictionStrategy transportOfferingStatusRestrictionStrategy;
	private final List<TransportOfferingStatus> notAllowedStatuses = Arrays.asList(TransportOfferingStatus.BOARDED,
			TransportOfferingStatus.CANCELLED, TransportOfferingStatus.DEPARTED);
	List<BookingActionData> bookingActionDataList;
	ReservationData reservationData;

	@Before
	public void setUp()
	{
		transportOfferingStatusRestrictionStrategy.setNotAllowedStatuses(notAllowedStatuses);
		final TransportOfferingData transportOfferingData1 = new TransportOfferingData();
		transportOfferingData1.setDepartureTime(DateUtils.addHours(new Date(), -4));
		transportOfferingData1.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData1.setArrivalTime(DateUtils.addHours(new Date(), -3));
		transportOfferingData1.setArrivalTimeZoneId(ZoneId.systemDefault());


		final TransportOfferingData transportOfferingData2 = new TransportOfferingData();
		transportOfferingData2.setDepartureTime(DateUtils.addHours(new Date(), -1));
		transportOfferingData2.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData2.setArrivalTime(DateUtils.addHours(new Date(), 2));
		transportOfferingData2.setArrivalTimeZoneId(ZoneId.systemDefault());
		transportOfferingData2.setStatus("SCHEDULED");

		final OriginDestinationOptionData originDestinationOptionData = new OriginDestinationOptionData();
		originDestinationOptionData
				.setTransportOfferings(Stream.of(transportOfferingData1, transportOfferingData2).collect(Collectors.toList()));

		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setOriginDestinationOptions(Stream.of(originDestinationOptionData).collect(Collectors.toList()));

		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setOriginDestinationRefNumber(0);
		reservationItemData.setReservationItinerary(itineraryData);

		reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);
	}

	@Test
	public void testTransportOfferingWithNotAllowedStatus()
	{
		reservationData.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
				.getTransportOfferings().get(0).setStatus("CANCELLED");
		transportOfferingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

	@Test
	public void testTransportOfferingWithAllowedStatus()
	{
		reservationData.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
				.getTransportOfferings().get(0).setStatus("DELAYED");
		transportOfferingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(bookingActionDataList.get(0).getAlternativeMessages().size(), 0);
	}
}
