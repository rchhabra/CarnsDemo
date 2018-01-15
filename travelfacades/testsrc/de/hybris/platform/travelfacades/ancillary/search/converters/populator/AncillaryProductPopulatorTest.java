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

package de.hybris.platform.travelfacades.ancillary.search.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import de.hybris.platform.travelfacades.ancillary.search.converters.populator.AncillaryProductPopulator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AncillaryProductPopulatorTest
{
	private final AncillaryProductPopulator<ProductModel, ProductData> populator = new AncillaryProductPopulator<ProductModel, ProductData>();

	@Test
	public void populateTargetTest()
	{
		final ProductModel source = Mockito.mock(ProductModel.class);
		Mockito.when(source.getCode()).thenReturn("loungeaccess");
		Mockito.when(source.getName()).thenReturn("Lounge Access");

		final ProductData target = new ProductData();

		populator.populate(source, target);

		Assert.assertEquals(source.getCode(), target.getCode());
		Assert.assertEquals(source.getName(), target.getName());
	}
}
