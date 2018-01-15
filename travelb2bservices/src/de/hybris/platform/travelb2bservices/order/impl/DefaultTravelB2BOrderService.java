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

package de.hybris.platform.travelb2bservices.order.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelb2bservices.dao.B2BTravelOrderDao;
import de.hybris.platform.travelb2bservices.dao.PagedB2BTravelOrderDao;
import de.hybris.platform.travelb2bservices.order.TravelB2BOrderService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation for {@link TravelB2BOrderService}
 */
public class DefaultTravelB2BOrderService implements TravelB2BOrderService
{
	private PagedB2BTravelOrderDao<OrderModel> pagedB2BTravelOrderDao;
	private B2BTravelOrderDao b2bTravelOrderDao;

	@Override
	public SearchPageData<OrderModel> getPagedOrders(final PageableData pageableData, final List<String> unitCodes,
			final String email,
			final Date startingDate, final Date endingDate, final String costCenterUid, final String currency)
	{
		Assert.notNull(pageableData, "PageableData can not be null!");
		return getPagedB2BTravelOrderDao().findPagedOrders(unitCodes, email, startingDate, endingDate, costCenterUid, currency,
				pageableData);
	}

	@Override
	public Double findTotal(final List<String> unitCodes, final String email, final Date fromDate, final Date toDate,
			final String costCenter, final String currencyIso)
	{
		final List<OrderModel> allOrders = getB2bTravelOrderDao().findOrders(unitCodes, email, fromDate, toDate, costCenter,
				currencyIso);
		Double total = 0.0;
		for (final OrderModel order : allOrders)
		{
			total = Double.sum(total, Double.sum(order.getTotalPrice(), order.getTotalTax()));
		}

		return total;
	}

	/**
	 * Gets B2B travel order dao.
	 *
	 * @return the B2B travel order dao
	 */
	protected B2BTravelOrderDao getB2bTravelOrderDao()
	{
		return b2bTravelOrderDao;
	}

	/**
	 * Sets B2B travel order dao.
	 *
	 * @param b2bTravelOrderDao
	 * 		the B2B travel order dao
	 */
	@Required
	public void setB2bTravelOrderDao(final B2BTravelOrderDao b2bTravelOrderDao)
	{
		this.b2bTravelOrderDao = b2bTravelOrderDao;
	}


	/**
	 * Gets paged B2B travel order dao.
	 *
	 * @return the paged B2B travel order dao
	 */
	protected PagedB2BTravelOrderDao<OrderModel> getPagedB2BTravelOrderDao()
	{
		return pagedB2BTravelOrderDao;
	}

	/**
	 * Sets paged B2B travel order dao.
	 *
	 * @param pagedB2BTravelOrderDao
	 * 		the paged B2B travel order dao
	 */
	@Required
	public void setPagedB2BTravelOrderDao(final PagedB2BTravelOrderDao<OrderModel> pagedB2BTravelOrderDao)
	{
		this.pagedB2BTravelOrderDao = pagedB2BTravelOrderDao;
	}

}
