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
package de.hybris.platform.travelservices.ordercancel.impl.denialstrategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelOrderCancelStateMappingStrategyTest
{

	@InjectMocks
	private final DefaultTravelOrderCancelStateMappingStrategy strategy = new DefaultTravelOrderCancelStateMappingStrategy();

	@Test
	public void testCancelledOrderState()
	{
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.CANCELLED);

		final OrderCancelState state = strategy.getOrderCancelState(order);

		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, state);
	}

	@Test
	public void testCancellingOrderState()
	{
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.CANCELLING);

		final OrderCancelState state = strategy.getOrderCancelState(order);

		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, state);
	}

	@Test
	public void testOtherOrderState()
	{
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.ACTIVE);

		final OrderCancelState state = strategy.getOrderCancelState(order);

		Assert.assertEquals(OrderCancelState.CANCELPOSSIBLE, state);
	}

}
