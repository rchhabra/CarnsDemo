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

package de.hybris.platform.travelfacades.strategies;

import de.hybris.platform.commercefacades.product.data.ProductData;


/**
 * The interface Add accommodation to cart strategy.
 */
public interface AddAccommodationToCartStrategy
{

	/**
	 * Can add accommodation to cart boolean.
	 *
	 * @param product
	 * 		the product
	 * @return the boolean
	 */
	boolean canAddAccommodationToCart(ProductData product);
}
