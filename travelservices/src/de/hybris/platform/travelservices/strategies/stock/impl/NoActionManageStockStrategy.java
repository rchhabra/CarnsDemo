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
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;


/**
 * Default implementation of {@link TravelManageStockStrategy}
 */
public class NoActionManageStockStrategy implements TravelManageStockStrategy
{

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry)
	{
		// Method deliberately empty - no action required if this strategy is called
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		// Method deliberately empty - no action required if this strategy is called
	}

}
