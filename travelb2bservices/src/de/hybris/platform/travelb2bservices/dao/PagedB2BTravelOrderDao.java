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

import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;


/**
 * The interface Paged B2B travel order dao.
 */
public interface PagedB2BTravelOrderDao<M> extends PagedGenericDao<M>
{

	/**
	 * Find paged orders search page data.
	 *
	 * @param unitCodes
	 * 		the unit codes
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenterUid
	 * 		the cost center uid
	 * @param currency
	 * 		the currency
	 * @param pageableData
	 * 		the pageable data
	 *
	 * @return a list of paginated orders for the parameters provided
	 */
	SearchPageData<OrderModel> findPagedOrders(List<String> unitCodes, String email, Date fromDate, Date toDate,
			String costCenterUid, String currency, PageableData pageableData);

}
