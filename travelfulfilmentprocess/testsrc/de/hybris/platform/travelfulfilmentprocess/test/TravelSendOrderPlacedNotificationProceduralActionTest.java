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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.travelfulfilmentprocess.actions.order.TravelSendOrderPlacedNotificationProceduralAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TravelSendOrderPlacedNotificationProceduralActionTest
{

	@InjectMocks
	TravelSendOrderPlacedNotificationProceduralAction travelSendOrderPlacedNotificationProceduralAction;

	@Mock
	private EventService eventService;

	@Mock
	private OrderModel originalOrderModel;

	@Mock
	private OrderModel orderModel;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionWithnonNullOriginalOrder()
	{
		Mockito.when(orderProcessModel.getOrder()).thenReturn(orderModel);
		Mockito.when(orderModel.getOriginalOrder()).thenReturn(originalOrderModel);
		Mockito.doNothing().when(eventService).publishEvent(Matchers.any(AbstractEvent.class));
		travelSendOrderPlacedNotificationProceduralAction.executeAction(orderProcessModel);
	}

	@Test
	public void testExecuteActionWithNullOriginalOrder()
	{
		Mockito.when(orderProcessModel.getOrder()).thenReturn(orderModel);
		Mockito.when(orderModel.getOriginalOrder()).thenReturn(null);
		Mockito.doNothing().when(eventService).publishEvent(Matchers.any(AbstractEvent.class));
		travelSendOrderPlacedNotificationProceduralAction.executeAction(orderProcessModel);
	}
}
