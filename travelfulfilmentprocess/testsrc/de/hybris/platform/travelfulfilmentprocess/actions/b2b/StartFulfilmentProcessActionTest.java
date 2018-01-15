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

package de.hybris.platform.travelfulfilmentprocess.actions.b2b;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StartFulfilmentProcessActionTest
{
	@InjectMocks
	StartFulfilmentProcessAction startFulfilmentProcessAction;

	@Mock
	BusinessProcessService businessProcessService;

	@Mock
	ModelService modelService;

	@Test
	public void testExecuteAction()
	{
		final B2BApprovalProcessModel process = new B2BApprovalProcessModel();
		final OrderModel order = new OrderModel();
		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);
		process.setOrder(order);

		startFulfilmentProcessAction.executeAction(process);

		store.setSubmitOrderProcessCode("fulfilmentProcessDefinitionName");
		final OrderProcessModel orderProcessModel = new OrderProcessModel();
		Mockito.when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(orderProcessModel);
		Mockito.doNothing().when(modelService).save(orderProcessModel);
		Mockito.doNothing().when(businessProcessService).startProcess(orderProcessModel);

		startFulfilmentProcessAction.executeAction(process);
	}

}
