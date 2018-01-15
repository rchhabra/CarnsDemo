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

package de.hybris.platform.travelservices.ordercancel.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;


/**
 * Default implementation of {@link OrderCancelPaymentServiceAdapter}
 */
public class DefaultOrderCancelPaymentServiceAdapter implements OrderCancelPaymentServiceAdapter
{

	private BookingService bookingService;

	@Override
	public void recalculateOrderAndModifyPayments(final OrderModel order)
	{
		final BigDecimal totalToRefund = getBookingService().getTotalToRefund(order);
		getBookingService().createRefundPaymentTransaction(order, totalToRefund.abs(), order.getEntries());
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		if (bookingService == null)
		{
			bookingService = lookupBookingService();
		}
		return bookingService;
	}

	public BookingService lookupBookingService()
	{
		throw new UnsupportedOperationException("lookup cart method injection");
	}

}
