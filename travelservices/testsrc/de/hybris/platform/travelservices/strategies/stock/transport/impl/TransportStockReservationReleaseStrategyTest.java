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
import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.services.TravelRestrictionService;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;
import de.hybris.platform.travelservices.strategies.stock.impl.DefaultStockReservationCreationStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * unit test for {@link TransportStockReservationReleaseStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportStockReservationReleaseStrategyTest
{
	@InjectMocks
	TransportStockReservationReleaseStrategy transportStockReservationReleaseStrategy;

	@Mock
	private TravelRestrictionService travelRestrictionService;

	@Mock
	private DefaultStockReservationCreationStrategy defaultStockReservationCreationStrategy;

	private final List<StockReservationData> stockReservationDatas = new ArrayList<>();

	@Before
	public void setUp()
	{
		final Map<String, StockReservationCreationStrategy> transportStockReservationCreationStrategyMap = new HashMap<>();
		transportStockReservationCreationStrategyMap.put("FareProductModel",
				defaultStockReservationCreationStrategy);
		transportStockReservationCreationStrategyMap.put("DEFAULT", defaultStockReservationCreationStrategy);
		transportStockReservationReleaseStrategy
				.setTransportStockReservationCreationStrategyMap(transportStockReservationCreationStrategyMap);
		Mockito.when(defaultStockReservationCreationStrategy.create(Matchers.any(AbstractOrderEntryModel.class)))
				.thenReturn(stockReservationDatas);

	}

	@Test
	public void testGetStockInformationForOrderEntry()
	{
		Mockito.when(travelRestrictionService.getAddToCartCriteria(Matchers.any(ProductModel.class)))
				.thenReturn(AddToCartCriteriaType.PER_LEG);
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setProduct(new ProductModel());

		transportStockReservationReleaseStrategy.getStockInformationForOrderEntry(abstractOrderEntry);
		Mockito.verify(defaultStockReservationCreationStrategy, Mockito.times(1))
				.create(Matchers.any(AbstractOrderEntryModel.class));
	}

}
