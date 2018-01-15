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
import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;

import java.util.List;
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
 * Unit Test for the DefaultTravelRouteService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelRouteServiceTest
{
	@Mock
	private TravelRouteDao travelRouteDao;
	@InjectMocks
	DefaultTravelRouteService travelRouteService;

	@Test
	public void testGetTravelRoutes()
	{
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		Mockito.when(travelRouteDao.findTravelRoutes(Matchers.any(), Matchers.any()))
				.thenReturn(Stream.of(travelRouteModel).collect(Collectors.toList()));
		final List<TravelRouteModel> travelRoutes = travelRouteService.getTravelRoutes("LGW", "CDG");
		Assert.assertNotNull(travelRoutes);
		Assert.assertEquals(1, travelRoutes.size());
	}

	@Test
	public void testGetTravelRoute()
	{
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		Mockito.when(travelRouteDao.findTravelRoute(Matchers.any())).thenReturn(travelRouteModel);
		final TravelRouteModel travelRoute = travelRouteService.getTravelRoute("LGW-CDG");
		Assert.assertNotNull(travelRoute);
	}
}
