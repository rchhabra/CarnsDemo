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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;

import java.util.Map;
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
 *
 * Unit Test for AccommodationStockReservationReleaseStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationStockReservationReleaseStrategyTest
{
	private static final String DEFAULT = "DEFAULT";

	@InjectMocks
	private AccommodationStockReservationReleaseStrategy strategy;
	@Mock
	private Map<String, StockReservationCreationStrategy> accommodationStockReservationCreationStrategyMap;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private RoomRateProductModel roomRateProduct;
	@Mock
	private RoomRateReservationCreationStrategy roomRateReservationCreationStrategy;
	@Mock
	private StockReservationData stockReservationData;
	@Mock
	private DefaultAccommodationReservationCreationStrategy defaultAccommodationReservationCreationStrategy;

	@Test
	public void testGetStockInformationForOrderEntry()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationStockReservationCreationStrategyMap.get(Matchers.anyString()))
				.thenReturn(roomRateReservationCreationStrategy);
		Mockito.when(roomRateReservationCreationStrategy.create(orderEntry))
				.thenReturn(Stream.of(stockReservationData).collect(Collectors.toList()));
		assertNotNull(strategy.getStockInformationForOrderEntry(orderEntry));
	}

	@Test
	public void testGetStockInformationForOrderEntryWithDefaultStrategy()
	{
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(accommodationStockReservationCreationStrategyMap.get(Matchers.anyString()))
				.thenReturn(null);
		Mockito.when(accommodationStockReservationCreationStrategyMap.get(DEFAULT))
				.thenReturn(defaultAccommodationReservationCreationStrategy);
		Mockito.when(defaultAccommodationReservationCreationStrategy.create(orderEntry))
				.thenReturn(Stream.of(stockReservationData).collect(Collectors.toList()));
		assertNotNull(strategy.getStockInformationForOrderEntry(orderEntry));
	}

}
