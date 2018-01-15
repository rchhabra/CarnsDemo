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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for AccommodationEntryManageStockStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationEntryManageStockStrategyTest
{
	private static final String DEFAULT = "DEFAULT";
	@InjectMocks
	private AccommodationEntryManageStockStrategy strategy;
	@Mock
	private Map<String, TravelManageStockStrategy> accommodationManageStockByProductTypeStrategyMap;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private RoomRateProductModel roomRateProduct;
	@Mock
	private RoomRateManageStockStrategy roomRateManageStockStrategy;
	@Mock
	private DefaultAccommodationProductManageStockStrategy defaultAccommodationProductManageStockStrategy;

	@Test
	public void testReserve()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(Matchers.anyString()))
				.thenReturn(roomRateManageStockStrategy);
		try
		{
			Mockito.doNothing().when(roomRateManageStockStrategy).reserve(orderEntry);
			strategy.reserve(orderEntry);
		}
		catch (final InsufficientStockLevelException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testReserveWithDefaultStrategy()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(Matchers.anyString()))
				.thenReturn(null);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(DEFAULT))
				.thenReturn(defaultAccommodationProductManageStockStrategy);
		try
		{
			Mockito.doNothing().when(defaultAccommodationProductManageStockStrategy).reserve(orderEntry);
			strategy.reserve(orderEntry);
		}
		catch (final InsufficientStockLevelException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testRelease()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(Matchers.anyString()))
				.thenReturn(roomRateManageStockStrategy);
			Mockito.doNothing().when(roomRateManageStockStrategy).release(orderEntry);
			strategy.release(orderEntry);
	}

	@Test
	public void testReleaseWithDefaultStrategy()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(Matchers.anyString())).thenReturn(null);
		Mockito.when(accommodationManageStockByProductTypeStrategyMap.get(DEFAULT))
				.thenReturn(defaultAccommodationProductManageStockStrategy);
			Mockito.doNothing().when(defaultAccommodationProductManageStockStrategy).release(orderEntry);
			strategy.release(orderEntry);
	}
}
