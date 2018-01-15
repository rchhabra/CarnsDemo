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

package de.hybris.platform.travelb2bservices.listeners;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravelAfterSessionUserChangeListener}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelAfterSessionUserChangeListenerTest
{
	@InjectMocks
	TravelAfterSessionUserChangeListener travelAfterSessionUserChangeListener;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private SessionService sessionService;

	@Mock
	private CartService cartService;

	@Mock
	private UserService userService;

	@Mock
	private Session session;

	@Test
	public void testOnEventForB2BCustomerModelUser()
	{
		final UserModel user = new B2BCustomerModel();
		when(userService.getCurrentUser()).thenReturn(user);

		when(sessionService.getCurrentSession()).thenReturn(session);
		doNothing().when(b2bUnitService).updateBranchInSession(session, user);
		doNothing().when(cartService).changeCurrentCartUser(user);
		travelAfterSessionUserChangeListener.onEvent(new AfterSessionUserChangeEvent());
		verify(cartService, times(1)).changeCurrentCartUser(user);
		verify(b2bUnitService, times(1)).updateBranchInSession(session, user);
	}

	@Test
	public void testOnEventForNonB2BCustomerModelUser()
	{
		final UserModel user = new UserModel();
		when(userService.getCurrentUser()).thenReturn(user);

		when(sessionService.getCurrentSession()).thenReturn(session);
		doNothing().when(b2bUnitService).updateBranchInSession(session, user);
		doNothing().when(cartService).changeCurrentCartUser(user);
		travelAfterSessionUserChangeListener.onEvent(new AfterSessionUserChangeEvent());
		verify(cartService, times(1)).changeCurrentCartUser(user);
		verify(b2bUnitService, times(0)).updateBranchInSession(session, user);
	}


}
