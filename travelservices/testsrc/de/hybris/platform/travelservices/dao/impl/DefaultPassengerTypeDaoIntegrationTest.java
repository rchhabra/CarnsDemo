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
import de.hybris.platform.travelservices.dao.PassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * Integration Test for the PassengerTypeDao implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultPassengerTypeDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private PassengerTypeDao passengerTypeDao;

	@Resource
	private ModelService modelService;

	private PassengerTypeModel vipPassengerType;

	/**
	 * Setup method for the test class
	 */
	@Before
	public void setUp()
	{
		vipPassengerType = new PassengerTypeModel();

		vipPassengerType.setCode("vipPassengerType");
		vipPassengerType.setName("VIP Passenger Type", Locale.ENGLISH);
	}

	/**
	 * Method to test get all PassengerTypes
	 */
	@Test
	public void getPassengerTypeTest()
	{

		// get current number passenger type items in the database

		final int numOfItems = passengerTypeDao.findPassengerTypes().size();

		// save new sample data

		modelService.saveAll(vipPassengerType);

		// get an update list of passenger type which should contain the sample data

		final List<PassengerTypeModel> newPassengerTypes = passengerTypeDao.findPassengerTypes();

		// evaluate results

		Assert.assertNotNull(newPassengerTypes);
		Assert.assertTrue(!newPassengerTypes.isEmpty());

		Assert.assertEquals(1, (newPassengerTypes.size() - numOfItems));

	}

	/**
	 * Method to test get passenger type by passengerTypeCode
	 */
	@Test
	public void testFindPassengerTypeByPassengerTypeCode()
	{

		// save new sample data

		modelService.saveAll(vipPassengerType);

		// get an update list of passenger type which should contain the sample data

		final PassengerTypeModel passengerTypeModel = passengerTypeDao.findPassengerType(vipPassengerType.getCode());

		// evaluate results

		Assert.assertNotNull(passengerTypeModel);

		Assert.assertEquals(passengerTypeModel.getCode(), vipPassengerType.getCode());

	}
}
