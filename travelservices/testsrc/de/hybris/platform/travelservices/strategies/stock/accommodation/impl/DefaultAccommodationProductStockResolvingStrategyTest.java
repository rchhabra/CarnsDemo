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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for DefaultAccommodationProductStockResolvingStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationProductStockResolvingStrategyTest
{
	@InjectMocks
	private DefaultAccommodationProductStockResolvingStrategy strategy;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private AccommodationOrderEntryGroupModel entryGroup;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private RoomRateProductModel roomRateProduct;

	@Test
	public void testGetStock()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("05/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(
				commerceStockService.getStockForDate(Matchers.any(ProductModel.class), Matchers.any(Date.class), Matchers.anyList()))
				.thenReturn(10);
		assertEquals(Long.valueOf(10), strategy.getStock(orderEntry));
	}

	@Test
	public void testGetStockOnSameDates()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		assertEquals(null, strategy.getStock(orderEntry));
	}
}
