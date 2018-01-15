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

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;

import java.util.Collections;
import java.util.List;


/**
 * Default implementation of {@link StockReservationCreationStrategy} that returns an empty collection of
 * stockReservationData for products that have a stockLevel against the default warehouse and have a FORCEINSTOCK
 * {@link InStockStatus}
 */
public class StockReservationForDefaultWarehouseCreationStrategy implements StockReservationCreationStrategy
{

	@Override
	public List<StockReservationData> create(final AbstractOrderEntryModel entry)
	{
		return Collections.emptyList();
	}

}
