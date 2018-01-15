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

package de.hybris.platform.travelservices.strategies.stock.transport.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * unit test for {@link TransportStockResolvingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportStockResolvingStrategyTest
{

	@InjectMocks
	TransportStockResolvingStrategy transportStockResolvingStrategy;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Test
	public void testGetStock()
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		travelOrderEntryInfoModel.setTransportOfferings(Stream.of(transportOffering).collect(Collectors.toList()));
		abstractOrderEntry.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
		Mockito.when(commerceStockService.getStockLevelQuantity(Matchers.any(ProductModel.class), Matchers.anyList()))
				.thenReturn(10l);
		Assert.assertEquals(10l, transportStockResolvingStrategy.getStock(abstractOrderEntry).longValue());

	}

	@Test
	public void testGetStockForEmpty()
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());
		Mockito.when(commerceStockService.getStockLevelQuantity(Matchers.any(ProductModel.class), Matchers.anyList()))
				.thenReturn(0l);
		Assert.assertEquals(0l, transportStockResolvingStrategy.getStock(abstractOrderEntry).longValue());

	}

}
