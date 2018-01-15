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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.fare.search.strategies.AncillaryAvailabilityStrategy;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link AncillaryAvailabilityStrategy}. This strategy is used to chech the availability of a
 * bundleTemplate with products with a PER_LEG addToCartCriteria. The bundleTemplate is available if the stock level of
 * every products is greater than 0.
 */
public class AncillaryPerLegAvailabilityStrategy implements AncillaryAvailabilityStrategy
{

	private static final Logger LOG = Logger.getLogger(AncillaryPerLegAvailabilityStrategy.class);
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;

	@Override
	public boolean checkIncludedAncillariesAvailability(final List<ProductData> productDataList, final int passengerNumber)
	{

		for (final ProductData productData : productDataList)
		{
			final ProductModel productModel = getProductService().getProductForCode(productData.getCode());
			try
			{
				final Long stockLevel = getCommerceStockService().getStockLevel(productModel, Collections.emptyList());
				if (stockLevel != null && stockLevel.intValue() < 1)
				{
					LOG.debug("Insufficient stock for Product (code: " + productModel.getCode() + ")");
					return false;
				}
			}
			catch (final StockLevelNotFoundException ex)
			{
				LOG.debug("Stocklevel not found for Product with code: " + productModel.getCode(), ex);
				return false;
			}
		}

		return true;
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
	 *           the productService to set
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
	 *           the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

}