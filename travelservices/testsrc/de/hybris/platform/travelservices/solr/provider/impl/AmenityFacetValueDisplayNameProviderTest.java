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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.services.PropertyFacilityService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link AmenityFacetValueDisplayNameProvider}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AmenityFacetValueDisplayNameProviderTest
{
	@InjectMocks
	AmenityFacetValueDisplayNameProvider amenityFacetValueDisplayNameProvider;

	@Mock
	private PropertyFacilityService propertyFacilityService;

	private final String TEST_SHORT_DESCRIPTION = "TEST_SHORT_DESCRIPTION";

	@Test
	public void testGetDisplayName()
	{
		final PropertyFacilityModel pfModel = new PropertyFacilityModel()
		{
			@Override
			public String getShortDescription()
			{
				return TEST_SHORT_DESCRIPTION;
			}
		};
		Mockito.when(propertyFacilityService.getPropertyFacility(Matchers.anyString())).thenReturn(pfModel);

		Assert.assertEquals(TEST_SHORT_DESCRIPTION,
				amenityFacetValueDisplayNameProvider.getDisplayName(null, null, "TEST_AMENITY_CODE"));
	}

	@Test
	public void testGetDisplayNameForNullModel()
	{
		Mockito.when(propertyFacilityService.getPropertyFacility(Matchers.anyString())).thenReturn(null);

		Assert.assertEquals("TEST_AMENITY_CODE",
				amenityFacetValueDisplayNameProvider.getDisplayName(null, null, "TEST_AMENITY_CODE"));
	}
}
