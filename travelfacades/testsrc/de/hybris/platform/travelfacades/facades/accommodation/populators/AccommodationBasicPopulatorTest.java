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
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for AccommodationBasicPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationBasicPopulatorTest
{
	@InjectMocks
	private AccommodationBasicPopulator<AccommodationModel, RoomTypeData> accommodationBasicPopulator;

	private final String TEST_ACCOMMODATION_CODE = "ACCOMMODATION_CODE_TEST";
	private final String TEST_ACCOMMODATION_NAME = "ACCOMMODATION_NAME_TEST";

	@Test
	public void populateTest()
	{
		final AccommodationModel source = Mockito.mock(AccommodationModel.class);
		given(source.getCode()).willReturn(TEST_ACCOMMODATION_CODE);
		given(source.getName()).willReturn(TEST_ACCOMMODATION_NAME);

		final RoomTypeData target = new RoomTypeData();
		accommodationBasicPopulator.populate(source, target);
		Assert.assertEquals(TEST_ACCOMMODATION_CODE, target.getCode());
		Assert.assertEquals(TEST_ACCOMMODATION_NAME, target.getName());
	}
}
