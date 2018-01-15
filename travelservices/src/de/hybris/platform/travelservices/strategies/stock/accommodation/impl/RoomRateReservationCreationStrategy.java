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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Concrete implementation to handle reservation and release for room rates in amendment scenario
 */
public class RoomRateReservationCreationStrategy implements StockReservationCreationStrategy
{

	@Override
	public List<StockReservationData> create(final AbstractOrderEntryModel entry)
	{
		final List<StockReservationData> stockDatas = new ArrayList<>();
		stockDatas.addAll(createStockDataForRoomRate(entry.getProduct(), entry.getQuantity(), entry.getEntryGroup()));
		stockDatas.addAll(createStockDataForAccommodation(entry.getProduct(), entry.getQuantity(), entry.getEntryGroup(),
				entry.getAccommodationOrderEntryInfo().getDates()));
		return stockDatas;
	}

	/**
	 * Creates stock reservation data for room rate
	 *
	 * @param roomRateProduct
	 * 		the room rate product
	 * @param quantity
	 * 		the quantity
	 * @param orderEntryGroup
	 * 		the order entry group
	 * @return list list
	 */
	protected List<StockReservationData> createStockDataForRoomRate(final ProductModel roomRateProduct, final Long quantity,
			final AbstractOrderEntryGroupModel orderEntryGroup)
	{
		final AccommodationOrderEntryGroupModel group = (AccommodationOrderEntryGroupModel) orderEntryGroup;
		final StockReservationData stock = new StockReservationData();
		stock.setProduct(roomRateProduct);
		stock.setQuantity(quantity.intValue());
		stock.setWarehouse(group.getAccommodationOffering());
		return Collections.singletonList(stock);
	}

	/**
	 * Creates stock reservation data for accommodation, one per day with quantity = 1 if the room rate associated with
	 * the entry has quantity greater than 0. Otherwise a stockData with quantity 0 will be created to trigger release
	 * logic in {@TravelCommerceStockService}
	 *
	 * @param product
	 * @param quantity
	 * @param orderEntryGroup
	 * @param dates
	 * @return
	 */
	private List<StockReservationData> createStockDataForAccommodation(final ProductModel product, final Long quantity,
			final AbstractOrderEntryGroupModel orderEntryGroup, final List<Date> dates)
	{
		final List<StockReservationData> stocks = new ArrayList<>();
		final AccommodationOrderEntryGroupModel group = (AccommodationOrderEntryGroupModel) orderEntryGroup;
		dates.forEach(date -> {
			final StockReservationData stock = new StockReservationData();
			stock.setProduct(group.getAccommodation());
			stock.setQuantity(quantity > 0 ? 1 : 0);
			stock.setWarehouse(group.getAccommodationOffering());
			stock.setDate(date);
			stocks.add(stock);
		});
		return stocks;
	}
}
