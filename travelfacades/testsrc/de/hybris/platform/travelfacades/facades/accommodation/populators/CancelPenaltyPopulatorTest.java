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
import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for CancelPenaltyPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelPenaltyPopulatorTest
{
	@InjectMocks
	private CancelPenaltyPopulator<CancelPenaltyModel, CancelPenaltyData> cancelPenaltyPopulator;

	private final String TEST_CANCEL_PENALITY_CODE = "CANCEL_PENALITY_CODE_TEST";

	@Test
	public void populateTest()
	{
		final CancelPenaltyModel source = Mockito.mock(CancelPenaltyModel.class);
		given(source.getCode()).willReturn(TEST_CANCEL_PENALITY_CODE);

		final CancelPenaltyData target = new CancelPenaltyData();
		cancelPenaltyPopulator.populate(source, target);
		Assert.assertEquals(TEST_CANCEL_PENALITY_CODE, target.getCode());
	}
}
