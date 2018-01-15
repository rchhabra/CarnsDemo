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

package de.hybris.platform.travelservices.ordercancel.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelrulesengine.enums.RefundActionType;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.strategies.RefundActionStrategy;
import de.hybris.platform.travelservices.strategies.impl.RefundAllActionStrategy;
import de.hybris.platform.travelservices.strategies.impl.RetainAdminFeeOnRefundStrategy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultTotalRefundCalculationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTotalRefundCalculationStrategyTest
{
	@InjectMocks
	DefaultTotalRefundCalculationStrategy defaultTotalRefundCalculationStrategy;

	@Mock
	private TravelRulesService travelRulesService;

	@Mock
	private RetainAdminFeeOnRefundStrategy retainAdminFeeOnRefundStrategy;

	@Mock
	private RefundAllActionStrategy refundAllActionStrategy;

	@Before
	public void setUp()
	{
		final Map<String, RefundActionStrategy> refundActionStrategyMap = new HashMap<>();
		refundActionStrategyMap.put("DEFAULT", refundAllActionStrategy);
		refundActionStrategyMap.put("REFUND_ALL", refundAllActionStrategy);
		refundActionStrategyMap.put("RETAIN_ADMIN_FEE", retainAdminFeeOnRefundStrategy);
		defaultTotalRefundCalculationStrategy.setRefundActionStrategyMap(refundActionStrategyMap);
	}

	@Mock
	private OrderModel order;

	@Test
	public void testGetTotalToRefundForNullRefundActionType()
	{
		Mockito.when(travelRulesService.getRefundAction(order)).thenReturn(null);
		Mockito.when(refundAllActionStrategy.applyStrategy(order)).thenReturn(10d);
		Assert.assertEquals(10, defaultTotalRefundCalculationStrategy.getTotalToRefund(order).intValue());
	}

	@Test
	public void testGetTotalToRefundForUnknownRefundActionType()
	{
		final Map<String, RefundActionStrategy> refundActionStrategyMap = new HashMap<>();
		refundActionStrategyMap.put("DEFAULT", refundAllActionStrategy);
		refundActionStrategyMap.put("REFUND_ALL", refundAllActionStrategy);
		defaultTotalRefundCalculationStrategy.setRefundActionStrategyMap(refundActionStrategyMap);
		Mockito.when(travelRulesService.getRefundAction(order)).thenReturn(RefundActionType.RETAIN_ADMIN_FEE);
		defaultTotalRefundCalculationStrategy.getTotalToRefund(order);
		Mockito.when(refundAllActionStrategy.applyStrategy(order)).thenReturn(10d);
		Assert.assertEquals(10, defaultTotalRefundCalculationStrategy.getTotalToRefund(order).intValue());
	}

	@Test
	public void testGetTotalToRefund()
	{
		Mockito.when(travelRulesService.getRefundAction(order)).thenReturn(RefundActionType.RETAIN_ADMIN_FEE);
		defaultTotalRefundCalculationStrategy.getTotalToRefund(order);
		Mockito.when(retainAdminFeeOnRefundStrategy.applyStrategy(order)).thenReturn(5d);
		Assert.assertEquals(5, defaultTotalRefundCalculationStrategy.getTotalToRefund(order).intValue());
	}
}
