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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravelSectorPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSectorPopulatorTest
{
	@InjectMocks
	TravelSectorPopulator travelSectorPopulator;
	@Mock
	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;

	@Test
	public void testTravelSectorPopulatorData()
	{
		final TravelSectorModel tsModel = new TravelSectorModel();
		tsModel.setCode("testCode");

		final TransportFacilityModel tfOriginModel = new TransportFacilityModel();
		tsModel.setOrigin(tfOriginModel);

		final TransportFacilityModel tfDestinationModel = new TransportFacilityModel();
		tsModel.setDestination(tfDestinationModel);

		final TransportFacilityData tfData = new TransportFacilityData();
		given(transportFacilityConverter.convert(Matchers.any(TransportFacilityModel.class))).willReturn(tfData);

		final TravelSectorData tsData = new TravelSectorData();
		travelSectorPopulator.populate(tsModel, tsData);

		Assert.assertEquals("testCode", tsData.getCode());
		Assert.assertNotNull(tsData.getOrigin());
		Assert.assertNotNull(tsData.getDestination());

	}
}
