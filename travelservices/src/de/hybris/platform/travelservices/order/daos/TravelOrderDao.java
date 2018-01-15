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

package de.hybris.platform.travelservices.order.daos;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;


/**
 * Travel specific Data Access Object oriented on orders and order entries
 */
public interface TravelOrderDao
{

	/**
	 * Returns the list of all the orders with the given orderCode in the baseStore
	 *
	 * @param orderCode
	 *           as the orderCode
	 * @param baseStore
	 *           as the baseStore
	 *
	 * @return a list of OrderModel
	 */
	List<OrderModel> findOrdersByCode(String orderCode, BaseStoreModel baseStore);
}
