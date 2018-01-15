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

package de.hybris.platform.travelservices.customer.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.customer.dao.TravelCustomerAccountDao;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultTravelCustomerAccountDao extends DefaultCustomerAccountDao implements TravelCustomerAccountDao
{

	private static final String FIND_ORDERS_BY_ORIGINAL_ORDER_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {"
			+ OrderModel.ORIGINALORDER + "} = ?originalOrder AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE
			+ "} = ?store AND {" + OrderModel.STATUS + "} != ?status";

	private static final String FIND_ORDERS_BY_CUSTOMER_QUERY = "select {" + OrderModel.PK + "},{" + OrderModel.CREATIONTIME
			+ "},{" + OrderModel.CODE + "} from {" + OrderModel._TYPECODE + " as o join " + OrderUserAccountMappingModel._TYPECODE
			+ " as l on {l:" + OrderUserAccountMappingModel.ORDERCODE + "}= {o:" + OrderModel.CODE + "} join " + UserModel._TYPECODE
			+ " as c on {l:" + OrderUserAccountMappingModel.USER + "}={c.pk} } where {o:" + OrderModel.VERSIONID
			+ "} IS NULL and {l:" + OrderUserAccountMappingModel.USER
			+ "} =?" + OrderUserAccountMappingModel.USER;

	private static final String FIND_CUSTOMER_ORDER_LINK = "select {" + OrderUserAccountMappingModel.PK + "} from {"
			+ OrderUserAccountMappingModel._TYPECODE + " as l} where {l:" + OrderUserAccountMappingModel.USER + "}=?"
			+ OrderUserAccountMappingModel.USER + " and {l:" + OrderUserAccountMappingModel.ORDERCODE + "}=?"
			+ OrderUserAccountMappingModel.ORDERCODE;

	private static final String FIND_SAVED_SEARCHES_BY_PK = "select {" + SavedSearchModel._TYPECODE + "." + SavedSearchModel.PK
			+ "} from {" + SavedSearchModel._TYPECODE + "}  where {" + SavedSearchModel._TYPECODE + "." + SavedSearchModel.PK
			+ "}=?savedSearchID";

	@Override
	public OrderModel findOrderModelByOriginalOrderCode(final String bookingReference, final BaseStoreModel baseStoreModel)
	{
		validateParameterNotNull(bookingReference, "Code must not be null");
		validateParameterNotNull(baseStoreModel, "Store must not be null");

		final OrderModel originalOrder = findOrderByCodeAndStore(bookingReference, baseStoreModel);
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("originalOrder", originalOrder);
		queryParams.put("store", baseStoreModel);
		queryParams.put("status", OrderStatus.AMENDMENTINPROGRESS);
		return getFlexibleSearchService()
				.searchUnique(new FlexibleSearchQuery(FIND_ORDERS_BY_ORIGINAL_ORDER_CODE_STORE_QUERY, queryParams));
	}

	@Override
	public List<OrderModel> findOrdersByOrderUserMapping(final CustomerModel customerModel)
	{
		validateParameterNotNull(customerModel, "customerModel must not be null");
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("user", customerModel);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDERS_BY_CUSTOMER_QUERY, queryParams);
		final SearchResult<OrderModel> searchResult = getFlexibleSearchService().search(fsq);
		if (searchResult != null)
		{
			return searchResult.getResult();
		}
		return Collections.emptyList();
	}

	@Override
	public OrderUserAccountMappingModel findOrderUserMapping(final String orderCode, final CustomerModel customerModel)
	{
		validateParameterNotNull(orderCode, "Order must not be null");
		validateParameterNotNull(customerModel, "Customer must not be null");
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("user", customerModel);
		queryParams.put("orderCode", orderCode);
		final SearchResult<OrderUserAccountMappingModel> searchResult = getFlexibleSearchService().search(FIND_CUSTOMER_ORDER_LINK,
				queryParams);
		return searchResult.getCount() > 0 ? searchResult.getResult().get(0) : null;
	}

	@Override
	public SavedSearchModel findSavedSearch(final String savedSearchID)
	{
		validateParameterNotNull(savedSearchID, "Search ID must not be null");
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("savedSearchID", savedSearchID);
		return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(FIND_SAVED_SEARCHES_BY_PK, queryParams));
	}

}
