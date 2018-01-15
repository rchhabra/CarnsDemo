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
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CheckOrderEntryCostCentersActiveTest
{
	@InjectMocks
	CheckOrderEntryCostCentersActive checkOrderEntryCostCentersActive;

	@Mock
	ModelService modelService;

	@Test
	public void testExecuteActionWithNOK()
	{
		final B2BApprovalProcessModel b2BApprovalProcessModel = new B2BApprovalProcessModel();
		final OrderModel order = Mockito.mock(OrderModel.class);
		b2BApprovalProcessModel.setOrder(order);
		final OrderEntryModel orderEntry = new OrderEntryModel();
		final B2BCostCenterModel costCenter = new B2BCostCenterModel();
		costCenter.setActive(false);
		orderEntry.setCostCenter(costCenter);

		Mockito.when(order.getEntries()).thenReturn(Collections.singletonList(orderEntry));

		Assert.assertEquals(checkOrderEntryCostCentersActive.executeAction(b2BApprovalProcessModel),
				AbstractSimpleDecisionAction.Transition.NOK);
	}

	@Test
	public void testExecuteActionWithOK()
	{
		final B2BApprovalProcessModel b2BApprovalProcessModel = new B2BApprovalProcessModel();
		final OrderModel order = Mockito.mock(OrderModel.class);
		b2BApprovalProcessModel.setOrder(order);
		final OrderEntryModel orderEntry = new OrderEntryModel();
		final B2BCostCenterModel costCenter = new B2BCostCenterModel();
		costCenter.setActive(true);
		orderEntry.setCostCenter(costCenter);

		Mockito.when(order.getEntries()).thenReturn(Collections.singletonList(orderEntry));

		Assert.assertEquals(checkOrderEntryCostCentersActive.executeAction(b2BApprovalProcessModel),
				AbstractSimpleDecisionAction.Transition.OK);
	}

	@Test
	public void testExecuteActionWithException()
	{
		final B2BApprovalProcessModel b2BApprovalProcessModel = new B2BApprovalProcessModel();
		final OrderModel order = Mockito.mock(OrderModel.class);
		b2BApprovalProcessModel.setOrder(order);

		Mockito.when(order.getEntries()).thenThrow(new IllegalStateException());
		Mockito.doNothing().when(modelService).save(order);

		Assert.assertEquals(checkOrderEntryCostCentersActive.executeAction(b2BApprovalProcessModel),
				AbstractSimpleDecisionAction.Transition.NOK);
	}

}
