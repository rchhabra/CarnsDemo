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
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SpecialServiceRequestPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SpecialServiceRequestPopulatorTest
{
	@InjectMocks
	SpecialServiceRequestPopulator specialServiceRequestPopulator;

	@Test
	public void testPopulateSpecialServiceRequest()
	{
		final SpecialServiceRequestModel ssRequestModel = Mockito.mock(SpecialServiceRequestModel.class);
		Mockito.when(ssRequestModel.getCode()).thenReturn("testCode");
		Mockito.when(ssRequestModel.getName()).thenReturn("testName");
		final SpecialServiceRequestData ssRequestData = new SpecialServiceRequestData();
		specialServiceRequestPopulator.populate(ssRequestModel, ssRequestData);
		Assert.assertEquals("testCode", ssRequestData.getCode());
		Assert.assertEquals("testName", ssRequestData.getName());

	}
}
