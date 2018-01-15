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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationDiscountEvaluationStrategy;


/**
 * Implementation of {@link AccommodationDiscountEvaluationStrategy} which evaluates discount for
 * {@link de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel}
 */
public class ProductPercentageDiscountEvaluationStrategy implements AccommodationDiscountEvaluationStrategy
{
	@Override
	public Double evaluateDiscount(final Double basePrice, final ProductPromotionModel promotion, final String currencyIso)
	{
		if (promotion instanceof ProductPercentageDiscountPromotionModel)
		{
			final Double percentageDiscount = ((ProductPercentageDiscountPromotionModel) promotion).getPercentageDiscount();
			if (percentageDiscount != null)
			{
				return basePrice * (percentageDiscount / 100d);
			}
		}

		return 0d;
	}
}
