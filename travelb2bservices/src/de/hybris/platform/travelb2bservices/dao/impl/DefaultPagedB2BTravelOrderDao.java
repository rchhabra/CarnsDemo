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

import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelb2bservices.constants.Travelb2bservicesConstants;
import de.hybris.platform.travelb2bservices.dao.PagedB2BTravelOrderDao;
import de.hybris.platform.travelb2bservices.utils.TravelB2BServicesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Default Paged B2B Travel Order Dao.
 */
public class DefaultPagedB2BTravelOrderDao extends DefaultPagedGenericDao<OrderModel>
		implements PagedB2BTravelOrderDao<OrderModel>
{
	private static final String SORT_BY_DATE = " ORDER BY {o." + OrderModel.CREATIONTIME + "} ASC";

	private static final String SORT_BY_USER = " ORDER BY {o." + OrderModel.USER + "} ASC";

	private static final String SORT_BY_UNIT = " ORDER BY {o." + OrderModel.UNIT + "} ASC";

	private static final String SORT_BY_TOTAL = " ORDER BY " + Travelb2bservicesConstants.TOTAL_WITH_TAX + " DESC";

	private static final String SORT_BY_CODE = " ORDER BY {o." + OrderModel.CODE + "} ASC";

	private static final Map<String, String> SORT_MAP = getSortMap();

	private CommonI18NService commonI18NService;

	/**
	 * Instantiates a new Default paged b 2 b travel order dao.
	 *
	 * @param typeCode
	 * 		the type code
	 */
	public DefaultPagedB2BTravelOrderDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public SearchPageData<OrderModel> findPagedOrders(final List<String> unitCodes, final String email, final Date fromDate,
			final Date toDate, final String costCenterUid, final String currency, final PageableData pageableData)
	{
		String query = Travelb2bservicesConstants.FIND_ORDER_BY_UNIT;
		validateParameterNotNull(unitCodes, "unitCodes  must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("unitCodes", unitCodes);
		CurrencyModel currencyModel = null;
		if (StringUtils.isNotEmpty(currency))
		{
			currencyModel = getCommonI18NService().getCurrency(currency);
		}
		query = TravelB2BServicesUtils
				.populateQueryParameters(email, fromDate, toDate, query, costCenterUid, currencyModel, queryParams);
		final List<SortQueryData> sortQueries = getSortQueryData(query);
		return getPagedFlexibleSearchService().search(sortQueries, pageableData.getSort(), queryParams, pageableData);
	}

	/**
	 * this method retrieves the sort query based on the given parameter
	 *
	 * @param query
	 * 		the query
	 *
	 * @return List<SortQueryData> sort query data
	 */
	protected List<SortQueryData> getSortQueryData(final String query)
	{
		final List<SortQueryData> sortQueries = new ArrayList<SortQueryData>();
		SORT_MAP.forEach((k, v) -> sortQueries.add(createSortQueryData(k, query + SORT_MAP.get(k))));
		return sortQueries;
	}

	/**
	 * this method returns the sort map
	 *
	 * @return Map<String String>
	 */
	protected static Map<String, String> getSortMap()
	{
		final Map<String, String> sortMap = new HashMap<String, String>();
		sortMap.put(OrderModel.CREATIONTIME, SORT_BY_DATE);
		sortMap.put(OrderModel.USER, SORT_BY_USER);
		sortMap.put(OrderModel.UNIT, SORT_BY_UNIT);
		sortMap.put(OrderModel.TOTALPRICE, SORT_BY_TOTAL);
		sortMap.put(OrderModel.CODE, SORT_BY_CODE);
		return sortMap;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


}
