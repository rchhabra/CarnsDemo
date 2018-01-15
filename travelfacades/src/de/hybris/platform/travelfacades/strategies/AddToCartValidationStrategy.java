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

import de.hybris.platform.commercefacades.travel.AddToCartResponseData;

import java.util.List;


/**
 * Strategy to validate the add to cart of ancillaries
 */
public interface AddToCartValidationStrategy
{

	/**
	 * Method to validate the add to cart of an ancillary product
	 *
	 * @param productCode
	 * 		as the product to be added or removed from the cart
	 * @param qty
	 * 		as the quantity to add/remove
	 * @param travellerCode
	 * 		as the code of the traveller to whom the product is added or removed
	 * @param transportOfferingCodes
	 * 		as the codes of the transportOfferings the product is added to or removed from
	 * @param travelRouteCode
	 * 		as the travelRouteCode the product is added to or removed from
	 * @return the addToCartResponseData, where valid is true if the addToCart is valid, false otherwise.
	 */
	AddToCartResponseData validateAddToCart(String productCode, long qty, String travellerCode,
			List<String> transportOfferingCodes, String travelRouteCode);

}
