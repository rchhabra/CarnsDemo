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

package de.hybris.platform.travelservices.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.services.BookingService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCheckoutCustomerStrategyTest
{
	@InjectMocks
	DefaultTravelCheckoutCustomerStrategy defaultTravelCheckoutCustomerStrategy;

	@Mock
	private BookingService bookingService;

	@Mock
	private SessionService sessionService;

	private static final String ANONYMOUS_CHECKOUT_GUID = "anonymous_checkout_guid";
	private static final String MANAGE_MY_BOOKING_GUEST_UID = "manage_my_booking_guest_uid";

	@Test
	public void testIsValidBookingForCurrentGuestUser()
	{
		final OrderModel order = new OrderModel();
		final UserModel user = new UserModel();
		user.setUid("TEST_USER_UID|TEST_USER");
		order.setUser(user);
		when(bookingService.getOrderModelFromStore(Matchers.anyString())).thenReturn(order);
		when(sessionService.getAttribute(MANAGE_MY_BOOKING_GUEST_UID)).thenReturn(null);
		when(sessionService.getAttribute(ANONYMOUS_CHECKOUT_GUID)).thenReturn("TEST_USER");
		Assert.assertTrue(defaultTravelCheckoutCustomerStrategy.isValidBookingForCurrentGuestUser("0001"));

	}

	@Test
	public void testIsValidBookingForCurrentGuestUserForGuestIDInSession()
	{
		final OrderModel order = new OrderModel();
		final UserModel user = new UserModel();
		user.setUid("TEST_USER_UID|TEST_USER");
		order.setUser(user);
		when(bookingService.getOrderModelFromStore(Matchers.anyString())).thenReturn(order);
		when(sessionService.getAttribute(MANAGE_MY_BOOKING_GUEST_UID)).thenReturn("TEST_USER_UID//|)TEST_USER_1");
		when(sessionService.getAttribute(ANONYMOUS_CHECKOUT_GUID)).thenReturn("TEST_USER");
		Assert.assertFalse(defaultTravelCheckoutCustomerStrategy.isValidBookingForCurrentGuestUser("0001"));

	}

}
