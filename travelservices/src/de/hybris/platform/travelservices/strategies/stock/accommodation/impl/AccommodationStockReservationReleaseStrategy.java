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

import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;
import de.hybris.platform.travelservices.strategies.stock.StockReservationReleaseByEntryTypeStrategy;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Concrete implementation to handle stock reservation and release for accommodation based entries according to their
 * instance type.
 */
public class AccommodationStockReservationReleaseStrategy implements StockReservationReleaseByEntryTypeStrategy
{

	private Map<String, StockReservationCreationStrategy> accommodationStockReservationCreationStrategyMap;
	private static final String DEFAULT = "DEFAULT";

	@Override
	public List<StockReservationData> getStockInformationForOrderEntry(final AbstractOrderEntryModel entry)
	{
		final StockReservationCreationStrategy strategy = getAccommodationStockReservationCreationStrategyMap()
				.get(entry.getProduct().getClass().getSimpleName());

		return Objects.nonNull(strategy) ? strategy.create(entry)
				: getAccommodationStockReservationCreationStrategyMap().get(DEFAULT).create(entry);

	}

	/**
	 * 
	 * @return the accommodationStockReservationCreationStrategyMap
	 */
	protected Map<String, StockReservationCreationStrategy> getAccommodationStockReservationCreationStrategyMap()
	{
		return accommodationStockReservationCreationStrategyMap;
	}

	/**
	 * 
	 * @param accommodationStockReservationCreationStrategyMap
	 *           the accommodationStockReservationCreationStrategyMap to set
	 */
	public void setAccommodationStockReservationCreationStrategyMap(
			final Map<String, StockReservationCreationStrategy> accommodationStockReservationCreationStrategyMap)
	{
		this.accommodationStockReservationCreationStrategyMap = accommodationStockReservationCreationStrategyMap;
	}

}
