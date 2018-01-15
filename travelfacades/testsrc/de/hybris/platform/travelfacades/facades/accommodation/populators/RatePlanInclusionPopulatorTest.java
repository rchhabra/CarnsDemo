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
import de.hybris.platform.commercefacades.accommodation.RatePlanInclusionData;
import de.hybris.platform.travelservices.model.accommodation.RatePlanInclusionModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for RatePlanInclusionPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RatePlanInclusionPopulatorTest
{
	@InjectMocks
	RatePlanInclusionPopulator<RatePlanInclusionModel, RatePlanInclusionData> ratePlanInclusionPopulator;

	private final String TEST_RATE_PLAN_INCLUSION_SHORT_DESCRIPTION = "RATE_PLAN_INCLUSION_SHORT_DESCRIPTION_TEST";

	@Test
	public void populateTest()
	{
		final RatePlanInclusionModel source = Mockito.mock(RatePlanInclusionModel.class);
		given(source.getShortDescription()).willReturn(TEST_RATE_PLAN_INCLUSION_SHORT_DESCRIPTION);
		final RatePlanInclusionData target = new RatePlanInclusionData();
		ratePlanInclusionPopulator.populate(source, target);

		Assert.assertEquals(TEST_RATE_PLAN_INCLUSION_SHORT_DESCRIPTION, target.getShortDescription());
	}
}
