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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.BookingService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RefundAllActionStrategyTest
{
	@InjectMocks
	RefundAllActionStrategy refundAllActionStrategy;

	@Mock
	private BookingService bookingService;

	@Test
	public void test()
	{
		final OrderModel order=new OrderModel();
		Mockito.when(bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT)).thenReturn(100d);
		Assert.assertEquals(100d, refundAllActionStrategy.applyStrategy(order, OrderEntryType.TRANSPORT), 0.001);
	}

}
