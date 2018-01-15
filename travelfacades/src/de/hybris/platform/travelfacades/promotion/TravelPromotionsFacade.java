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

package de.hybris.platform.travelfacades.promotion;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Travel Promotion facade interface. Travel specific promotion methods are defined here.
 */
public interface TravelPromotionsFacade
{
	/**
	 * Method to check if travel promotions should be applied for the current user
	 *
	 * @return boolean
	 */
	boolean isCurrentUserEligibleForTravelPromotions();

	/**
	 * Method to populate potential promotions for the product
	 *
	 * @param productModel
	 * 		the product model
	 * @param productData
	 * 		the product data
	 */
	void populatePotentialPromotions(final ProductModel productModel, final ProductData productData);

}
