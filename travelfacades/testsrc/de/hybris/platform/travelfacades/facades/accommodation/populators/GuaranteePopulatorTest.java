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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.GuaranteeData;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for GuaranteePopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuaranteePopulatorTest
{
	@InjectMocks
	private GuaranteePopulator<GuaranteeModel, GuaranteeData> guaranteePopulator;

	private final String TEST_GURANTEE_CODE = "GURANTEE_CODE_TEST";

	@Test
	public void populateTest()
	{
		final GuaranteeModel source = Mockito.mock(GuaranteeModel.class);

		given(source.getCode()).willReturn(TEST_GURANTEE_CODE);
		final GuaranteeData target = new GuaranteeData();
		guaranteePopulator.populate(source, target);
		Assert.assertEquals(TEST_GURANTEE_CODE, target.getCode());
	}
}
