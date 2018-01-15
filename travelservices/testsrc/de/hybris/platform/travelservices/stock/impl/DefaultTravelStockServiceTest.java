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

package de.hybris.platform.travelservices.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.travelservices.dao.TravelStockLevelDao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * The type Default travel stock service test.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelStockServiceTest
{

	@InjectMocks
	private DefaultTravelStockService travelStockService;

	@Mock
	private TravelStockLevelDao travelStockLevelDao;

	/**
	 * Test get stock level for date.
	 */
	@Test
	public void testGetStockLevelForDate()
	{
		final ProductModel product = new ProductModel();
		product.setCode("ProductCode");
		final Collection<WarehouseModel> warehouses = Collections.singletonList(new WarehouseModel());
		final Date date = new Date();
		final StockLevelModel stockLevelModel = new StockLevelModel();
		BDDMockito.given(travelStockLevelDao.findStockLevel(product.getCode(), warehouses, date)).willReturn(stockLevelModel);
		final StockLevelModel stockLevelModelAct = travelStockService.getStockLevelForDate(product, warehouses, date);
		Assert.assertEquals(stockLevelModel, stockLevelModelAct);

		final StockLevelModel stockLevelModelAct1 = travelStockService.getStockLevelForDate(null, warehouses, date);
		Assert.assertNull(stockLevelModelAct1);
	}
}
