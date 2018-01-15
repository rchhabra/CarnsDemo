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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;


/**
 * Default implementation of {@link TravelManageStockStrategy}
 */
public class DefaultTravelManageStockStrategy implements TravelManageStockStrategy
{

	private StockService stockService;

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		final ProductModel product = abstractOrderEntry.getProduct();
		if (abstractOrderEntry.getTravelOrderEntryInfo() == null)
		{
			return;
		}
		for (final TransportOfferingModel transportOffering : abstractOrderEntry.getTravelOrderEntryInfo()
				.getTransportOfferings())
		{
			final int qty = abstractOrderEntry.getQuantity().intValue();
			getStockService().reserve(product, transportOffering, qty, null);
		}
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		final ProductModel product = abstractOrderEntry.getProduct();
		if (null != abstractOrderEntry.getTravelOrderEntryInfo())
		{
			for (final TransportOfferingModel transportOffering : abstractOrderEntry.getTravelOrderEntryInfo()
					.getTransportOfferings())
			{
				final int qty = abstractOrderEntry.getQuantity().intValue();
				if (qty > 0)
				{
					getStockService().release(product, transportOffering, qty, null);
				}
			}
		}
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
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

}
