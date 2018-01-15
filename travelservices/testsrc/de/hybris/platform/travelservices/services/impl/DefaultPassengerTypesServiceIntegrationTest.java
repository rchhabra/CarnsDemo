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
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * Integration Test for the PassengerTypeService implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultPassengerTypesServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private PassengerTypeService passengerTypeService;

	@Resource
	private ModelService modelService;

	private List<PassengerTypeModel> ptModels;

	@Before
	public void setUp()
	{

		ptModels = new ArrayList<>();

		final PassengerTypeModel vip = new PassengerTypeModel();
		vip.setCode("vip");
		vip.setName("VIP", Locale.ENGLISH);

		ptModels.add(vip);
	}

	@Test
	public void getPassengerTypesTest()
	{

		// get list of current passenger types from the database

		final List<PassengerTypeModel> passengerTypes = passengerTypeService.getPassengerTypes();

		Assert.assertNotNull(passengerTypes);
		Assert.assertTrue(passengerTypes.isEmpty());

		// save out new sample data

		modelService.saveAll(ptModels);

		// get an updated list of passenger types which should contain the sample data

		final List<PassengerTypeModel> newPassengerTypes = passengerTypeService.getPassengerTypes();

		// evaluate results

		Assert.assertNotNull(newPassengerTypes);
		Assert.assertTrue(!newPassengerTypes.isEmpty());

		Assert.assertEquals(ptModels.size(), (newPassengerTypes.size() - passengerTypes.size()));
		Assert.assertTrue(newPassengerTypes.containsAll(ptModels));
	}

}
