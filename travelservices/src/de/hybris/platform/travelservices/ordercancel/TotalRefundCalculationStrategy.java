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

package de.hybris.platform.travelservices.ordercancel;

import de.hybris.platform.core.model.order.OrderModel;

import java.math.BigDecimal;


/**
 * Strategy to calculate the total amount to be refunded when cancelling the order
 */
public interface TotalRefundCalculationStrategy
{

	/**
	 * Returns the BigDecimal corresponding to the total amount to be refunded when cancelling the order
	 *
	 * @param order
	 * 		as the orderModel to be cancelled
	 * @return BigDecimal corresponding to the total amount to be refunded
	 */
	BigDecimal getTotalToRefund(OrderModel order);

}
