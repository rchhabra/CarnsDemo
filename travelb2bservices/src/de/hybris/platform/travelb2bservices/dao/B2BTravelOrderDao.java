/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelb2bservices.dao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.Date;
import java.util.List;


/**
 * The interface B2B travel order dao.
 */
public interface B2BTravelOrderDao extends Dao
{

	/**
	 * Find orders list.
	 *
	 * @param unitCodes
	 * 		the unit codes
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenter
	 * 		the cost center
	 * @param currencyIso
	 * 		the currency iso
	 *
	 * @return a list of orders for the given parameters
	 */
	List<OrderModel> findOrders(List<String> unitCodes, String email, Date fromDate, Date toDate, String costCenter,
			String currencyIso);

}
