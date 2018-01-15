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

package de.hybris.platform.travelfacades.fare.search.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareProductPopulatorTest
{
	private final FareProductPopulator<FareProductModel, FareProductData> populator = new FareProductPopulator<FareProductModel, FareProductData>();

	@Test
	public void populateTargetTest()
	{
		final FareProductModel source = new FareProductModel();
		source.setCode("FP1");
		source.setBookingClass("ECONOMY-PLUS");
		source.setFareBasisCode("ECONOMY");

		final FareProductData target = new FareProductData();

		populator.populate(source, target);

		Assert.assertEquals(source.getCode(), target.getCode());
		Assert.assertEquals(source.getBookingClass(), target.getBookingClass());
		Assert.assertEquals(source.getFareBasisCode(), target.getFareBasisCode());
	}
}
