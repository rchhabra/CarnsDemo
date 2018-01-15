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

package de.hybris.platform.travelservices.event.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravelSubmitOrderEventListener}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSubmitOrderEventListenerTest
{
	@InjectMocks
	TravelSubmitOrderEventListener travelSubmitOrderEventListener;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private ModelService modelService;

	@Test
	public void testOnSiteEventForNullStore()
	{
		final SubmitOrderEvent event = new SubmitOrderEvent();

		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");
		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(null);
		event.setOrder(order);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(null);
		travelSubmitOrderEventListener.onSiteEvent(event);
		verify(businessProcessService, times(0)).startProcess(Matchers.any(OrderProcessModel.class));

	}

	@Test
	public void testOnSiteEventForNullStoreInOrder()
	{
		final SubmitOrderEvent event = new SubmitOrderEvent();

		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");

		final BaseStoreModel store = new BaseStoreModel();
		store.setSubmitOrderProcessCode("TEST_SUBMIT_ORDER_PROCESS_CODE");

		order.setStore(null);
		event.setOrder(order);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(store);

		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString())).thenReturn(businessProcessModel);

		doNothing().when(modelService).save(Matchers.any(OrderProcessModel.class));
		doNothing().when(businessProcessService).startProcess(Matchers.any(OrderProcessModel.class));


		travelSubmitOrderEventListener.onSiteEvent(event);
		verify(businessProcessService, times(1)).startProcess(Matchers.any(OrderProcessModel.class));

	}

	@Test
	public void testOnSiteEventFoNullFullfillmentProcessDefinitionCode()
	{
		final SubmitOrderEvent event = new SubmitOrderEvent();

		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");

		final BaseStoreModel store = new BaseStoreModel();

		order.setStore(store);
		event.setOrder(order);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(store);

		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString())).thenReturn(businessProcessModel);

		doNothing().when(modelService).save(Matchers.any(OrderProcessModel.class));
		doNothing().when(businessProcessService).startProcess(Matchers.any(OrderProcessModel.class));


		travelSubmitOrderEventListener.onSiteEvent(event);
		verify(businessProcessService, times(0)).startProcess(Matchers.any(OrderProcessModel.class));

	}

}
