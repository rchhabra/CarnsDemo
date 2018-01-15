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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import static org.mockito.BDDMockito.given;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportFacilityPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportFacilityPopulatorTest
{
	@InjectMocks
	TransportFacilityPopulator transportFacilityPopulator;

	@Mock
	Converter<LocationModel, LocationData> locationConverter;

	@Test
	public void testPopulateTransportFacility()
	{
		final TransportFacilityModel tfModel = Mockito.mock(TransportFacilityModel.class);

		Mockito.when(tfModel.getCode()).thenReturn("testCode");
		Mockito.when(tfModel.getName()).thenReturn("testName");

		final LocationData locationData = new LocationData();
		locationData.setCode("LON");

		given(locationConverter.convert(Matchers.any())).willReturn(locationData);

		final TransportFacilityData tfData = new TransportFacilityData();
		transportFacilityPopulator.populate(tfModel, tfData);


		Assert.assertEquals("testCode", tfData.getCode());
		Assert.assertEquals("testName", tfData.getName());
		Assert.assertEquals("LON", tfData.getLocation().getCode());
	}
}
