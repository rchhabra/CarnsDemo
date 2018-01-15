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
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultOrderCancelPaymentServiceAdapter}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderCancelPaymentServiceAdapterTest
{
	@Mock
	private BookingService bookingService;

	@Mock
	OrderModel order;

	@Test(expected = UnsupportedOperationException.class)
	public void testRecalculateOrderAndModifyPaymentsForException()
	{
		final DefaultOrderCancelPaymentServiceAdapter defaultOrderCancelPaymentServiceAdapter = new DefaultOrderCancelPaymentServiceAdapter();
		defaultOrderCancelPaymentServiceAdapter.recalculateOrderAndModifyPayments(order);
		Mockito.verify(bookingService, Mockito.times(0)).getTotalToRefund(order);
	}

	@Test
	public void testRecalculateOrderAndModifyPayments()
	{
		final DefaultOrderCancelPaymentServiceAdapter defaultOrderCancelPaymentServiceAdapter = new DefaultOrderCancelPaymentServiceAdapter()
		{
			@Override
			public BookingService lookupBookingService()
			{
				return bookingService;
			}
		};
		final BigDecimal totalRefundValue=BigDecimal.valueOf(100d);
		Mockito.when(bookingService.getTotalToRefund(order)).thenReturn(totalRefundValue);
		Mockito.when(bookingService.createRefundPaymentTransaction(order, totalRefundValue, order.getEntries()))
				.thenReturn(Boolean.TRUE);
		defaultOrderCancelPaymentServiceAdapter.recalculateOrderAndModifyPayments(order);
		Mockito.verify(bookingService, Mockito.times(1)).createRefundPaymentTransaction(order, totalRefundValue,
				order.getEntries());
	}
}
