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
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByProductType;

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
 * Unit Test for AccommodationStockResolvingStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationStockResolvingStrategyTest
{
	private static final String DEFAULT = "DEFAULT";

	@InjectMocks
	private AccommodationStockResolvingStrategy strategy;
	@Mock
	private Map<String, StockResolvingStrategyByProductType> strategyByProductTypeMap;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private RoomRateProductModel roomRateProduct;
	@Mock
	private RoomRateStockResolvingStrategy roomRateStockResolvingStrategy;
	@Mock
	private DefaultAccommodationProductStockResolvingStrategy defaultAccommodationProductStockResolvingStrategy;

	@Test
	public void testGetStock()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(strategyByProductTypeMap.get(Matchers.anyString())).thenReturn(roomRateStockResolvingStrategy);
		Mockito.when(roomRateStockResolvingStrategy.getStock(orderEntry)).thenReturn(50L);
		assertEquals(Long.valueOf(50), strategy.getStock(orderEntry));
	}

	@Test
	public void testGetStockWithDefaultStrategy()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(strategyByProductTypeMap.get(Matchers.anyString())).thenReturn(null);
		Mockito.when(strategyByProductTypeMap.get(DEFAULT)).thenReturn(defaultAccommodationProductStockResolvingStrategy);
		Mockito.when(defaultAccommodationProductStockResolvingStrategy.getStock(orderEntry)).thenReturn(50L);
		assertEquals(Long.valueOf(50), strategy.getStock(orderEntry));
	}
}
