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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for GuestOccupancyPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuestOccupancyPopulatorTest
{
	@InjectMocks
	private GuestOccupancyPopulator<GuestOccupancyModel, GuestOccupancyData> guaranteePopulator;

	@Mock
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	private final String TEST_GUEST_OCCUPANCY_CODE = "GUEST_OCCUPANCY_CODE_TEST";
	private final String TEST_PASSENGER_TYPE_CODE = "PASSENGER_TYPE_CODE_TEST";

	@Test
	public void populateTest()
	{
		final GuestOccupancyModel source = Mockito.mock(GuestOccupancyModel.class);
		final GuestOccupancyData target = new GuestOccupancyData();
		given(source.getCode()).willReturn(TEST_GUEST_OCCUPANCY_CODE);

		final PassengerTypeModel testPassengerTypeModel = Mockito.mock(PassengerTypeModel.class);
		final PassengerTypeData testPassengerTypeData=new PassengerTypeData();
		testPassengerTypeData.setCode(TEST_PASSENGER_TYPE_CODE);
		given(source.getPassengerType()).willReturn(testPassengerTypeModel);
		given(passengerTypeConverter.convert(testPassengerTypeModel)).willReturn(testPassengerTypeData);
		guaranteePopulator.populate(source, target);
		Assert.assertEquals(TEST_GUEST_OCCUPANCY_CODE, target.getCode());
		Assert.assertEquals(TEST_PASSENGER_TYPE_CODE, target.getPassengerType().getCode());
	}
}
