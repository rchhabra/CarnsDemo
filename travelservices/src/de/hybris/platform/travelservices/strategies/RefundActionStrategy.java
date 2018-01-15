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

package de.hybris.platform.travelservices.strategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;


/**
 * Strategy for refund actions.
 */
public interface RefundActionStrategy
{
	/**
	 * Method to apply business logic on the total amount.
	 * 
	 * @deprecated Deprecated since version 3.0.
	 * @param order
	 *           the order
	 * @return total refund amount
	 */
	@Deprecated
	double applyStrategy(OrderModel order);

	/**
	 * Method to apply business logic on the total amount.
	 *
	 * @param order
	 *           the order
	 * @return total refund amount
	 */
	double applyStrategy(OrderModel order, OrderEntryType orderEntryType);

}
