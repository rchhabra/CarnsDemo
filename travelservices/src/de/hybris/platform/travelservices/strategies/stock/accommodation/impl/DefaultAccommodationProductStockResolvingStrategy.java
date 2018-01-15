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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByProductType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Strategy retrieving stock per date for accommodation products
 */
public class DefaultAccommodationProductStockResolvingStrategy implements StockResolvingStrategyByProductType
{

	private TravelCommerceStockService commerceStockService;

	@Override
	public Long getStock(final AbstractOrderEntryModel entry)
	{
		final AccommodationOrderEntryGroupModel entryGroup = (AccommodationOrderEntryGroupModel) entry.getEntryGroup();
		final List<Long> stockLevels = new ArrayList<>();
		Date date = entryGroup.getStartingDate();
		while (!TravelDateUtils.isSameDate(date, entryGroup.getEndingDate()))
		{
			collectAvailability(stockLevels, entry.getProduct(), entryGroup, date);
			date = TravelDateUtils.addDays(date, 1);
		}

		return CollectionUtils.isNotEmpty(stockLevels)
				? stockLevels.stream().mapToLong(Long::longValue).min().getAsLong() : null;
	}



	protected void collectAvailability(final List<Long> stockLevels, final ProductModel product,
			final AccommodationOrderEntryGroupModel entryGroup, final Date date)
	{
		stockLevels.add(Long.valueOf(getCommerceStockService().getStockForDate(product, date,
				Collections.singletonList(entryGroup.getAccommodationOffering()))));
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

}
