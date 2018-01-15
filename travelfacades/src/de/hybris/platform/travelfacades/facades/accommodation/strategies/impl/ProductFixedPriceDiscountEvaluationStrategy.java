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

import de.hybris.platform.promotions.model.ProductFixedPricePromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationDiscountEvaluationStrategy;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * Implementation of {@link AccommodationDiscountEvaluationStrategy} which evaluates discount for
 * {@link de.hybris.platform.promotions.model.ProductFixedPricePromotionModel}
 */
public class ProductFixedPriceDiscountEvaluationStrategy implements AccommodationDiscountEvaluationStrategy
{
	@Override
	public Double evaluateDiscount(final Double basePrice, final ProductPromotionModel promotion, final String currencyIso)
	{
		if (promotion instanceof ProductFixedPricePromotionModel)
		{
			final Optional<PromotionPriceRowModel> optionalPromotionPriceRow = ((ProductFixedPricePromotionModel) promotion)
					.getProductFixedUnitPrice().stream().filter(promotionPriceRowModel -> StringUtils
							.equalsIgnoreCase(promotionPriceRowModel.getCurrency().getIsocode(), currencyIso)).findFirst();

			if (optionalPromotionPriceRow.isPresent())
			{
				return basePrice - optionalPromotionPriceRow.get().getPrice();
			}

		}

		return 0d;
	}
}
