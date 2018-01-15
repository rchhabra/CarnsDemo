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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.ProfileData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * The type Default accommodation booking facade test.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationBookingFacadeTest
{
	@InjectMocks
	private DefaultAccommodationBookingFacade defaultAccommodationBookingFacade;

	//	@Mock


	@Test
	public void testGetBookerEmailIDWithAccommodationReservationData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<GuestData> guestDataList = new LinkedList<>();
		guestDataList.add(testDataSetUp.createGuestData("Smith"));
		guestDataList.add(testDataSetUp.createGuestData("White"));

		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(guestDataList);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalTravelReservationData.setAccommodationReservationData(accommodationReservationData);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));
		assertNotNull(defaultAccommodationBookingFacade
				.getBookerEmailIDFromAccommodationReservationData(globalTravelReservationData, "Smith"));
	}

	@Test
	public void testGetBookerEmailIDWithAccommodationReservationDataWrongCredentials()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<GuestData> guestDataList = new LinkedList<>();
		guestDataList.add(testDataSetUp.createGuestData("Smith"));
		guestDataList.add(testDataSetUp.createGuestData("White"));

		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(guestDataList);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalTravelReservationData.setAccommodationReservationData(accommodationReservationData);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));
		assertNull(defaultAccommodationBookingFacade
				.getBookerEmailIDFromAccommodationReservationData(globalTravelReservationData, "Rose"));
	}

	/* Data setup for Test */
	private class TestDataSetUp
	{
		private CustomerData createCustomerData(final String uid, final CustomerType type)
		{
			final CustomerData customerData = new CustomerData();
			customerData.setUid(uid);
			customerData.setType(type);
			return customerData;
		}

		private AccommodationReservationData getAccommodationReservationData(final String bookingReference,
				final List<ReservedRoomStayData> reservedRoomStayData, final String bookingStatusCode)
		{
			final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
			accommodationReservationData.setRoomStays(reservedRoomStayData);
			accommodationReservationData.setCode(bookingReference);
			accommodationReservationData.setBookingStatusCode(bookingStatusCode);
			return accommodationReservationData;
		}

		private ReservedRoomStayData createRoomStayData(final List<GuestData> reservedGuests)
		{
			final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
			roomStayData.setReservedGuests(reservedGuests);
			return roomStayData;
		}

		private GuestData createGuestData(final String lastName)
		{
			final GuestData guestData = new GuestData();
			final ProfileData profileData = new ProfileData();
			profileData.setLastName(lastName);
			guestData.setProfile(profileData);
			return guestData;
		}
	}

}
