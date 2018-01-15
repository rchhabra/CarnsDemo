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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.services.TravelRestrictionService;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation to handle release and reservation for product types with no specific strategy. This strategy
 * behaves as a dispatcher to call a specific strategy from a map according with the AddToCartCriteria
 */
public class DefaultTransportProductManageStockStrategy implements TravelManageStockStrategy
{
	private Map<AddToCartCriteriaType, TravelManageStockStrategy> travelManageStockStrategyMap;
	private TravelRestrictionService travelRestrictionService;

	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		final AddToCartCriteriaType addToCartCriteria = getAddToCartCriteria(abstractOrderEntry.getProduct());
		final TravelManageStockStrategy strategy = getTravelManageStockStrategyMap().get(addToCartCriteria);
		strategy.reserve(abstractOrderEntry);
	}

	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		final AddToCartCriteriaType addToCartCriteria = getAddToCartCriteria(abstractOrderEntry.getProduct());
		final TravelManageStockStrategy strategy = getTravelManageStockStrategyMap().get(addToCartCriteria);
		strategy.release(abstractOrderEntry);
	}


	protected AddToCartCriteriaType getAddToCartCriteria(final ProductModel product)
	{
		return getTravelRestrictionService().getAddToCartCriteria(product);
	}

	protected Map<AddToCartCriteriaType, TravelManageStockStrategy> getTravelManageStockStrategyMap()
	{
		return travelManageStockStrategyMap;
	}

	@Required
	public void setTravelManageStockStrategyMap(
			final Map<AddToCartCriteriaType, TravelManageStockStrategy> travelManageStockStrategyMap)
	{
		this.travelManageStockStrategyMap = travelManageStockStrategyMap;
	}

	protected TravelRestrictionService getTravelRestrictionService()
	{
		return travelRestrictionService;
	}

	@Required
	public void setTravelRestrictionService(final TravelRestrictionService travelRestrictionService)
	{
		this.travelRestrictionService = travelRestrictionService;
	}


}
