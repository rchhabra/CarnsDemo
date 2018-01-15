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

package de.hybris.platform.travelservices.strategies.stock.accommodation.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockByEntryTypeStrategy;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.Map;
import java.util.Objects;


/**
 * Concrete implementation to handle accommodation type entries Each accommodation based product will be managed
 * according its type in Hybris item model
 */
public class AccommodationEntryManageStockStrategy implements TravelManageStockByEntryTypeStrategy
{

	private Map<String, TravelManageStockStrategy> accommodationManageStockByProductTypeStrategyMap;
	private static final String DEFAULT = "DEFAULT";

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		final TravelManageStockStrategy strategy = getAccommodationManageStockByProductTypeStrategyMap()
				.get(abstractOrderEntry.getProduct().getClass().getSimpleName());
		if (Objects.nonNull(strategy))
		{
			strategy.reserve(abstractOrderEntry);
		}
		else
		{
			getAccommodationManageStockByProductTypeStrategyMap().get(DEFAULT).reserve(abstractOrderEntry);
		}
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		final TravelManageStockStrategy strategy = getAccommodationManageStockByProductTypeStrategyMap()
				.get(abstractOrderEntry.getProduct().getClass().getSimpleName());
		if (Objects.nonNull(strategy))
		{
			strategy.release(abstractOrderEntry);
		}
		else
		{
			getAccommodationManageStockByProductTypeStrategyMap().get(DEFAULT).release(abstractOrderEntry);
		}
	}

	/**
	 * 
	 * @return the accommodationManageStockByProductTypeStrategyMap
	 */
	protected Map<String, TravelManageStockStrategy> getAccommodationManageStockByProductTypeStrategyMap()
	{
		return accommodationManageStockByProductTypeStrategyMap;
	}

	/**
	 * 
	 * @param accommodationManageStockByProductTypeStrategyMap
	 *           the accommodationManageStockByProductTypeStrategyMap to set
	 */
	public void setAccommodationManageStockByProductTypeStrategyMap(
			final Map<String, TravelManageStockStrategy> accommodationManageStockByProductTypeStrategyMap)
	{
		this.accommodationManageStockByProductTypeStrategyMap = accommodationManageStockByProductTypeStrategyMap;
	}



}
