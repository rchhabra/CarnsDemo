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

package de.hybris.platform.travelservices.order.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.order.daos.TravelCartDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DAO responsible for finding entries related to Cart
 */
public class DefaultTravelCartDao extends DefaultGenericDao<CartModel> implements TravelCartDao
{

	/**
	 * Default constructor of the Dao
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultTravelCartDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<CartModel> findCartsForOriginalOrder(final OrderModel originalOrderModel)
	{
		validateParameterNotNull(originalOrderModel, "Original Order Model must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(CartModel.ORIGINALORDER, originalOrderModel);
		return find(params);
	}
}
