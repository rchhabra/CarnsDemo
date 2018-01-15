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
import de.hybris.platform.commercefacades.accommodation.AccommodationFacilityData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.facility.AccommodationFacilityModel;
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
 * Unit tests for AccommodationFacilitiesPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationFacilitiesPopulatorTest
{
	@InjectMocks
	private AccommodationFacilitiesPopulator<AccommodationModel, RoomTypeData> accommodationFacilitiesPopulator;

	@Mock
	private Converter<AccommodationFacilityModel, AccommodationFacilityData> accommodationFacilityConverter;

	private final List<AccommodationFacilityModel> testAccommodationModels = new ArrayList<>(1);
	private final String TEST_ACCOMMODATION_FACILITY_CODE = "ACCCOMMODATION_FACILITY_CODE_TEST";

	@Test
	public void populateTest()
	{
		final AccommodationFacilityData testAccommodationFacilityData = new AccommodationFacilityData();
		testAccommodationFacilityData.setCode(TEST_ACCOMMODATION_FACILITY_CODE);
		final List<AccommodationFacilityData> testAccommodationFacilityDatas = Arrays.asList(testAccommodationFacilityData);

		given(accommodationFacilityConverter.convertAll(testAccommodationModels)).willReturn(testAccommodationFacilityDatas);

		final AccommodationModel source = Mockito.mock(AccommodationModel.class);
		given(source.getAccommodationFacility()).willReturn(testAccommodationModels);

		final RoomTypeData target = new RoomTypeData();
		accommodationFacilitiesPopulator.populate(source, target);

		Assert.assertEquals(TEST_ACCOMMODATION_FACILITY_CODE, target.getFacilities().get(0).getCode());
	}

}
