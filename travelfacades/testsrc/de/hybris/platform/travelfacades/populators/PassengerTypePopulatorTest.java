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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for PassengerTypePopulator implementation
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PassengerTypePopulatorTest
{

	@InjectMocks
	private PassengerTypePopulator passengerTypePopulator = new PassengerTypePopulator();

	@Test
	public void populatePassengerTypeDataFromPassengerTypeModelTest()
	{

		final PassengerTypeModel ptModel = Mockito.mock(PassengerTypeModel.class);

		Mockito.when(ptModel.getCode()).thenReturn("adult");
		Mockito.when(ptModel.getName()).thenReturn("Adult");

		final PassengerTypeData ptData = new PassengerTypeData();

		passengerTypePopulator.populate(ptModel, ptData);

		Assert.assertEquals(ptModel.getCode(), ptData.getCode());
		Assert.assertEquals(ptModel.getName(), ptData.getName());

	}

	@Test
	public void nullSourceTest()
	{

		final PassengerTypeModel ptModel = null;
		final PassengerTypeData ptData = new PassengerTypeData();

		passengerTypePopulator.populate(ptModel, ptData);

		Assert.assertEquals(null, ptData.getCode());
		Assert.assertEquals(null, ptData.getName());
	}
}
