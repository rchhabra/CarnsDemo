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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link BookingStatusRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingStatusRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.status.alternative.message";

	@InjectMocks
	BookingStatusRestrictionStrategy bookingStatusRestrictionStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingStatusRestrictionStrategy = new BookingStatusRestrictionStrategy();
		bookingStatusRestrictionStrategy.setNotAllowedStatuses(Arrays.asList(OrderStatus.CANCELLING, OrderStatus.CANCELLED));
	}

	@Test
	public void testCancelledStatusForBookingAction()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");
		reservationData.setBookingStatusCode(OrderStatus.CANCELLING.toString());

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		bookingStatusRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

}
