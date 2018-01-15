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
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.stock.impl.DefaultTravelCommerceStockService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEntryTypeStockResolvingStrategyTest
{
	@InjectMocks
	private final DefaultEntryTypeStockResolvingStrategy entryTypeStockResolvingStrategy = new DefaultEntryTypeStockResolvingStrategy();

	@Test
	public void testGetStock()
	{
		final TravelCommerceStockService commerceStockService = Mockito.spy(new DefaultTravelCommerceStockService());
		entryTypeStockResolvingStrategy.setCommerceStockService(commerceStockService);
		final AbstractOrderEntryModel entry=new AbstractOrderEntryModel();
		final ProductModel product=new ProductModel();
		entry.setProduct(product);
		Mockito.doReturn(100l).when(commerceStockService).getStockLevelQuantity(product, Collections.emptyList());
		final long stock = entryTypeStockResolvingStrategy.getStock(entry);
		Assert.assertEquals(100l, stock);
	}

}
