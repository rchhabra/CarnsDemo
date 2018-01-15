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
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for CabinClassPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CabinClassPopulatorTest
{

	@InjectMocks
	private CabinClassPopulator cabinClassPopulator = new CabinClassPopulator();

	@Test
	public void populateCabinClassDataTest()
	{

		final CabinClassModel ccModel = Mockito.mock(CabinClassModel.class);

		ccModel.setCode("economy");
		ccModel.setName("Economy");

		final CabinClassData ccData = new CabinClassData();

		cabinClassPopulator.populate(ccModel, ccData);

		Assert.assertEquals(ccModel.getCode(), ccData.getCode());
		Assert.assertEquals(ccModel.getName(), ccData.getName());
	}

	@Test
	public void nullCabinClassModelTest()
	{

		final CabinClassModel ccModel = null;

		final CabinClassData ccData = new CabinClassData();

		cabinClassPopulator.populate(ccModel, ccData);

		Assert.assertEquals(null, ccData.getCode());
		Assert.assertEquals(null, ccData.getName());
	}
}
