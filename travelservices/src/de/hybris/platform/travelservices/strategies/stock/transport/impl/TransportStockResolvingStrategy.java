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
 *
 */

package de.hybris.platform.travelservices.strategies.stock.transport.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByEntryType;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Concrete implementation resolving stock level quantity for products belonging to transport entry
 */
public class TransportStockResolvingStrategy implements StockResolvingStrategyByEntryType
{
	private TravelCommerceStockService commerceStockService;

	@Override
	public Long getStock(final AbstractOrderEntryModel entry)
	{
		final Collection<WarehouseModel> warehouses = Objects.nonNull(entry.getTravelOrderEntryInfo())
				? entry.getTravelOrderEntryInfo().getTransportOfferings().stream().collect(Collectors.toList())
				: Collections.emptyList();

		return getCommerceStockService().getStockLevelQuantity(entry.getProduct(), warehouses);

	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

}
