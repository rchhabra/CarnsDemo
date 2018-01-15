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
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Concrete implementation to handle reservation and release for accommodation products not covered by specific
 * strategies in amendment scenario.
 */
public class DefaultAccommodationReservationCreationStrategy implements StockReservationCreationStrategy
{

	@Override
	public List<StockReservationData> create(final AbstractOrderEntryModel entry)
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entry.getEntryGroup();
		Date date = accommodationEntryGroup.getStartingDate();
		final Date endingDate = accommodationEntryGroup.getEndingDate();
		final List<StockReservationData> stocks = new ArrayList<>();
		while (!TravelDateUtils.isSameDate(date, endingDate))
		{
			final StockReservationData stock = new StockReservationData();
			stock.setProduct(entry.getProduct());
			stock.setQuantity(entry.getQuantity().intValue());
			stock.setWarehouse(accommodationEntryGroup.getAccommodationOffering());
			stock.setDate(date);
			stocks.add(stock);
			date = TravelDateUtils.addDays(date, 1);
		}
		return stocks;
	}

}
