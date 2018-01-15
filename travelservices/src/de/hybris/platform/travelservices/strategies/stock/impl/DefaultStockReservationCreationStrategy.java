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

import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;

import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of {@link StockReservationCreationStrategy} to create a stockReservationData for product with
 * stockLevel against a transportOffering.
 */
public class DefaultStockReservationCreationStrategy implements StockReservationCreationStrategy
{

	@Override
	public List<StockReservationData> create(final AbstractOrderEntryModel entry)
	{
		final List<StockReservationData> stockReservations = new ArrayList<>();

		entry.getTravelOrderEntryInfo().getTransportOfferings().forEach(to -> {
			final StockReservationData stock = new StockReservationData();
			stock.setProduct(entry.getProduct());
			stock.setWarehouse(to);
			stock.setQuantity(entry.getQuantity().intValue());
			stockReservations.add(stock);
		});

		return stockReservations;
	}

}
