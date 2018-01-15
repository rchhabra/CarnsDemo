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

package de.hybris.platform.travelfacades.strategies.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.travelfacades.strategies.ProductsSortStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Test for the Strategy {@link ProductsSortStrategy}, for implementation class {@link LowestFareStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LowestFareStrategyTest
{
	private static final double DELTA = 0.001;

	private LowestFareStrategy lowestFareStrategy;

	/**
	 * Initializes the strategy.
	 */
	@Before
	public void setup()
	{
		lowestFareStrategy = new LowestFareStrategy();
	}

	/**
	 * Testing Products List with multiple products and price information.
	 */
	@Test
	public void testFindLowestPricedProductForEmptyList()
	{
		lowestFareStrategy.applyStrategy(Collections.emptyList());
	}

	/**
	 * Testing Products List with multiple products and price information.
	 */
	@Test
	public void testFindLowestPricedProduct()
	{
		final List<ProductData> productDataList = new ArrayList<ProductData>();
		productDataList.add(createProductDataWithPrice(20));
		productDataList.add(createProductData());
		productDataList.add(createProductDataWithPrice(10));
		productDataList.add(createProductDataWithPrice(40));
		productDataList.add(createProductDataWithPrice(5));
		productDataList.add(null);
		productDataList.add(null);
		lowestFareStrategy.applyStrategy(productDataList);
		Assert.assertEquals(5, productDataList.get(0).getPrice().getValue().doubleValue(), DELTA);
	}

	/**
	 * Testing Products list with only one product.
	 */
	@Test
	public void testFindLowestPricedProductForOnlyOneProduct()
	{
		final List<ProductData> productDataList = new ArrayList<ProductData>();
		productDataList.add(createProductDataWithPrice(20));

		lowestFareStrategy.applyStrategy(productDataList);
		Assert.assertEquals(20, productDataList.get(0).getPrice().getValue().doubleValue(), DELTA);
	}

	/**
	 * Testing Products with no price information, these will appear at the top of the list.
	 */
	@Test
	public void testProductListWithNoPrices()
	{
		final List<ProductData> productDataList = new ArrayList<ProductData>();
		productDataList.add(createProductDataWithPrice(20));
		productDataList.add(createProductData());

		lowestFareStrategy.applyStrategy(productDataList);
		Assert.assertEquals(20, productDataList.get(0).getPrice().getValue().doubleValue(), DELTA);
	}

	private ProductData createProductDataWithPrice(final double price)
	{
		final ProductData productData = new ProductData();
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(price));
		productData.setPrice(priceData);
		return productData;
	}

	private ProductData createProductData()
	{
		final ProductData productData = new ProductData();
		return productData;
	}
}
