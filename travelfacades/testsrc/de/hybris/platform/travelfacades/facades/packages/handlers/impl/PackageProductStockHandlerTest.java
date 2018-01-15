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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageProductStockHandlerTest
{
	@InjectMocks
	PackageProductStockHandler packageProductStockHandler;

	@Mock
	ProductService productService;

	@Mock
	TravelCommerceStockService commerceStockService;

	@Mock
	WarehouseService warehouseService;

	@Test
	public void testHandle()
	{
		final BundleTemplateData bundleTemplate = new BundleTemplateData();
		final List<PackageProductData> packageProductDataList = new ArrayList<>();

		final PackageProductData packageProductData = new PackageProductData();
		final ProductData product = new ProductData();
		product.setCode("productCode");
		packageProductData.setProduct(product);
		packageProductDataList.add(packageProductData);

		final ProductModel productModel = new ProductModel();
		Mockito.when(productService.getProductForCode(packageProductData.getProduct().getCode())).thenReturn(productModel);

		final WarehouseModel wareHouse=new WarehouseModel();
		Mockito.when(warehouseService.getWarehouseForCode(Mockito.anyString())).thenReturn(wareHouse);
		Mockito.when(
				commerceStockService.getStockLevelQuantity(Mockito.any(ProductModel.class), Mockito.anyListOf(WarehouseModel.class)))
				.thenReturn(10l);

		packageProductStockHandler.handle(bundleTemplate, packageProductDataList);
		Assert.assertEquals(Long.valueOf(10L), packageProductData.getProduct().getStock().getStockLevel());
	}
}
