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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.servicelayer.time.TimeService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link PastLegRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PastLegRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.past.leg.alternative.message";

	@InjectMocks
	PastLegRestrictionStrategy pastLegRestrictionStrategy;
	@Mock
	private TimeService timeService;

	@Test
	public void testPastDepartureTime()
	{
		final TransportOfferingData transportOfferingData1 = new TransportOfferingData();
		transportOfferingData1.setDepartureTime(DateUtils.addHours(new Date(), -1));
		transportOfferingData1.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData1.setArrivalTime(DateUtils.addHours(new Date(), 3));
		transportOfferingData1.setArrivalTimeZoneId(ZoneId.systemDefault());

		final TransportOfferingData transportOfferingData2 = new TransportOfferingData();
		transportOfferingData2.setDepartureTime(DateUtils.addDays(new Date(), 7));
		transportOfferingData2.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData2.setArrivalTime(DateUtils.addDays(new Date(), 8));
		transportOfferingData2.setArrivalTimeZoneId(ZoneId.systemDefault());

		final OriginDestinationOptionData originDestinationOptionData = new OriginDestinationOptionData();
		originDestinationOptionData.setTransportOfferings(Stream.of(transportOfferingData1, transportOfferingData2).collect(
				Collectors.toList()));

		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setOriginDestinationOptions(Stream.of(originDestinationOptionData).collect(Collectors.toList()));

		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setOriginDestinationRefNumber(0);
		reservationItemData.setReservationItinerary(itineraryData);

		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));

		given(timeService.getCurrentTime()).willReturn(new Date());

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		pastLegRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

}
