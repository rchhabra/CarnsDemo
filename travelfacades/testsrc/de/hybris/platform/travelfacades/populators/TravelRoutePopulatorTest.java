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
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for TravelRoutePopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelRoutePopulatorTest
{

	@InjectMocks
	private TravelRoutePopulator travelRoutePopulator = new TravelRoutePopulator();

	@Mock
	private Converter<TravelSectorModel, TravelSectorData> travelSectorConverter;

	@Mock
	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;

	@Mock
	private TravelRouteModel travelRouteModel;

	@Before
	public void setup()
	{

		Mockito.when(travelRouteModel.getCode()).thenReturn("0001");
		Mockito.when(travelRouteModel.getName()).thenReturn("London to New York via Paris");
		Mockito.when(travelRouteModel.getOrigin()).thenReturn(createTransportFacilityModel("LHR", "London Heathrow Airport"));
		Mockito.when(travelRouteModel.getDestination())
				.thenReturn(createTransportFacilityModel("JFK", "John F. Kennedy International Airport"));
		Mockito.when(travelRouteModel.getTravelSector()).thenReturn(getTravelSectors());

		final TransportFacilityData origin = new TransportFacilityData();
		origin.setCode("LHR");
		origin.setName("London Heathrow Airport");

		final TransportFacilityData destination = new TransportFacilityData();
		destination.setCode("JFK");
		destination.setName("John F. Kennedy International Airport");

		Mockito.when(transportFacilityConverter.convert(travelRouteModel.getOrigin())).thenReturn(origin);
		Mockito.when(transportFacilityConverter.convert(travelRouteModel.getDestination())).thenReturn(destination);

	}

	/**
	 * Test method to verify the population of travel routes models to data objects.
	 */
	@Test
	public void populateTravelRouteDataFromTravelRouteModelTest()
	{
		final TravelRouteData travelRouteData = new TravelRouteData();

		travelRoutePopulator.populate(travelRouteModel, travelRouteData);

		Assert.assertNotNull(travelRouteData);
		Assert.assertEquals(travelRouteData.getCode(), travelRouteModel.getCode());

		Assert.assertNotNull(travelRouteData.getOrigin());
		Assert.assertEquals(travelRouteData.getOrigin().getCode(), travelRouteModel.getOrigin().getCode());

		Assert.assertNotNull(travelRouteData.getDestination());
		Assert.assertEquals(travelRouteData.getDestination().getCode(), travelRouteModel.getDestination().getCode());

		Assert.assertTrue(!travelRouteData.getSectors().isEmpty());
		Assert.assertEquals(travelRouteModel.getTravelSector().size(), travelRouteData.getSectors().size());
	}

	private List<TravelSectorModel> getTravelSectors()
	{
		final List<TravelSectorModel> sectors = new ArrayList<>();

		final TravelSectorModel sector1 = new TravelSectorModel();
		sector1.setCode("S1");
		sector1.setName("London to Paris", Locale.ENGLISH);
		sector1.setOrigin(createTransportFacilityModel("LHR", "London Heathrow Airport"));
		sector1.setDestination(createTransportFacilityModel("CDG", "Charles de Gaulle Airport"));

		final TravelSectorModel sector2 = new TravelSectorModel();
		sector2.setCode("S2");
		sector2.setName("Paris to New York", Locale.ENGLISH);
		sector2.setOrigin(createTransportFacilityModel("CDG", "Charles de Gaulle Airport"));
		sector2.setDestination(createTransportFacilityModel("JFK", "John F. Kennedy International Airport"));

		sectors.add(sector1);
		sectors.add(sector2);

		return sectors;

	}

	private TransportFacilityModel createTransportFacilityModel(final String code, final String name)
	{
		final TransportFacilityModel transportFacility = new TransportFacilityModel();

		transportFacility.setCode(code);
		transportFacility.setName(name, Locale.ENGLISH);
		transportFacility.setType(TransportFacilityType.AIRPORT);

		return transportFacility;
	}
}
