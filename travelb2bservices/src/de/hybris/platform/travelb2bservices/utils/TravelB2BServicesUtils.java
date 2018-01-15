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

package de.hybris.platform.travelb2bservices.utils;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.travelb2bservices.constants.Travelb2bservicesConstants;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * Travel B2B services utils.
 */
public final class TravelB2BServicesUtils
{
	
	private TravelB2BServicesUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Populate query parameters string.
	 *
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param query
	 * 		the query
	 * @param costCenterUid
	 * 		the cost center uid
	 * @param currency
	 * 		the currency
	 * @param queryParams
	 * 		the query params
	 *
	 * @return the string
	 */
	public static String populateQueryParameters(final String email, final Date fromDate, final Date toDate, final String query,
			final String costCenterUid, final CurrencyModel currency, final Map<String, Object> queryParams)
	{
		String newQuery = query;
		if (StringUtils.isNotEmpty(email))
		{
			newQuery = newQuery + Travelb2bservicesConstants.USER_RESTRICTIONS;
			queryParams.put("userId", email);
		}

		if (fromDate != null)
		{
			newQuery = newQuery + Travelb2bservicesConstants.FROM_DATE_RESTRICTION;
			queryParams.put("fromDate", fromDate);
		}

		if (toDate != null)
		{
			newQuery = newQuery + Travelb2bservicesConstants.TO_DATE_RESTRICTION;
			queryParams.put("toDate", toDate);
		}

		if (StringUtils.isNotEmpty(costCenterUid))
		{
			newQuery = newQuery + Travelb2bservicesConstants.COST_CENTER_RESTRICTION;
			queryParams.put("costCenterId", costCenterUid);
		}

		if (currency != null)
		{
			newQuery = newQuery + Travelb2bservicesConstants.CURRENCY_RESTRICTION;
			queryParams.put("currency", currency);
		}
		return newQuery;
	}

}
