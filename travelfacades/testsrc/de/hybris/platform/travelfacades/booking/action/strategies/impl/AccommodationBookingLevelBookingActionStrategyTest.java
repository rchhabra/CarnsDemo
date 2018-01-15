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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AccommodationBookingLevelBookingActionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationBookingLevelBookingActionStrategyTest
{
	@InjectMocks
	AccommodationBookingLevelBookingActionStrategy strategy;

	@Mock
	private Map<String, String> accommodationBookingActionTypeUrlMap;

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		reservationData.setCode("acc1");
		given(accommodationBookingActionTypeUrlMap.get(ActionTypeOption.ADD_ROOM.toString()))
				.willReturn("/manage-booking/add-room/{bookingReference}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.ADD_ROOM, reservationData);
		Assert.assertEquals("/manage-booking/add-room/acc1", bookingActionDataList.get(0).getActionUrl());
	}
}
