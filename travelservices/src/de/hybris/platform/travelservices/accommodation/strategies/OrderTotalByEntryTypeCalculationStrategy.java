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

package de.hybris.platform.travelservices.accommodation.strategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;


/**
 * Strategy that calculates the total amount paid for a certain orderEntryType.
 */
public interface OrderTotalByEntryTypeCalculationStrategy
{

	/**
	 * Returns the total paid amount for a given orderEntryType for the given order
	 *
	 * @param orderModel
	 *           as the orderModel
	 * @param orderEntryType
	 *           as the orderEntryType to be used to calculate the total paid amount
	 * @return the total paid amount for the given orderEntryType
	 */
	BigDecimal calculate(OrderModel orderModel, OrderEntryType orderEntryType);

}
