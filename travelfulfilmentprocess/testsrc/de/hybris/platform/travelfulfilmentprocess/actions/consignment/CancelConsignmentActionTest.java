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
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelConsignmentActionTest
{
	@InjectMocks
	CancelConsignmentAction cancelConsignmentAction;

	@Mock
	private ModelService modelService;

	@Test
	public void testExecuteAction()
	{
		final ConsignmentProcessModel process = new ConsignmentProcessModel();
		final ConsignmentModel consignment = new ConsignmentModel();
		process.setConsignment(consignment);

		cancelConsignmentAction.executeAction(process);
	}

}
