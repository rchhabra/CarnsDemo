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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BAcceleratorInformOfOrderApprovalTest
{
	@InjectMocks
	B2BAcceleratorInformOfOrderApproval b2BAcceleratorInformOfOrderApproval;

	@Mock
	ModelService modelService;

	@Test(expected = IllegalStateException.class)
	public void testExecuteActionWithException()
	{
		final B2BApprovalProcessModel b2BApprovalProcessModel = new B2BApprovalProcessModel();
		final OrderModel order = Mockito.mock(OrderModel.class);
		b2BApprovalProcessModel.setOrder(order);
		Mockito.when(order.getUser()).thenThrow(new IllegalStateException());
		Mockito.doNothing().when(modelService).save(order);
		b2BAcceleratorInformOfOrderApproval.executeAction(b2BApprovalProcessModel);
	}

	@Test
	public void testExecuteAction()
	{
		final B2BApprovalProcessModel b2BApprovalProcessModel = new B2BApprovalProcessModel();
		final OrderModel order = new OrderModel();
		final B2BCustomerModel b2BCustomerModel = new B2BCustomerModel();
		order.setUser(b2BCustomerModel);
		b2BApprovalProcessModel.setOrder(order);
	}
}
