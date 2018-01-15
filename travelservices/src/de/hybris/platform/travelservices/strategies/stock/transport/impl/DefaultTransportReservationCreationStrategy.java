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
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.services.TravelRestrictionService;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;

import java.util.List;
import java.util.Map;


/**
 * Concrete implementation to handle release and reservation for product types with no specific strategy. This strategy
 * behaves as a dispatcher to call a specific strategy from a map according with the AddToCartCriteria
 */
public class DefaultTransportReservationCreationStrategy implements StockReservationCreationStrategy
{

	private TravelRestrictionService travelRestrictionService;
	private Map<AddToCartCriteriaType, StockReservationCreationStrategy> stockReservationCreationStrategyMap;


	@Override
	public List<StockReservationData> create(final AbstractOrderEntryModel entry)
	{
		final AddToCartCriteriaType addToCartCriteria = getTravelRestrictionService().getAddToCartCriteria(entry.getProduct());
		final StockReservationCreationStrategy strategy = getStockReservationCreationStrategyMap().get(addToCartCriteria);
		return strategy.create(entry);
	}

	/**
	 *
	 * @return the travelRestrictionService
	 */
	protected TravelRestrictionService getTravelRestrictionService()
	{
		return travelRestrictionService;
	}

	/**
	 *
	 * @param travelRestrictionService
	 *           the travelRestrictionService to set
	 */
	public void setTravelRestrictionService(final TravelRestrictionService travelRestrictionService)
	{
		this.travelRestrictionService = travelRestrictionService;
	}

	/**
	 *
	 * @return the stockReservationCreationStrategyMap
	 */
	protected Map<AddToCartCriteriaType, StockReservationCreationStrategy> getStockReservationCreationStrategyMap()
	{
		return stockReservationCreationStrategyMap;
	}

	/**
	 *
	 * @param stockReservationCreationStrategyMap
	 *           the stockReservationCreationStrategyMap to set
	 */
	public void setStockReservationCreationStrategyMap(
			final Map<AddToCartCriteriaType, StockReservationCreationStrategy> stockReservationCreationStrategyMap)
	{
		this.stockReservationCreationStrategyMap = stockReservationCreationStrategyMap;
	}


}
