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
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravelManageStockStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelManageStockStrategyTest
{
	@InjectMocks
	DefaultTravelManageStockStrategy defaultTravelManageStockStrategy;

	@Mock
	private StockService stockService;

	@Test
	public void testReserveForEmptyTravelOrderEntryInfo() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		defaultTravelManageStockStrategy.reserve(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(0)).reserve(Matchers.any(ProductModel.class),
				Matchers.any(TransportOfferingModel.class), Matchers.anyInt(), Matchers.anyString());
	}

	@Test
	public void testReserve() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		travelOrderEntryInfoModel.setTransportOfferings(Stream.of(transportOffering).collect(Collectors.toList()));
		abstractOrderEntry.setQuantity(10l);
		abstractOrderEntry.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
		Mockito.doNothing().when(stockService).reserve(Matchers.any(ProductModel.class), Matchers.any(TransportOfferingModel.class),
				Matchers.anyInt(), Matchers.anyString());
		defaultTravelManageStockStrategy.reserve(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(1)).reserve(Matchers.any(ProductModel.class),
				Matchers.any(TransportOfferingModel.class), Matchers.anyInt(), Matchers.anyString());
	}

	@Test
	public void testReleaseForEmptyTravelOrderEntryInfo() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		defaultTravelManageStockStrategy.release(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(0)).release(Matchers.any(ProductModel.class),
				Matchers.any(TransportOfferingModel.class), Matchers.anyInt(), Matchers.anyString());
	}

	@Test
	public void testRelease() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();

		abstractOrderEntry.setProduct(new ProductModel());
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		travelOrderEntryInfoModel.setTransportOfferings(Stream.of(transportOffering).collect(Collectors.toList()));
		abstractOrderEntry.setQuantity(10l);
		abstractOrderEntry.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
		Mockito.doNothing().when(stockService).release(Matchers.any(ProductModel.class), Matchers.any(TransportOfferingModel.class),
				Matchers.anyInt(), Matchers.anyString());
		defaultTravelManageStockStrategy.release(abstractOrderEntry);
		Mockito.verify(stockService, Mockito.times(1)).release(Matchers.any(ProductModel.class),
				Matchers.any(TransportOfferingModel.class), Matchers.anyInt(), Matchers.anyString());
	}

}
