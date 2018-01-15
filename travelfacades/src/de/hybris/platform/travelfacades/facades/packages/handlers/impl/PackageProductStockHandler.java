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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.packages.handlers.StandardPackageProductHandler;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link StandardPackageProductHandler} interface. This handler is responsible to populate the
 * stockData of the productData for each PackageProductData. If a product is not available the stock will be 0; if it is available
 * the stock will be > 0; if it is FORCE_IN_STOCK the stock will be null.
 */
public class PackageProductStockHandler implements StandardPackageProductHandler
{
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private WarehouseService warehouseService;

	private static final String DEFAULT_WAREHOUSE = "default";

	@Override
	public void handle(final BundleTemplateData bundleTemplate, final List<PackageProductData> packageProductDataList)
	{
		packageProductDataList.forEach(packageProductData ->
		{
			ProductModel productModel = getProductService().getProductForCode(packageProductData.getProduct().getCode());

			Long stockLevelQuantity = getCommerceStockService().getStockLevelQuantity(productModel,
					Collections.singleton(getWarehouseService().getWarehouseForCode(DEFAULT_WAREHOUSE)));
			final StockData stockData = new StockData();
			stockData.setStockLevel(stockLevelQuantity);
			packageProductData.getProduct().setStock(stockData);
		});
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 * 		the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 * 		the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the warehouseService
	 */
	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	/**
	 * @param warehouseService
	 * 		the warehouseService to set
	 */
	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

}
