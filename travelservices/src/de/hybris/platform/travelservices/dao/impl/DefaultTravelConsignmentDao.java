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

package de.hybris.platform.travelservices.dao.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.TravelConsignmentDao;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


public class DefaultTravelConsignmentDao extends DefaultGenericDao<ConsignmentModel> implements TravelConsignmentDao
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelConsignmentDao.class);

	private FlexibleSearchService flexibleSearchService;

	public DefaultTravelConsignmentDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public ConsignmentModel getConsignment(final WarehouseModel warehouseModel, final OrderModel order,
			final TravellerModel traveller)
	{

		final Map<String, Object> params = new HashMap<>();
		params.put(WarehouseModel._TYPECODE, warehouseModel);
		params.put(TravellerModel._TYPECODE, traveller);

		final StringBuilder sb = new StringBuilder();

		sb.append("select {c.pk} from {").append(OrderModel._TYPECODE).append(" as o join ").append(ConsignmentModel._TYPECODE)
				.append(" as c on {c.").append(ConsignmentModel.ORDER).append("} = {o.pk}} where {c.")
				.append(ConsignmentModel.TRAVELLER).append("} = ?").append(TravellerModel._TYPECODE).append(" and {c.")
				.append(ConsignmentModel.WAREHOUSE).append("} = ?").append(WarehouseModel._TYPECODE);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(sb.toString(), params);

		final SearchResult<ConsignmentModel> searchResults = flexibleSearchService.search(flexibleSearchQuery);

		if (CollectionUtils.isEmpty(searchResults.getResult()))
		{
			LOG.error("No consignments found for traveller: " + traveller.getPk() + ", warehouseModel: " + warehouseModel.getCode()
					+ ", order: " + order.getCode());
			return null;
		}
		if (searchResults.getResult().size() > 1)
		{
			LOG.error("More then one consignment found for traveller: " + traveller.getPk() + ", warehouseModel: "
					+ warehouseModel.getCode() + ", order: " + order.getCode());
			return null;
		}
		return searchResults.getResult().get(0);
	}

	@Override
	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
