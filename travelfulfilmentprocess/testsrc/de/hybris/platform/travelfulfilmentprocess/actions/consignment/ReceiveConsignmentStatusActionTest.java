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

package de.hybris.platform.travelfulfilmentprocess.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.WarehouseConsignmentState;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReceiveConsignmentStatusActionTest
{
	@InjectMocks
	ReceiveConsignmentStatusAction receiveConsignmentStatusAction;

	@Mock
	ModelService modelservice;

	@Test
	public void testGetTransitions()
	{
		final Set<String> transitions = receiveConsignmentStatusAction.getTransitions();
		Assert.assertTrue(transitions.contains(ConfirmConsignmentPickupAction.Transition.CANCEL.toString()));
	}

	@Test
	public void testExecute()
	{
		final ConsignmentProcessModel process = new ConsignmentProcessModel();
		Assert.assertEquals(receiveConsignmentStatusAction.execute(process),
				ConfirmConsignmentPickupAction.Transition.ERROR.toString());

		process.setWarehouseConsignmentState(WarehouseConsignmentState.COMPLETE);
		Assert.assertEquals(receiveConsignmentStatusAction.execute(process),
				ConfirmConsignmentPickupAction.Transition.OK.toString());

		process.setWarehouseConsignmentState(WarehouseConsignmentState.CANCEL);
		Assert.assertEquals(receiveConsignmentStatusAction.execute(process),
				ConfirmConsignmentPickupAction.Transition.CANCEL.toString());

		process.setWarehouseConsignmentState(WarehouseConsignmentState.PARTIAL);
		Assert.assertEquals(receiveConsignmentStatusAction.execute(process),
				ConfirmConsignmentPickupAction.Transition.ERROR.toString());
	}
}
