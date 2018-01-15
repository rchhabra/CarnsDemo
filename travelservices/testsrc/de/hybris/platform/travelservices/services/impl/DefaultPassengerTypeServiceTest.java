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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.PassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultPassengerTypeService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPassengerTypeServiceTest
{

	@Mock
	private PassengerTypeDao passengerTypeDao;

	@InjectMocks
	private final DefaultPassengerTypeService passengerTypeService = new DefaultPassengerTypeService();

	@Test
	public void getPassengerTypesTest()
	{

		// setup sample data

		final PassengerTypeModel adult = new PassengerTypeModel();
		adult.setCode("adult");
		adult.setName("Adult", Locale.ENGLISH);

		final PassengerTypeModel children = new PassengerTypeModel();
		children.setCode("child");
		children.setName("Child", Locale.ENGLISH);

		final PassengerTypeModel infant = new PassengerTypeModel();
		adult.setCode("infant");
		adult.setName("Infant", Locale.ENGLISH);

		final List<PassengerTypeModel> passengerTypes = new ArrayList<>();
		passengerTypes.add(adult);
		passengerTypes.add(children);
		passengerTypes.add(infant);

		// setup mock scenario

		Mockito.when(passengerTypeDao.findPassengerTypes()).thenReturn(passengerTypes);

		// perform test

		final List<PassengerTypeModel> result = passengerTypeService.getPassengerTypes();

		// evaluate results

		Assert.assertNotNull(result);
		Assert.assertEquals(passengerTypes.size(), result.size());
	}

	@Test
	public void getPassengerTypeTest()
	{
		final PassengerTypeModel adultPassengerType = new PassengerTypeModel();
		adultPassengerType.setCode("adult");
		adultPassengerType.setName("Adult", Locale.ENGLISH);
		Mockito.when(passengerTypeDao.findPassengerType("adult")).thenReturn(adultPassengerType);
		final PassengerTypeModel result = passengerTypeService.getPassengerType("adult");
		Assert.assertNotNull(result);
		Assert.assertEquals("adult", result.getCode());
	}

}
