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
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.dao.TravelLocationDao;
import de.hybris.platform.travelservices.model.travel.LocationModel;

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
 * Unit Test for the DefaultTravelLocationService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelLocationServiceTest
{
	@InjectMocks
	private DefaultTravelLocationService travelLocationService;
	@Mock
	private TravelLocationDao travelLocationDao;

	@Test
	public void testgetLocation()
	{
		final LocationModel location = new LocationModel();
		Mockito.when(travelLocationDao.findLocation(Matchers.any())).thenReturn(location);

		final LocationModel result = travelLocationService.getLocation("loc1");
		Assert.assertNotNull(result);
	}

}
