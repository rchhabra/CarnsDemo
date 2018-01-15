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
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.services.TravelRestrictionService;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockStrategy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportEntryManageStockStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportEntryManageStockStrategyTest
{
	@InjectMocks
	TransportEntryManageStockStrategy transportEntryManageStockStrategy;

	@Mock
	private TravelRestrictionService travelRestrictionService;

	@Mock
	private DefaultTravelManageStockStrategy defaultTravelManageStockStrategy;

	@Before
	public void setUp() throws InsufficientStockLevelException
	{
		final Map<String, TravelManageStockStrategy> transportManageStockByProductTypeStrategyMap = new HashMap<>();
		transportManageStockByProductTypeStrategyMap.put("FareProductModel",
				defaultTravelManageStockStrategy);
		transportManageStockByProductTypeStrategyMap.put("DEFAULT", defaultTravelManageStockStrategy);
		transportEntryManageStockStrategy
				.setTransportManageStockByProductTypeStrategyMap(transportManageStockByProductTypeStrategyMap);
		Mockito.doNothing().when(defaultTravelManageStockStrategy).reserve(Matchers.any(AbstractOrderEntryModel.class));
	}

	@Test
	public void testReserve() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());
		Mockito.when(travelRestrictionService.getAddToCartCriteria(Matchers.any(ProductModel.class)))
				.thenReturn(AddToCartCriteriaType.PER_LEG);
		transportEntryManageStockStrategy.reserve(abstractOrderEntry);
		Mockito.verify(defaultTravelManageStockStrategy, Mockito.times(1)).reserve(Matchers.any(AbstractOrderEntryModel.class));
	}

	@Test
	public void testRelease() throws InsufficientStockLevelException
	{
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());
		Mockito.when(travelRestrictionService.getAddToCartCriteria(Matchers.any(ProductModel.class)))
				.thenReturn(AddToCartCriteriaType.PER_LEG);
		transportEntryManageStockStrategy.release(abstractOrderEntry);
		Mockito.verify(defaultTravelManageStockStrategy, Mockito.times(1)).release(Matchers.any(AbstractOrderEntryModel.class));
	}

}
