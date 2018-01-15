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

import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;
import de.hybris.platform.travelservices.strategies.stock.StockReservationReleaseByEntryTypeStrategy;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation to handle release and reservation for transport based entries
 */
public class TransportStockReservationReleaseStrategy implements StockReservationReleaseByEntryTypeStrategy
{
	private Map<String, StockReservationCreationStrategy> transportStockReservationCreationStrategyMap;
	private static final String DEFAULT = "DEFAULT";

	@Override
	public List<StockReservationData> getStockInformationForOrderEntry(final AbstractOrderEntryModel entry)
	{

		final StockReservationCreationStrategy strategy = getTransportStockReservationCreationStrategyMap()
				.get(entry.getProduct().getClass().getSimpleName());

		return Objects.nonNull(strategy) ? strategy.create(entry)
				: getTransportStockReservationCreationStrategyMap().get(DEFAULT).create(entry);
	}

	/**
	 *
	 * @return the transportStockReservationCreationStrategyMap
	 */
	protected Map<String, StockReservationCreationStrategy> getTransportStockReservationCreationStrategyMap()
	{
		return transportStockReservationCreationStrategyMap;
	}

	/**
	 *
	 * @param transportStockReservationCreationStrategyMap
	 *           the transportStockReservationCreationStrategyMap to set
	 */
	@Required
	public void setTransportStockReservationCreationStrategyMap(
			final Map<String, StockReservationCreationStrategy> transportStockReservationCreationStrategyMap)
	{
		this.transportStockReservationCreationStrategyMap = transportStockReservationCreationStrategyMap;
	}


}
