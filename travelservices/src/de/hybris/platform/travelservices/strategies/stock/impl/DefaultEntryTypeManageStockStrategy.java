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

package de.hybris.platform.travelservices.strategies.stock.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockByEntryTypeStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to reserve and release stocks for products belonging to default warehouse
 */
public class DefaultEntryTypeManageStockStrategy implements TravelManageStockByEntryTypeStrategy
{
	private StockService stockService;
	private WarehouseService warehouseService;

	private static final String DEFAULT_WAREHOUSE = "default";

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		getStockService().reserve(abstractOrderEntry.getProduct(), getWarehouseService().getWarehouseForCode(DEFAULT_WAREHOUSE),
				abstractOrderEntry.getQuantity().intValue(), null);
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		getStockService().release(abstractOrderEntry.getProduct(), getWarehouseService().getWarehouseForCode(DEFAULT_WAREHOUSE),
				abstractOrderEntry.getQuantity().intValue(), null);
	}

	/**
	 * @return the stockService
	 */
	protected StockService getStockService()
	{
		return stockService;
	}

	/**
	 * @param stockService
	 *           the stockService to set
	 */
	@Required
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

	/**
	 * @return the warehouseService
	 */
	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	/**
	 * @param warehouseService
	 *           the warehouseService to set
	 */
	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}


}
