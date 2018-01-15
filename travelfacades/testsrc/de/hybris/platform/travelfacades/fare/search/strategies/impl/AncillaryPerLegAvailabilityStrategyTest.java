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

package de.hybris.platform.travelfacades.fare.search.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link AncillaryPerLegAvailabilityStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AncillaryPerLegAvailabilityStrategyTest
{
	@InjectMocks
	AncillaryPerLegAvailabilityStrategy ancillaryPerLegAvailabilityStrategy;
	@Mock
	private ProductService productService;
	@Mock
	private TravelCommerceStockService commerceStockService;

	final String TEST_PRODUCT_CODE_A = "TEST_PRODUCT_CODE_A";
	final String TEST_PRODUCT_CODE_B = "TEST_PRODUCT_CODE_B";
	final String TEST_PRODUCT_CODE_C = "TEST_PRODUCT_CODE_C";
	final String TEST_PRODUCT_CODE_D = "TEST_PRODUCT_CODE_D";
	private ProductData pdA;
	private ProductData pdB;
	private ProductData pdC;
	private ProductData pdD;

	@Before
	public void setUp()
	{
		pdA = new ProductData();
		pdA.setCode(TEST_PRODUCT_CODE_A);

		pdB = new ProductData();
		pdB.setCode(TEST_PRODUCT_CODE_B);

		pdC = new ProductData();
		pdC.setCode(TEST_PRODUCT_CODE_C);

		pdD = new ProductData();
		pdD.setCode(TEST_PRODUCT_CODE_D);

		final ProductModel pdmA = Mockito.mock(ProductModel.class);
		final ProductModel pdmB = Mockito.mock(ProductModel.class);
		final ProductModel pdmC = Mockito.mock(ProductModel.class);
		final ProductModel pdmD = Mockito.mock(ProductModel.class);
		when(productService.getProductForCode(TEST_PRODUCT_CODE_A)).thenReturn(pdmA);
		when(productService.getProductForCode(TEST_PRODUCT_CODE_B)).thenReturn(pdmB);
		when(productService.getProductForCode(TEST_PRODUCT_CODE_C)).thenReturn(pdmC);
		when(productService.getProductForCode(TEST_PRODUCT_CODE_D)).thenReturn(pdmD);

		when(commerceStockService.getStockLevel(pdmA, Collections.emptyList())).thenReturn(null);
		when(commerceStockService.getStockLevel(pdmB, Collections.emptyList())).thenReturn(new Long(2));
		when(commerceStockService.getStockLevel(pdmC, Collections.emptyList())).thenReturn(new Long(0));
		when(commerceStockService.getStockLevel(pdmD, Collections.emptyList()))
				.thenThrow(new StockLevelNotFoundException("Exception Found"));
	}

	@Test
	public void testCheckIncludedAncillariesAvailabilityWithNullStockLevel()
	{
		final List<ProductData> productDataList = new ArrayList<>();
		productDataList.add(pdA);

		Assert.assertTrue(ancillaryPerLegAvailabilityStrategy.checkIncludedAncillariesAvailability(productDataList, 0));
	}

	@Test
	public void testCheckIncludedAncillariesAvailabilityWithStockLevel()
	{
		final List<ProductData> productDataList = new ArrayList<>();
		productDataList.add(pdB);

		Assert.assertTrue(ancillaryPerLegAvailabilityStrategy.checkIncludedAncillariesAvailability(productDataList, 0));
	}

	@Test
	public void testCheckIncludedAncillariesAvailabilityWithZeroStockLevel()
	{
		final List<ProductData> productDataList = new ArrayList<>();
		productDataList.add(pdC);

		Assert.assertFalse(ancillaryPerLegAvailabilityStrategy.checkIncludedAncillariesAvailability(productDataList, 0));
	}

	@Test
	public void testCheckIncludedAncillariesAvailabilityWithStockLevelNotFoundException()
	{
		final List<ProductData> productDataList = new ArrayList<>();
		productDataList.add(pdD);

		Assert.assertFalse(ancillaryPerLegAvailabilityStrategy.checkIncludedAncillariesAvailability(productDataList, 0));
	}

}
