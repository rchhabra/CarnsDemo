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

package de.hybris.platform.travelb2bservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelb2bservices.constants.Travelb2bservicesConstants;
import de.hybris.platform.travelb2bservices.dao.B2BTravelOrderDao;
import de.hybris.platform.travelb2bservices.utils.TravelB2BServicesUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default B2B Travel Order Dao.
 */
public class DefaultB2BTravelOrderDao extends DefaultGenericDao<OrderModel> implements B2BTravelOrderDao
{
	private CommonI18NService commonI18NService;

	public DefaultB2BTravelOrderDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<OrderModel> findOrders(final List<String> unitCodes, final String email, final Date fromDate, final Date toDate,
			final String costCenter, final String currencyIso)
	{
		String query = Travelb2bservicesConstants.FIND_ORDER_BY_UNIT;
		validateParameterNotNull(unitCodes, "unitCodes  must not be null");
		validateParameterNotNull(currencyIso, "currencyIso  must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("unitCodes", unitCodes);
		final CurrencyModel currencyModel = getCommonI18NService().getCurrency(currencyIso);
		query = TravelB2BServicesUtils.populateQueryParameters(email, fromDate, toDate, query, costCenter, currencyModel,
				queryParams);
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query, queryParams);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(flexibleSearchQuery);
		return result.getResult();
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}
