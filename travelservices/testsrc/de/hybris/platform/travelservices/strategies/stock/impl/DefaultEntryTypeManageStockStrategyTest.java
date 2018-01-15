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

package de.hybris.platform.travelservices.strategies.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEntryTypeManageStockStrategyTest
{
	@InjectMocks
	private DefaultEntryTypeManageStockStrategy entryTypeManageStockStrategy;

	@Mock
	private StockService stockService;

	@Mock
	private WarehouseService warehouseService;

	private AbstractOrderEntryModel abstractOrderEntry;

	@Before
	public void setUp() throws InsufficientStockLevelException
	{
		Mockito.when(warehouseService.getWarehouseForCode(Matchers.anyString())).thenReturn(new WarehouseModel());
		abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setQuantity(2l);

	}

	@Test
	public void testReserve() throws InsufficientStockLevelException
	{
		Mockito.doNothing().when(stockService).reserve(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class),
				Matchers.anyInt(), Matchers.any());
		entryTypeManageStockStrategy.reserve(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(1)).reserve(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class),
				Matchers.anyInt(), Matchers.any());
	}

	@Test(expected = InsufficientStockLevelException.class)
	public void testReserveForInsufficientStockLevelException() throws InsufficientStockLevelException
	{
		Mockito.doThrow(new InsufficientStockLevelException("InsufficientStockLevelException")).when(stockService)
				.reserve(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class), Matchers.anyInt(), Matchers.any());
		entryTypeManageStockStrategy.reserve(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(1)).reserve(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class),
				Matchers.anyInt(), Matchers.any());
	}


	@Test
	public void testRelease()
	{
		Mockito.doNothing().when(stockService).release(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class),
				Matchers.anyInt(), Matchers.any());
		entryTypeManageStockStrategy.release(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(1)).release(Matchers.any(ProductModel.class), Matchers.any(WarehouseModel.class),
				Matchers.anyInt(), Matchers.any());
	}
}
