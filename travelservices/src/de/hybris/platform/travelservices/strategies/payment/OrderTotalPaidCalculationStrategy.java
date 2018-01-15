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

package de.hybris.platform.travelservices.strategies.payment;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.math.BigDecimal;


/**
 * Interface for strategies calculating the total amount paid given an order.
 */
public interface OrderTotalPaidCalculationStrategy
{
	/**
	 * Calculates the total paid according with the transactions against the given order
	 *
	 * @param abstractOrder
	 * @return
	 */
	BigDecimal calculate(AbstractOrderModel abstractOrder);
}
