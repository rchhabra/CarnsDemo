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

import de.hybris.platform.ordersplitting.impl.DefaultOrderSplittingService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Concrete implementation of {@link SplittingStrategy}. This class will be the only splitting strategy called by
 * {@link DefaultOrderSplittingService} and will return the final list of OrderEntryGroup to be used to create
 * consignments. This strategy basically behaves as a dispatcher, calling a set of strategies handling specific order
 * entry types, passing the whole entry set to each of them. Hence there should not be any intersection between any two
 * lists returned by the nested strategies.
 */
public class DefaultTravelSplittingStrategy implements SplittingStrategy
{

	private List<SplittingStrategy> strategiesByTypeList;

	/**
	 * Method calling the nested strategies
	 */
	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		final List<OrderEntryGroup> splittedList = new ArrayList<>();
		getStrategiesByTypeList().forEach(strategy -> splittedList.addAll(strategy.perform(orderEntryGroup)));
		return splittedList;
	}

	/**
	 * Method to set required fields against the created consignment model, after splitting. To be consistent the method
	 * calls the equivalent method for each nested strategy.
	 */
	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		getStrategiesByTypeList().forEach(strategy -> strategy.afterSplitting(group, createdOne));
	}

	/**
	 * Gets strategies by type list.
	 *
	 * @return the strategiesByTypeList
	 */
	protected List<SplittingStrategy> getStrategiesByTypeList()
	{
		return strategiesByTypeList;
	}

	/**
	 * Sets strategies by type list.
	 *
	 * @param strategiesByTypeList
	 * 		the strategiesByTypeList
	 */
	public void setStrategiesByTypeList(final List<SplittingStrategy> strategiesByTypeList)
	{
		this.strategiesByTypeList = strategiesByTypeList;
	}


}
