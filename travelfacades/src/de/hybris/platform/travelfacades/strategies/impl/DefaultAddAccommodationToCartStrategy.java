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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.strategies.AddAccommodationToCartStrategy;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default add accommodation to cart strategy.
 */
public class DefaultAddAccommodationToCartStrategy implements AddAccommodationToCartStrategy
{

	private ProductService productService;

	@Override
	public boolean canAddAccommodationToCart(final ProductData productData)
	{
		final ProductModel product = getProductService().getProductForCode(productData.getCode());
		if (ProductType.ACCOMMODATION.equals(product.getProductType()) || product instanceof AccommodationModel)
		{
			return true;
		}
		return false;
	}

	/**
	 * Gets product service.
	 *
	 * @return the product service
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets product service.
	 *
	 * @param productService
	 * 		the product service
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

}
