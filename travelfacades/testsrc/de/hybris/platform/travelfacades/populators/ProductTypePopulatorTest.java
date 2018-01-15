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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.ProductType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link ProductTypePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductTypePopulatorTest
{
	@InjectMocks
	ProductTypePopulator productTypePopulator;

	@Test
	public void testProductTypePopulator()
	{

		final ProductModel productTypeModel = new ProductModel();
		productTypeModel.setProductType(ProductType.FARE_PRODUCT);
		final ProductData productData = new ProductData();
		productTypePopulator.populate(productTypeModel, productData);
		Assert.assertEquals(ProductType.FARE_PRODUCT.toString(), productData.getProductType());

	}
}