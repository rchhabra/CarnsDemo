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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.promotions.model.ProductFixedPricePromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link ProductFixedPriceDiscountEvaluationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductFixedPriceDiscountEvaluationStrategyTest
{
	@InjectMocks
	ProductFixedPriceDiscountEvaluationStrategy productFixedPriceDiscountEvaluationStrategy;

	private final double DELTA = 0.001d;

	private ProductPromotionModel promotion;

	@Mock
	private PromotionPriceRowModel promotionPriceRowModel;

	@Mock
	private CurrencyModel currency;

	private final String TEST_ISO_CODE = "TEST_ISO_CODE";

	@Test
	public void testEvaluateDiscountForNonProductFixedPricePromotionModel()
	{
		promotion = Mockito.mock(ProductPromotionModel.class);
		Assert.assertEquals(0d,
				productFixedPriceDiscountEvaluationStrategy.evaluateDiscount(0d, promotion, StringUtils.EMPTY).doubleValue(), DELTA);
	}

	@Test
	public void testEvaluateDiscountForDiffferntISOCode()
	{
		promotion = Mockito.mock(ProductFixedPricePromotionModel.class);

		final PromotionPriceRowModel promotionPriceRowModel = Mockito.mock(PromotionPriceRowModel.class);
		given(promotionPriceRowModel.getCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn(TEST_ISO_CODE);
		given(((ProductFixedPricePromotionModel) promotion).getProductFixedUnitPrice())
				.willReturn(Arrays.asList(promotionPriceRowModel));
		Assert.assertEquals(0d,
				productFixedPriceDiscountEvaluationStrategy.evaluateDiscount(0d, promotion, StringUtils.EMPTY).doubleValue(), DELTA);
	}

	@Test
	public void testEvaluateDiscount()
	{
		promotion = Mockito.mock(ProductFixedPricePromotionModel.class);

		final PromotionPriceRowModel promotionPriceRowModel = Mockito.mock(PromotionPriceRowModel.class);
		given(promotionPriceRowModel.getPrice()).willReturn(new Double(10));
		given(promotionPriceRowModel.getCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn(TEST_ISO_CODE);
		given(((ProductFixedPricePromotionModel) promotion).getProductFixedUnitPrice())
				.willReturn(Arrays.asList(promotionPriceRowModel));
		Assert.assertEquals(90d,
				productFixedPriceDiscountEvaluationStrategy.evaluateDiscount(100d, promotion, TEST_ISO_CODE).doubleValue(), DELTA);
	}

}
