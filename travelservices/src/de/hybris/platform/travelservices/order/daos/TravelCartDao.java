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

package de.hybris.platform.travelservices.order.daos;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;


/**
 * Data Access Object oriented on Cart
 */
public interface TravelCartDao
{
	/**
	 * Finds all carts for given original order reference
	 *
	 * @param originalOrderModel
	 * 		- original order
	 * @return list of all the carts associated with original order
	 */
	List<CartModel> findCartsForOriginalOrder(final OrderModel originalOrderModel);

}
