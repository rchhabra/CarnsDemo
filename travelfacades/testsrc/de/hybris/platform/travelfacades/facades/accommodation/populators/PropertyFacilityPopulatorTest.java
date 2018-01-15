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
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for PropertyFacilityPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyFacilityPopulatorTest
{
	@InjectMocks
	private PropertyFacilityPopulator<PropertyFacilityModel, FacilityData> propertyFacilityPopulator;

	@Mock
	private EnumerationService enumerationService;

	private final String TEST_FACILITY_TYPE = "FACILITY_TYPE";

	@Test
	public void populateTest()
	{
		final PropertyFacilityModel source = Mockito.mock(PropertyFacilityModel.class);
		given(enumerationService.getEnumerationName(Matchers.any())).willReturn(TEST_FACILITY_TYPE);
		final FacilityData target = new FacilityData();
		propertyFacilityPopulator.populate(source, target);
		Assert.assertEquals(TEST_FACILITY_TYPE, target.getFacilityType());
	}
}
