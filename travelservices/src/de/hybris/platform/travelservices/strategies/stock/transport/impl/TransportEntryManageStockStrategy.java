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
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockByEntryTypeStrategy;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation to handle transport type entries
 */
public class TransportEntryManageStockStrategy implements TravelManageStockByEntryTypeStrategy
{
	private Map<String, TravelManageStockStrategy> transportManageStockByProductTypeStrategyMap;
	private static final String DEFAULT = "DEFAULT";

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		final TravelManageStockStrategy strategy = getTransportManageStockByProductTypeStrategyMap()
				.get(abstractOrderEntry.getProduct().getClass().getSimpleName());
		if (Objects.nonNull(strategy))
		{
			strategy.reserve(abstractOrderEntry);
		}
		else
		{
			getTransportManageStockByProductTypeStrategyMap().get(DEFAULT).reserve(abstractOrderEntry);
		}
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		final TravelManageStockStrategy strategy = getTransportManageStockByProductTypeStrategyMap()
				.get(abstractOrderEntry.getProduct().getClass().getSimpleName());
		if (Objects.nonNull(strategy))
		{
			strategy.release(abstractOrderEntry);
		}
		else
		{
			getTransportManageStockByProductTypeStrategyMap().get(DEFAULT).release(abstractOrderEntry);
		}
	}

	/**
	 *
	 * @return the transportManageStockByProductTypeStrategyMap
	 */
	protected Map<String, TravelManageStockStrategy> getTransportManageStockByProductTypeStrategyMap()
	{
		return transportManageStockByProductTypeStrategyMap;
	}

	/**
	 *
	 * @param transportManageStockByProductTypeStrategyMap
	 *           the transportManageStockByProductTypeStrategyMap
	 */
	@Required
	public void setTransportManageStockByProductTypeStrategyMap(
			final Map<String, TravelManageStockStrategy> transportManageStockByProductTypeStrategyMap)
	{
		this.transportManageStockByProductTypeStrategyMap = transportManageStockByProductTypeStrategyMap;
	}


}
