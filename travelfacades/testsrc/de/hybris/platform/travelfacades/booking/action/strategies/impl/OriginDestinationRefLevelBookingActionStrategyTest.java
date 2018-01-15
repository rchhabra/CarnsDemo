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
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link OriginDestinationRefLevelBookingActionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginDestinationRefLevelBookingActionStrategyTest
{
	@InjectMocks
	OriginDestinationRefLevelBookingActionStrategy originDestinationRefLevelBookingActionStrategy;

	@Before
	public void setUp() throws Exception
	{
		final Map<String, String> bookingActionTypeUrlMap = new HashMap<String, String>();
		bookingActionTypeUrlMap.put(ActionTypeOption.CHECK_IN.toString(),
				"/manage-booking/check-in/{orderCode}/{originDestinationRefNumber}?travellerReference={travellerUid}");
		originDestinationRefLevelBookingActionStrategy.setBookingActionTypeUrlMap(bookingActionTypeUrlMap);

		final Map<ActionTypeOption, List<String>> bookingActionTypeAltMessagesMap = new HashMap<ActionTypeOption, List<String>>();
		bookingActionTypeAltMessagesMap.put(ActionTypeOption.CHECK_IN,
				Stream.of("checked.in.action.alternative.message").collect(Collectors.toList()));
		originDestinationRefLevelBookingActionStrategy.setBookingActionTypeAltMessagesMap(bookingActionTypeAltMessagesMap);
	}

	@Test
	public void testApplyStrategy()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();

		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setOriginDestinationRefNumber(0);

		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));

		originDestinationRefLevelBookingActionStrategy.applyStrategy(bookingActionDataList, ActionTypeOption.CHECK_IN,
				reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ActionTypeOption.CHECK_IN, bookingActionDataList.get(0).getActionType());
	}

}
