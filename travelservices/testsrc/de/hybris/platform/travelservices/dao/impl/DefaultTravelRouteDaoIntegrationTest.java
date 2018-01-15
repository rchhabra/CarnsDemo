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
*/
package de.hybris.platform.travelservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test to test the functionality of the TravelRoute Dao service. This test case will save some sample data
 * to use for each scenario.
 */
@IntegrationTest
public class DefaultTravelRouteDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private TravelRouteDao travelRouteDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Setup of data for the test case
	 */
	@Before
	public void setup()
	{

		// setup data

		final TransportFacilityModel london = new TransportFacilityModel();

		london.setCode("LON");
		london.setName("London", Locale.ENGLISH);
		london.setType(TransportFacilityType.AIRPORT);

		final TransportFacilityModel newYork = new TransportFacilityModel();

		newYork.setCode("NYC");
		newYork.setName("New York City", Locale.ENGLISH);
		newYork.setType(TransportFacilityType.AIRPORT);

		final TransportFacilityModel paris = new TransportFacilityModel();

		paris.setCode("PAR");
		paris.setName("Paris", Locale.ENGLISH);
		paris.setType(TransportFacilityType.AIRPORT);

		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode("R1");
		travelRoute.setName("London to New York", Locale.ENGLISH);
		travelRoute.setOrigin(london);
		travelRoute.setDestination(newYork);

		modelService.save(london);
		modelService.save(newYork);
		modelService.save(paris);
		modelService.save(travelRoute);
	}

	/**
	 * Test case to test a valid scenario where a List of TravelRoutes are returned based on the Origin and Destination
	 * TransportFacility provided. This test case should return a list of TravelRouteModel models with 2 elements.
	 */
	@Test
	public void getTravelRoutesTest()
	{

		final List<TravelRouteModel> models = travelRouteDao.findTravelRoutes("LON", "NYC");

		Assert.assertTrue(!models.isEmpty());
		Assert.assertEquals(1, models.size());
	}

	/**
	 * Test case to test a scenario where a TravelRoute based on the Origin and Destination can not be found in the
	 * database. The test case should return an empty list.
	 */
	@Test
	public void noRoutesFoundTest()
	{
		final List<TravelRouteModel> models = travelRouteDao.findTravelRoutes("LON", "PAR");

		Assert.assertTrue(models.isEmpty());
	}

	/**
	 * Test case to test a for instances where no Origin TransportFacilityModel is returned from the database. The test
	 * case should fail with an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullOriginParameterTest()
	{
		travelRouteDao.findTravelRoutes("LTN", null);
	}

	/**
	 * Test case to test a for instances where no Destination TransportFacilityModel is returned from the database. The
	 * test case should fail with an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullDestinationParameterTest()
	{
		travelRouteDao.findTravelRoutes(null, "LTN");
	}

	/**
	 * Test case to get a particular route by the route code
	 */
	@Test
	public void findRouteByCodeTest()
	{
		final TravelRouteModel route = travelRouteDao.findTravelRoute("R1");

		Assert.assertNotNull(route);
		Assert.assertEquals("R1", route.getCode());
	}

	/**
	 * Test case to test for exception when a route is not found
	 */
	@Test(expected = ModelNotFoundException.class)
	public void noRouteFoundTest()
	{
		travelRouteDao.findTravelRoute("NoRoute");
	}

	/**
	 * Test case to test for ambiguous route
	 */
	@Test(expected = AmbiguousIdentifierException.class)
	public void ambiguousRouteTest()
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setCode("R1");
		modelService.save(travelRoute);
		travelRouteDao.findTravelRoute(travelRoute.getCode());
	}
}
