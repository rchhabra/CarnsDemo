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

package de.hybris.platform.travelfacades.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCheckInEvaluatorStrategyTest
{
	@InjectMocks
	final DefaultCheckInEvaluatorStrategy strategy = new DefaultCheckInEvaluatorStrategy();

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration cfg;

	@Mock
	private TimeService timeService;

	private final List<TransportOfferingStatus> notAllowedStatuses = Arrays.asList(TransportOfferingStatus.BOARDED,
			TransportOfferingStatus.CANCELLED, TransportOfferingStatus.DEPARTED);

	private final int MIN_CHECKIN_TIME = 24;
	private final int MAX_CHECKIN_TIME = 1;

	@Before
	public void setup()
	{
		strategy.setNotAllowedStatuses(notAllowedStatuses);
		given(configurationService.getConfiguration()).willReturn(cfg);
		given(configurationService.getConfiguration().getInt(TravelfacadesConstants.MIN_CHECKIN_TIME_PROPERTY))
				.willReturn(MIN_CHECKIN_TIME);
		given(configurationService.getConfiguration().getInt(TravelfacadesConstants.MAX_CHECKIN_TIME_PROPERTY))
				.willReturn(MAX_CHECKIN_TIME);
		given(timeService.getCurrentTime()).willReturn(Calendar.getInstance().getTime());
	}

	@Test
	public void testForNoReservations()
	{
		final ReservationData reservation = null;
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testActiveReservation()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.SCHEDULED);
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertTrue(result);
	}

	@Test
	public void testCancelledReservation()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.CANCELLED,
				TransportOfferingStatus.SCHEDULED);
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testCancellingReservation()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.CANCELLING,
				TransportOfferingStatus.SCHEDULED);
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testForNoReservationItems()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.SCHEDULED);
		reservation.getReservationItems().clear();
		;
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testForNoTransportOfferingsOptions()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.SCHEDULED);
		reservation.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
		.getTransportOfferings().clear();
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testForBoardingTransportOffering()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.BOARDED);
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testForCancelledTransportOffering()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.CANCELLED);
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	@Test
	public void testForInvalidCheckInWindow()
	{
		final ReservationData reservation = TestData.createReservationData(OrderStatus.ACTIVE, TransportOfferingStatus.SCHEDULED);
		reservation.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
		.getTransportOfferings().get(0).setDepartureTime(Calendar.getInstance().getTime());
		reservation.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
				.getTransportOfferings().get(0).setDepartureTimeZoneId(ZoneId.systemDefault());
		final Boolean result = strategy.isCheckInPossible(reservation, 0);
		Assert.assertFalse(result);
	}

	private static class TestData
	{
		private static final int OUTBOUND = 0;

		public static ReservationData createReservationData(final OrderStatus orderStatus,
				final TransportOfferingStatus transportOfferingStatus)
		{
			final List<ReservationItemData> reservationItems = new ArrayList<>();
			reservationItems.add(createReservationItems(OUTBOUND, transportOfferingStatus));

			final ReservationData reservation = new ReservationData();
			reservation.setBookingStatusCode(orderStatus.getCode());
			reservation.setReservationItems(reservationItems);

			return reservation;
		}

		private static ReservationItemData createReservationItems(final int originDestinationRefNumber,
				final TransportOfferingStatus transportOfferingStatus)
		{
			final ReservationItemData reservationItem = new ReservationItemData();
			reservationItem.setOriginDestinationRefNumber(originDestinationRefNumber);
			reservationItem.setReservationItinerary(createItinerary(transportOfferingStatus));
			return reservationItem;
		}

		private static ItineraryData createItinerary(final TransportOfferingStatus transportOfferingStatus)
		{
			final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>();
			originDestinationOptions.add(createOriginDestinationOption(transportOfferingStatus));

			final ItineraryData itinerary = new ItineraryData();
			itinerary.setOriginDestinationOptions(originDestinationOptions);

			return itinerary;
		}

		private static OriginDestinationOptionData createOriginDestinationOption(
				final TransportOfferingStatus transportOfferingStatus)
		{
			final List<TransportOfferingData> transportOfferings = new ArrayList<>();
			transportOfferings.add(createTransportOffering(transportOfferingStatus));

			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption.setTransportOfferings(transportOfferings);
			return originDestinationOption;
		}

		private static TransportOfferingData createTransportOffering(final TransportOfferingStatus transportOfferingStatus)
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setStatus(transportOfferingStatus.getCode());
			transportOffering.setDepartureTime(TravelDateUtils.addHours(Calendar.getInstance().getTime(), 5));
			transportOffering.setDepartureTimeZoneId(ZoneId.systemDefault());
			return transportOffering;
		}

	}
}
