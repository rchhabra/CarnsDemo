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

package de.hybris.platform.travelb2bservices.order;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;


/**
 * Interface for Travel B2B order service.
 */
public interface TravelB2BOrderService
{
	/**
	 * Gets paged orders.
	 *
	 * @param pageableData
	 * 		the pageable data
	 * @param unitCodes
	 * 		the unit codes
	 * @param email
	 * 		the email
	 * @param startingDate
	 * 		the starting date
	 * @param endingDate
	 * 		the ending date
	 * @param costCenterUid
	 * 		the cost center uid
	 * @param currency
	 * 		the currency
	 *
	 * @return a list of paginated orders for the given parameters
	 */
	SearchPageData<OrderModel> getPagedOrders(PageableData pageableData, List<String> unitCodes, String email, Date startingDate,
			Date endingDate, String costCenterUid, String currency);

	/**
	 * Find total double.
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
	 * @return the total for all the orders int the search
	 */
	Double findTotal(List<String> unitCodes, String email, Date fromDate, Date toDate, String costCenter, String currencyIso);
}
