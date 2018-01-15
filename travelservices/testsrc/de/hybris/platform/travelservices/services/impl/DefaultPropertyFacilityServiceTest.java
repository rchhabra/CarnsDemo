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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.dao.PropertyFacilityDao;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultPropertyFacilityService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPropertyFacilityServiceTest
{
	@InjectMocks
	private DefaultPropertyFacilityService propertyFacilityService;
	@Mock
	private PropertyFacilityDao propertyFacilityDao;
	@Mock
	private PropertyFacilityModel propertyFacilityModel;

	@Test
	public void testGetPropertyFacility()
	{
		Mockito.when(propertyFacilityDao.findPropertyFacility(Matchers.anyString())).thenReturn(propertyFacilityModel);
		Assert.assertNotNull(propertyFacilityService.getPropertyFacility("code1"));
	}

	@Test
	public void testGetPropertyFacilityThrowsException()
	{
		Mockito.when(propertyFacilityDao.findPropertyFacility(Matchers.anyString()))
				.thenThrow(new ModelNotFoundException("Model Not found"));
		Assert.assertNull(propertyFacilityService.getPropertyFacility("code1"));
	}

	@Test
	public void testGetPropertyFacilities()
	{
		Mockito.when(propertyFacilityDao.findPropertyFacilities())
				.thenReturn(Stream.of(propertyFacilityModel).collect(Collectors.toList()));
		Assert.assertNotNull(propertyFacilityService.getPropertyFacilities());
	}

	@Test
	public void testGetPropertyFacilitiesWithBatchAndIndex()
	{
		Mockito.when(propertyFacilityDao.findPropertyFacilities(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Assert.assertNull(propertyFacilityService.getPropertyFacilities(1, 1));
	}
}
