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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link ProductPercentageDiscountEvaluationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductPercentageDiscountEvaluationStrategyTest
{
	@InjectMocks
	ProductPercentageDiscountEvaluationStrategy productPercentageDiscountEvaluationStrategy;

	private final double DELTA = 0.001d;

	private ProductPromotionModel promotion;
	@Test
	public void testEvaluateDiscountForNonProductPercentageDiscountPromotionModel()
	{
		promotion = Mockito.mock(ProductPromotionModel.class);
		Assert.assertEquals(0d,
				productPercentageDiscountEvaluationStrategy.evaluateDiscount(0d, promotion, StringUtils.EMPTY).doubleValue(), DELTA);
	}

	@Test
	public void testEvaluateDiscountForNullPercentageDiscount()
	{
		promotion = Mockito.mock(ProductPercentageDiscountPromotionModel.class);
		given(((ProductPercentageDiscountPromotionModel) promotion).getPercentageDiscount()).willReturn(null);
		Assert.assertEquals(0d,
				productPercentageDiscountEvaluationStrategy.evaluateDiscount(0d, promotion, StringUtils.EMPTY).doubleValue(), DELTA);
	}

	@Test
	public void testEvaluateDiscount()
	{
		promotion = Mockito.mock(ProductPercentageDiscountPromotionModel.class);
		given(((ProductPercentageDiscountPromotionModel) promotion).getPercentageDiscount()).willReturn(new Double(100));
		Assert.assertEquals(1d,
				productPercentageDiscountEvaluationStrategy.evaluateDiscount(1d, promotion, StringUtils.EMPTY).doubleValue(), DELTA);
	}

}
