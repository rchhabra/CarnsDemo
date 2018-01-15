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

package de.hybris.platform.travelfacades.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.impl.DefaultAccommodationAmendmentFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultBookingFacade;
import de.hybris.platform.travelservices.order.TravelCartService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the AccommodationAmendmentFacade implementation
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationAmendmentFacadeTest
{
	@InjectMocks
	private DefaultAccommodationAmendmentFacade accommodationAmendmentFacade;

	@Mock
	private TravelCartService travelCartService;

	@Mock
	private DefaultBookingFacade bookingFacade;

	@Test
	public void testStartAmendment()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		when(travelCartService.createCartFromOrder(Matchers.anyString(), Matchers.anyString())).thenReturn(cart);
		final UserService us = Mockito.mock(UserService.class, Mockito.RETURNS_DEEP_STUBS);
		bookingFacade.setUserService(us);
		when(us.getCurrentUser().getUid()).thenReturn("deep");
		when(bookingFacade.getCurrentUserUid()).thenReturn("testUid");

		final boolean testAmendAddRoom = accommodationAmendmentFacade.startAmendment("0001");
		assertTrue(testAmendAddRoom);
	}
}
