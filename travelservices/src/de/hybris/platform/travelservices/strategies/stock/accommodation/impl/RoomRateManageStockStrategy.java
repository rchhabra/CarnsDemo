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
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Class to manage stock for {@link RoomRateProductModel} and the {@link AccommodationModel} associated
 */
public class RoomRateManageStockStrategy implements TravelManageStockStrategy
{
	private StockService stockService;
	private TravelCommerceStockService commerceStockService;

	/**
	 * For each entry this method reserve stock both for the room rate product associated with the entry and the relative
	 * accommodation
	 */
	@Override
	public void reserve(final AbstractOrderEntryModel abstractOrderEntry) throws InsufficientStockLevelException
	{
		reserveStockForRoomRate(abstractOrderEntry.getProduct(), abstractOrderEntry.getQuantity(),
				abstractOrderEntry.getEntryGroup());

		reserveStockForAccommodation(abstractOrderEntry.getEntryGroup(),
				abstractOrderEntry.getAccommodationOrderEntryInfo().getDates());

	}

	/**
	 * For each entry this method release stock both for the room rate product associated with the entry and the relative
	 * accommodation
	 */
	@Override
	public void release(final AbstractOrderEntryModel abstractOrderEntry)
	{
		final int qty = abstractOrderEntry.getQuantity().intValue();
		if (qty > 0)
		{
			releaseStockForRoomRate(abstractOrderEntry.getProduct(), abstractOrderEntry.getQuantity(),
					abstractOrderEntry.getEntryGroup());

			releaseStockForAccommodation(abstractOrderEntry.getEntryGroup(),
					abstractOrderEntry.getAccommodationOrderEntryInfo().getDates());
		}
	}

	/**
	 * Standard reservation for room rate products - no date
	 *
	 * @param roomRateProduct the room rate product
	 * @param quantity        the quantity
	 * @param entryGroup      the entry group
	 * @throws InsufficientStockLevelException the insufficient stock level exception
	 */
	protected void reserveStockForRoomRate(final ProductModel roomRateProduct, final Long quantity,
			final AbstractOrderEntryGroupModel entryGroup) throws InsufficientStockLevelException
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entryGroup;
		getStockService().reserve(roomRateProduct, accommodationEntryGroup.getAccommodationOffering(), quantity.intValue(), null);
	}

	/**
	 * Reservation by date for accommodation
	 *
	 * @param entryGroup the entry group
	 * @param dates      the dates
	 * @throws InsufficientStockLevelException the insufficient stock level exception
	 */
	protected void reserveStockForAccommodation(final AbstractOrderEntryGroupModel entryGroup, final List<Date> dates)
			throws InsufficientStockLevelException
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entryGroup;
		for (final Date date : dates)
		{
			getCommerceStockService().reservePerDateProduct(accommodationEntryGroup.getAccommodation(), date, 1,
					Collections.singletonList(accommodationEntryGroup.getAccommodationOffering()));
		}
	}

	/**
	 * Standard release for room rate product - no date
	 *
	 * @param roomRateProduct the room rate product
	 * @param quantity        the quantity
	 * @param entryGroup      the entry group
	 */
	protected void releaseStockForRoomRate(final ProductModel roomRateProduct, final Long quantity,
			final AbstractOrderEntryGroupModel entryGroup)
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entryGroup;
		getStockService().release(roomRateProduct, accommodationEntryGroup.getAccommodationOffering(), quantity.intValue(), null);
	}

	/**
	 * Release by date for accommodation
	 *
	 * @param entryGroup the entry group
	 * @param dates      the dates
	 */
	protected void releaseStockForAccommodation(final AbstractOrderEntryGroupModel entryGroup, final List<Date> dates)
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entryGroup;
		for (final Date date : dates)
		{
			getCommerceStockService().releasePerDateProduct(accommodationEntryGroup.getAccommodation(), date, 1,
					Collections.singletonList(accommodationEntryGroup.getAccommodationOffering()));
		}
	}

	/**
	 * Gets stock service.
	 *
	 * @return the stockService
	 */
	protected StockService getStockService()
	{
		return stockService;
	}

	/**
	 * Sets stock service.
	 *
	 * @param stockService the stockService to set
	 */
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

	/**
	 * Gets commerce stock service.
	 *
	 * @return commerceStockService commerce stock service
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * Sets commerce stock service.
	 *
	 * @param commerceStockService the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}



}
