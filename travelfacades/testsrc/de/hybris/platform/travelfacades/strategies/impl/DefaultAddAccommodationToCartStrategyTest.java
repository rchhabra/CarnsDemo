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
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.enums.ProductType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAddAccommodationToCartStrategyTest
{
	@Mock
	private ProductService productService;

	@Mock
	private ProductModel productModel;

	@InjectMocks
	private final DefaultAddAccommodationToCartStrategy defaultAddAccommodationToCartStrategy = new DefaultAddAccommodationToCartStrategy();

	@Test
	public void nonAccommodationProductCannotBeAddedToCartTest()
	{
		final ProductData productData = new ProductData();
		final String productCode = "testProductCode";
		productData.setCode(productCode);
		Mockito.when(productService.getProductForCode(productCode)).thenReturn(productModel);
		final boolean canAddProductToCart = defaultAddAccommodationToCartStrategy.canAddAccommodationToCart(productData);
		Assert.assertFalse(canAddProductToCart);
	}

	@Test
	public void accommodationProductCanBeAddedToCartTest()
	{
		final ProductData productData = new ProductData();
		final String productCode = "testProductCode";
		productData.setCode(productCode);
		Mockito.when(productService.getProductForCode(productCode)).thenReturn(productModel);
		Mockito.when(productModel.getProductType()).thenReturn(ProductType.ACCOMMODATION);
		final boolean canAddAccommodationToCart = defaultAddAccommodationToCartStrategy.canAddAccommodationToCart(productData);
		Assert.assertTrue(canAddAccommodationToCart);
	}

}