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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.OrderUserAccountMappingDao;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link OrderUserAccountMappingDao}
 */
public class DefaultOrderUserAccountMappingDao extends DefaultGenericDao<OrderUserAccountMappingModel>
		implements OrderUserAccountMappingDao
{
	private static String FIND_BY_ORDER_AND_USER = "select {ouam." + OrderUserAccountMappingModel.PK + "} from {"
			+ OrderUserAccountMappingModel._TYPECODE + " as ouam join " + UserModel._TYPECODE + " as um on {um." + UserModel.PK
			+ "}={ouam." + OrderUserAccountMappingModel.USER + "}} where {um." + UserModel.PK + "} = ?userPk and {ouam."
			+ OrderUserAccountMappingModel.ORDERCODE + "} = ?orderCode";

	public DefaultOrderUserAccountMappingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<OrderUserAccountMappingModel> findMappings(final UserModel user, final AbstractOrderModel order)
	{

		validateParameterNotNull(user, "User must not be null!");
		validateParameterNotNull(order, "Order must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderCode", order.getCode());
		params.put("userPk", user.getPk());

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_ORDER_AND_USER, params);
		final SearchResult<OrderUserAccountMappingModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult.getResult();
		}

		return Collections.emptyList();
	}

}
