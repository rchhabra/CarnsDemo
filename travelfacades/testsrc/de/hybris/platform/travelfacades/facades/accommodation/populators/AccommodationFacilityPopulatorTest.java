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
import de.hybris.platform.commercefacades.accommodation.AccommodationFacilityData;
import de.hybris.platform.travelservices.model.facility.AccommodationFacilityModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for AccommodationFacilityPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationFacilityPopulatorTest
{
	@InjectMocks
	private AccommodationFacilityPopulator<AccommodationFacilityModel, AccommodationFacilityData> accommodationFacilityPopulator;

	private final String TEST_ACCOMMODATION_FACILITY_CODE = "ACCOMMODATION_FACILITY_CODE_TEST";
	private final String TEST_ACCOMMODATION_FACILITY_SHORT_DESCRIPTION = "ACCOMMODATION_FACILITY_SHORT_DESCRIPTION_TEST";

	@Test
	public void populateTest()
	{
		final AccommodationFacilityModel source = Mockito.mock(AccommodationFacilityModel.class);
		given(source.getCode()).willReturn(TEST_ACCOMMODATION_FACILITY_CODE);
		given(source.getShortDescription()).willReturn(TEST_ACCOMMODATION_FACILITY_SHORT_DESCRIPTION);

		final AccommodationFacilityData target = new AccommodationFacilityData();
		accommodationFacilityPopulator.populate(source, target);

		Assert.assertEquals(TEST_ACCOMMODATION_FACILITY_CODE, target.getCode());
		Assert.assertEquals(TEST_ACCOMMODATION_FACILITY_SHORT_DESCRIPTION, target.getShortDescription());
	}
}
