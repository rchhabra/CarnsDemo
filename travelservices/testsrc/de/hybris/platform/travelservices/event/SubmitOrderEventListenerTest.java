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

package de.hybris.platform.travelservices.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SubmitOrderEventListener}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SubmitOrderEventListenerTest
{
	@InjectMocks
	SubmitOrderEventListener submitOrderEventListener;

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
		submitOrderEventListener.onSiteEvent(event);
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


		submitOrderEventListener.onSiteEvent(event);
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


		submitOrderEventListener.onSiteEvent(event);
		verify(businessProcessService, times(0)).startProcess(Matchers.any(OrderProcessModel.class));

	}

	@Test
	public void testGetSiteChannelForEvent()
	{
		final SubmitOrderEvent event = new SubmitOrderEvent();

		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");

		final BaseSiteModel site = new BaseSiteModel();
		site.setChannel(SiteChannel.B2C);
		order.setSite(site);
		event.setOrder(order);
		Assert.assertEquals(SiteChannel.B2C, submitOrderEventListener.getSiteChannelForEvent(event));

	}

}
