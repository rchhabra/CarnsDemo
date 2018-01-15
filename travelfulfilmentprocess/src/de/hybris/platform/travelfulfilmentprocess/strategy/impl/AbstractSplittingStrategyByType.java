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

package de.hybris.platform.travelfulfilmentprocess.strategy.impl;

import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Abstract Splitting strategy to be extended to handle specific order entries
 */
public abstract class AbstractSplittingStrategyByType implements SplittingStrategy
{
	private List<SplittingStrategy> strategiesList;

	/**
	 * Each implementation of AbstractSplittingStrategyByType takes the whole list of OrderEntryGroup and applies a set
	 * of strategies to build the result. The nested strategies use the output of the previous one as an input
	 */
	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		List<OrderEntryGroup> orderEntryGroupsByType = new ArrayList<>();
		orderEntryGroupsByType.addAll(orderEntryGroup);
		if (CollectionUtils.isEmpty(getStrategiesList()))
		{
			return Collections.emptyList();
		}
		for (final SplittingStrategy strategy : getStrategiesList())
		{
			orderEntryGroupsByType = strategy.perform(orderEntryGroupsByType);
		}
		return orderEntryGroupsByType;
	}

	/**
	 * Gets strategies list.
	 *
	 * @return the strategiesList
	 */
	protected List<SplittingStrategy> getStrategiesList()
	{
		return strategiesList;
	}

	/**
	 * Sets strategies list.
	 *
	 * @param strategiesList
	 * 		the strategiesList to set
	 */
	public void setStrategiesList(final List<SplittingStrategy> strategiesList)
	{
		this.strategiesList = strategiesList;
	}


}
