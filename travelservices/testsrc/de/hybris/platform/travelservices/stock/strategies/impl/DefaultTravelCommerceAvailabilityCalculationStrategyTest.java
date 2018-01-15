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
*/

package de.hybris.platform.travelservices.stock.strategies.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 * 
 */
@UnitTest
public class DefaultTravelCommerceAvailabilityCalculationStrategyTest
{
	private DefaultTravelCommerceAvailabilityCalculationStrategy travelCommerceAvailabilityCalculationStrategy;

	private Collection<StockLevelModel> stockLevels;


	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelCommerceAvailabilityCalculationStrategy = new DefaultTravelCommerceAvailabilityCalculationStrategy();

	}

	/**
	 * Test method to calculate availability when stock level collection is having all stock level object as Force In
	 * Stock Status
	 */
	@Test
	public void testAllStockLevelsAreForceInStockStatus()
	{
		stockLevels = new ArrayList<>();
		final StockLevelModel stockLevel1 = new StockLevelModel();
		stockLevel1.setInStockStatus(InStockStatus.FORCEINSTOCK);
		final StockLevelModel stockLevel2 = new StockLevelModel();
		stockLevel2.setInStockStatus(InStockStatus.FORCEINSTOCK);
		stockLevels.add(stockLevel1);
		stockLevels.add(stockLevel2);

		final Long stockLevel = travelCommerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels);

		Assert.assertNull(stockLevel);

	}

	/**
	 * Test method to calculate availability when stock level collection is having one stock level object as Force Out Of
	 * Stock Status
	 */
	@Test
	public void testWhenOneStockLevelHaveForceOutOfStockStatus()
	{
		stockLevels = new ArrayList<>();
		final StockLevelModel stockLevel1 = new StockLevelModel();
		stockLevel1.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		final StockLevelModel stockLevel2 = new StockLevelModel();
		stockLevel2.setInStockStatus(InStockStatus.FORCEINSTOCK);
		stockLevels.add(stockLevel1);
		stockLevels.add(stockLevel2);

		final Long stockLevel = travelCommerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels);

		assertEquals(Long.valueOf(0L), stockLevel);

	}

	/**
	 * Test method to calculate availability when stock level collection is having more than one stock level object
	 */
	@Test
	public void testWhenAllStockLevelsArePresent()
	{
		stockLevels = new ArrayList<>();
		final StockLevelModel stockLevel1 = new StockLevelModel();
		stockLevel1.setInStockStatus(InStockStatus.NOTSPECIFIED);
		stockLevel1.setAvailable(5);
		stockLevel1.setReserved(1);
		stockLevel1.setOverSelling(1);
		final StockLevelModel stockLevel2 = new StockLevelModel();
		stockLevel2.setInStockStatus(InStockStatus.FORCEINSTOCK);
		stockLevel2.setAvailable(10);
		stockLevel2.setReserved(2);
		stockLevel2.setOverSelling(2);
		stockLevels.add(stockLevel1);
		stockLevels.add(stockLevel2);

		final Long stockLevel = travelCommerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels);

		assertEquals(Long.valueOf(5L), stockLevel);

	}

	/**
	 * Test method to calculate availability when stock level collection is having one stock level object
	 */
	@Test
	public void testWhenOneStockLevelsIsPresent()
	{
		stockLevels = new ArrayList<>();
		final StockLevelModel stockLevel1 = new StockLevelModel();
		stockLevel1.setInStockStatus(InStockStatus.NOTSPECIFIED);
		stockLevel1.setAvailable(6);
		stockLevel1.setReserved(1);
		stockLevel1.setOverSelling(2);
		stockLevels.add(stockLevel1);

		final Long stockLevel = travelCommerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels);

		assertEquals(Long.valueOf(7L), stockLevel);

	}

}
