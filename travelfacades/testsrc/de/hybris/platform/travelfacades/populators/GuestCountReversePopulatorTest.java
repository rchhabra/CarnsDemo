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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * Unit tests for @{link = GuestCountReversePopulator} implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuestCountReversePopulatorTest
{
	@Mock
	private PassengerTypeService passengerTypeService;
	
	@InjectMocks
	private GuestCountReversePopulator guestCountReversePopulator;

	private final String TEST_PASSENGER_TYPE_CODE="TEST_PASSENGER_TYPE_CODE";
	
	private final PassengerTypeModel testPassengerTypeModel = new PassengerTypeModel();

	@Test
	public void populateTest()
	{
		final PassengerTypeQuantityData source = new PassengerTypeQuantityData();
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode(TEST_PASSENGER_TYPE_CODE);
		source.setPassengerType(passengerTypeData);
		source.setQuantity(0);
		given(passengerTypeService.getPassengerType(Matchers.anyString())).willReturn(testPassengerTypeModel);

		final GuestCountModel target = new GuestCountModel();
		guestCountReversePopulator.populate(source, target);
		Assert.assertEquals(testPassengerTypeModel, target.getPassengerType());
	}
}
