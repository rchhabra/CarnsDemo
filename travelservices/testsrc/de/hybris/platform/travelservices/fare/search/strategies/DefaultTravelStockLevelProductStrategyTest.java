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

package de.hybris.platform.travelservices.fare.search.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.travelservices.enums.ProductType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelStockLevelProductStrategyTest
{
	@InjectMocks
	private DefaultTravelStockLevelProductStrategy travelStockLevelProductStrategy;

	@Test
	public void testConvert()
	{
		final Map<String, String> productsQualifierMap = new HashMap<>();
		travelStockLevelProductStrategy.setProductsQualifierMap(productsQualifierMap);

		final ProductModel model = Mockito.mock(ProductModel.class);
		final ItemModelContextImpl internalContext=Mockito.mock(ItemModelContextImpl.class,Mockito.RETURNS_DEEP_STUBS);
		BDDMockito.given(model.getItemModelContext()).willReturn(internalContext);
		final Object value = null;
		BDDMockito.given(internalContext.getAttributeProvider().getAttribute(Matchers.anyString())).willReturn(value);
		final String convertedProd1 = travelStockLevelProductStrategy.convert(model);
		Assert.assertNull(convertedProd1);

		BDDMockito.given(model.getProductType()).willReturn(ProductType.FARE_PRODUCT);
		productsQualifierMap.put(ProductType.FARE_PRODUCT.getCode(), null);
		final String convertedProd2 = travelStockLevelProductStrategy.convert(model);
		Assert.assertNull(convertedProd2);

		productsQualifierMap.put(ProductType.FARE_PRODUCT.getCode(), "ProductCode");
		BDDMockito.given(internalContext.getAttributeProvider().getAttribute(Matchers.anyString())).willReturn("FareProductCode");
		final String convertedProd3 = travelStockLevelProductStrategy.convert(model);
		Assert.assertNotNull(convertedProd3);

	}

}