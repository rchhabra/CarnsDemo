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
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for AccommodationOccupanciesPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationOccupanciesPopulatorTest
{
	@InjectMocks
	private AccommodationOccupanciesPopulator<AccommodationModel, RoomTypeData> accommodationOccupanciesPopulator;

	@Mock
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;

	public String TEST_GUEST_OCCUPANCY_DATA_CODE = "GUEST_OCCUPANCY_DATA_CODE_TEST";
	List<GuestOccupancyModel> testGuestOccupancyModels = new ArrayList<>(1);

	@Test
	public void populateTest()
	{
		final GuestOccupancyData testGuestOccupancyData = new GuestOccupancyData();
		testGuestOccupancyData.setCode(TEST_GUEST_OCCUPANCY_DATA_CODE);

		final List<GuestOccupancyData> testGuestOccupancyDatas = Arrays.asList(testGuestOccupancyData);
		final AccommodationModel source = Mockito.mock(AccommodationModel.class);
		given(source.getGuestOccupancies()).willReturn(testGuestOccupancyModels);
		given(guestOccupancyConverter.convertAll(testGuestOccupancyModels)).willReturn(testGuestOccupancyDatas);

		final RoomTypeData target = new RoomTypeData();
		accommodationOccupanciesPopulator.populate(source, target);

		Assert.assertEquals(TEST_GUEST_OCCUPANCY_DATA_CODE, target.getOccupancies().get(0).getCode());
	}
}
