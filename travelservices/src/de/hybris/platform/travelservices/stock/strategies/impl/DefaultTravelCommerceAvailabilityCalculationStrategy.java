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
*/

package de.hybris.platform.travelservices.stock.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.stock.strategies.impl.DefaultCommerceAvailabilityCalculationStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy to evaluate the available quantity in the Stock Level
 */
public class DefaultTravelCommerceAvailabilityCalculationStrategy extends DefaultCommerceAvailabilityCalculationStrategy
{
	@Override
	public Long calculateAvailability(final Collection<StockLevelModel> stockLevels)
	{
		validateParameterNotNull(stockLevels, "stock levels cannot be null");

		if (CollectionUtils.isEmpty(stockLevels))
		{
			return 0L;
		}

		if (stockLevels.stream().allMatch(stockLevel -> InStockStatus.FORCEINSTOCK.equals(stockLevel.getInStockStatus())))
		{
			// If all stock levels are flagged as FORCEINSTOCK then return null to indicate in stock
			return null;
		}

		if (stockLevels.stream().filter(stockLevel -> InStockStatus.FORCEOUTOFSTOCK.equals(stockLevel.getInStockStatus())).findAny()
				.isPresent())
		{
			// At least one stock level is FORCEOUTOFSTOCK then overall availability is 0
			return 0L;
		}
		final Optional<StockLevelModel> stocklevelModel = stockLevels.stream()
				.min((s1, s2) -> Long.valueOf(getAvailableToSellQuantity(s1)).compareTo(getAvailableToSellQuantity(s2)));
		return Objects.nonNull(stocklevelModel) && stocklevelModel.isPresent() ? getAvailableToSellQuantity(stocklevelModel.get())
				: 0L;

	}
}
