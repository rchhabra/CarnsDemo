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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.strategies.RefundActionStrategy;

import java.math.BigDecimal;


/**
 * Strategy to calculate refund amount for default order entries
 */
public class StandardTotalRefundCalculationStrategy implements TotalRefundCalculationStrategy
{
	private RefundActionStrategy refundActionStrategy;

	@Override
	public BigDecimal getTotalToRefund(final OrderModel order)
	{
		return BigDecimal.valueOf(getRefundActionStrategy().applyStrategy(order, OrderEntryType.DEFAULT));
	}

	/**
	 * @return the refundActionStrategy
	 */
	public RefundActionStrategy getRefundActionStrategy()
	{
		return refundActionStrategy;
	}

	/**
	 * @param refundActionStrategy
	 *           the refundActionStrategy to set
	 */
	public void setRefundActionStrategy(final RefundActionStrategy refundActionStrategy)
	{
		this.refundActionStrategy = refundActionStrategy;
	}

}
