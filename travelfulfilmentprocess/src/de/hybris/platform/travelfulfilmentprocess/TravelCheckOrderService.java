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

package de.hybris.platform.travelfulfilmentprocess;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Used by TravelCheckOrderAction, this service is designed to validate the order prior to running the fulfilment process.
 */
public interface TravelCheckOrderService
{
	/**
	 * Check boolean.
	 *
	 * @param order
	 * 		the order
	 *
	 * @return the boolean
	 */
	boolean check(OrderModel order);
}
