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
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.event.AmendmentFinishedEvent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link OrderAmendmentEventListner}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderAmendmentEventListnerTest
{
	@InjectMocks
	OrderAmendmentEventListner orderAmendmentEventListner;

	@Mock
	private ModelService modelService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Test
	public void testOnSiteEvent()
	{
		final OrderProcessModel orderProcess = new OrderProcessModel();
		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");
		orderProcess.setOrder(order);
		final AmendmentFinishedEvent event = new AmendmentFinishedEvent(orderProcess);

		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString())).thenReturn(businessProcessModel);

		doNothing().when(modelService).save(Matchers.any(OrderProcessModel.class));
		orderAmendmentEventListner.onSiteEvent(event);
		verify(businessProcessService, times(1)).startProcess(Matchers.any(OrderProcessModel.class));
	}

	@Test
	public void testShouldHandleEvent()
	{
		final OrderProcessModel orderProcess = new OrderProcessModel();
		final OrderModel order = new OrderModel();
		order.setCode("TEST_ORDER_CODE");
		final AmendmentFinishedEvent event = new AmendmentFinishedEvent(orderProcess);


		final BaseSiteModel site = new BaseSiteModel();
		site.setChannel(SiteChannel.B2C);
		order.setSite(site);
		orderProcess.setOrder(order);

		doNothing().when(modelService).save(Matchers.any(OrderProcessModel.class));
		Assert.assertTrue(orderAmendmentEventListner.shouldHandleEvent(event));
	}

}
