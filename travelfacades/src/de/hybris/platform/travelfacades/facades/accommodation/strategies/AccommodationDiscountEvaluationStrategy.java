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
 */

package de.hybris.platform.travelfacades.facades.accommodation.strategies;

import de.hybris.platform.promotions.model.ProductPromotionModel;


/**
 * Interface responsible for evaluation of the discount value from a promotion provided.
 */
public interface AccommodationDiscountEvaluationStrategy
{
	/**
	 * Evaluates a strategy related to specific promotion to retrieve the value of discount.
	 *
	 * @param basePrice
	 * 		the base price
	 * @param promotion
	 * 		the promotion
	 * @param currencyIso
	 * 		the currency iso
	 * @return discount value
	 */
	Double evaluateDiscount(Double basePrice, ProductPromotionModel promotion, String currencyIso);
}
