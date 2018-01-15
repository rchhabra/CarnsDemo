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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test to test the functionality of the TravelRoute Service. This test case will save some sample data to
 * use for each scenario.
 */
@IntegrationTest
public class DefaultTravelRouteServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private TravelRouteService travelRouteService;

	@Resource
	private ModelService modelService;

	/**
	 * Initial setup of data for the test case
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

		// Route: LON - NYC (direct)
		final TravelRouteModel travelRouteA = new TravelRouteModel();

		travelRouteA.setCode("R001");
		travelRouteA.setName("London to New York ", Locale.ENGLISH);
		travelRouteA.setOrigin(london);
		travelRouteA.setDestination(newYork);

		// Route: LON - PAR - NYC (indirect)
		final TravelRouteModel travelRouteB = new TravelRouteModel();

		travelRouteB.setCode("R002");
		travelRouteB.setName("London to New York via Paris", Locale.ENGLISH);
		travelRouteB.setOrigin(london);
		travelRouteB.setDestination(newYork);

		modelService.save(london);
		modelService.save(newYork);
		modelService.save(paris);
		modelService.save(travelRouteA);
		modelService.save(travelRouteB);
	}

	/**
	 * Test case to test a valid scenario where a List of TravelRoutes are returned based on the Origin and Destination
	 * TransportFacility provided. This test case should return a list of TravelRouteModel models with 2 elements.
	 */
	@Test
	public void getTravelRoutesTest()
	{

		final List<TravelRouteModel> models = travelRouteService.getTravelRoutes("LON", "NYC");

		Assert.assertTrue(!models.isEmpty());
		Assert.assertEquals(2, models.size());
	}

	/**
	 * Test case to test a scenario where no TravelRoute is found. This test case should return an empty list.
	 */
	@Test
	public void noTravelRoutesTest()
	{

		final List<TravelRouteModel> models = travelRouteService.getTravelRoutes("LTN", "NYC");

		Assert.assertTrue(models.isEmpty());
	}

}
