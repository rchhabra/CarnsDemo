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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;


/**
 * Service that exposes methods relevant to TravelRestriction
 */
public interface TravelRestrictionService
{

	/**
	 * Returns the AddToCartCriteriaType for the given productModel. It retrieves the offerGroup of the product and
	 * returns its addToCartCriteria restriction if present, the default criteria otherwise.
	 *
	 * @param productModel
	 * 		the product model
	 * @return the AddToCartCriteriaType for the given product
	 */
	AddToCartCriteriaType getAddToCartCriteria(ProductModel productModel);

}
