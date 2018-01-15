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

import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;


/**
 * Strategy to validate the add bundle to cart
 */
public interface AddBundleToCartValidationStrategy
{
	/**
	 * Validates the add bundle to cart
	 *
	 * @param addBundleToCartRequestData
	 * 		as the add bundle to cart request data
	 *
	 * @return the add to cart response data
	 */
	AddToCartResponseData validate(final AddBundleToCartRequestData addBundleToCartRequestData);
}
