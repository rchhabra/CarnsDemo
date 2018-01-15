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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.actions.order.MoveOrderToActiveAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MoveOrderToActiveActionTest
{
	@InjectMocks
	private MoveOrderToActiveAction moveOrderToActiveAction;

	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteAction() throws RetryLaterException, Exception
	{
		final OrderProcessModel orderProcessModel = new OrderProcessModel();
		final OrderModel order = new OrderModel();
		orderProcessModel.setOrder(order);
		moveOrderToActiveAction.executeAction(orderProcessModel);
	}
}
