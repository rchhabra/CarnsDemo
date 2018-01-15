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

package de.hybris.platform.travelfulfilmentprocess.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.travelfulfilmentprocess.actions.order.SendOrderCancelledNotificationAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SendOrderCancelledNotificationActionTest
{
	@InjectMocks
	private SendOrderCancelledNotificationAction sendOrderCancelledNotificationAction;

	@Mock
	private EventService eventService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void executeAction()
	{
		final OrderProcessModel orderProcessModel = new OrderProcessModel();
		Mockito.doNothing().when(eventService).publishEvent(Matchers.any(AbstractEvent.class));
		sendOrderCancelledNotificationAction.executeAction(orderProcessModel);
	}
}
