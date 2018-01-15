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
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AccommodationExpiredBookingStatusRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationExpiredBookingStatusRestrictionStrategyTest
{
	@InjectMocks
	AccommodationExpiredBookingStatusRestrictionStrategy accommodationExpiredBookingStatusRestrictionStrategy;

	@Before
	public void setUp()
	{
		accommodationExpiredBookingStatusRestrictionStrategy
				.setAllowedStatuses(Arrays.asList(OrderStatus.ACTIVE, OrderStatus.PAST));
	}

	@Test
	public void testApplyStrategyWithDisabledBookingActionData()
	{
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(false);

		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(bookingActionData);
		accommodationExpiredBookingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, null);

	}

	@Test
	public void testApplyStrategyForAllowedStatuses()
	{
		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setBookingStatusCode(OrderStatus.PAST.getCode());
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(true);

		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<AccommodationBookingActionData>();
		bookingActionDataList.add(bookingActionData);
		accommodationExpiredBookingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, accommodationReservationData);

	}

	@Test
	public void testApplyStrategyForNotAllowedStatuses()
	{
		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setBookingStatusCode(OrderStatus.ACTIVE_DISRUPTED.getCode());
		final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
		bookingActionData.setEnabled(true);

		bookingActionData.setAlternativeMessages(new ArrayList<>());

		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<AccommodationBookingActionData>();
		bookingActionDataList.add(bookingActionData);
		accommodationExpiredBookingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}
}
