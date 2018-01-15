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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test to test the functionality of the TransportFacility Dao service. This test case will save some sample
 * data to use for each scenario.
 */
@IntegrationTest
public class DefaultTransportFacilityDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private TransportFacilityDao transportFacilityDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private final TransportFacilityModel london = new TransportFacilityModel();

	/**
	 * Setup of data for the test case
	 */
	@Before
	public void setup()
	{

		// setup data

		london.setCode("LON");
		london.setName("London", Locale.ENGLISH);
		london.setType(TransportFacilityType.AIRPORT);

		modelService.save(london);
	}

	/**
	 * Test case to test a valid set of data. This test case should return a model with the code 'LON'
	 */
	@Test
	public void getTransportFacilityTest()
	{

		final TransportFacilityModel model = transportFacilityDao.findTransportFacility("LON");

		Assert.assertNotNull(model);
		Assert.assertEquals(london.getCode(), model.getCode());
	}

	/**
	 * Test case to test a for instances where a transport facility for a given code can not be found. This test case
	 * should return null.
	 */
	@Test
	public void noTransportFacilityFoundTest()
	{

		final TransportFacilityModel model = transportFacilityDao.findTransportFacility("NYC");

		Assert.assertNull(model);
	}

	/**
	 * Test case to test a for instances where a null object is passed to the TransportFacilityDao.findTravelRoutes()
	 * method. This test case should thrown an IllegalArgumentException exception after failing the
	 * validateParameterNotNull in the TransportFacilityDao.findTravelRoutes() method.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullCodeParameterTest()
	{
		transportFacilityDao.findTransportFacility(null);
	}

}
