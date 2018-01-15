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
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.services.BookingService;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link LastAdultTravellerRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LastAdultTravellerRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.last.adult.traveller.alternative.message";

	@InjectMocks
	LastAdultTravellerRestrictionStrategy lastAdultTravellerRestrictionStrategy;

	@Mock
	BookingService bookingService;

	@Test
	public void testRestrictLastAdultTraveller()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT);

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setTravellerType(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER);
		travellerData.setTravellerInfo(passengerInformationData);

		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setTravellers(Stream.of(travellerData).collect(Collectors.toList()));

		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setOriginDestinationRefNumber(0);
		reservationItemData.setReservationItinerary(itineraryData);

		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));
		reservationData.setFilteredTravellers(false);

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		bookingActionData.setTraveller(travellerData);

		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(bookingActionData);

		lastAdultTravellerRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

	@Test
	public void testRestrictLastAdultTravellerWithFilteredTravellers()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT);

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setTravellerType(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER);
		travellerData.setTravellerInfo(passengerInformationData);

		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setTravellers(Stream.of(travellerData).collect(Collectors.toList()));

		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setOriginDestinationRefNumber(0);
		reservationItemData.setReservationItinerary(itineraryData);

		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));
		reservationData.setFilteredTravellers(true);

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		bookingActionData.setTraveller(travellerData);

		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(bookingActionData);

		given(bookingService.atleastOneAdultTravellerRemaining(Matchers.anyString(),Matchers.anyString())).willReturn(false);

		lastAdultTravellerRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

}
