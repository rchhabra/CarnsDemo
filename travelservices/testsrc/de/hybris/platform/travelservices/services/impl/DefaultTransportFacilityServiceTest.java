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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTransportFacilityServiceTest
{
	@Mock
	private TransportFacilityDao transportFacilityDao;

	@InjectMocks
	private DefaultTransportFacilityService defaultTransportFacilityService;

	@Test
	public void testGetTransportFacility()
	{
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		given(transportFacilityDao.findTransportFacility("LGW")).willReturn(transportFacility);
		assertEquals(transportFacility, defaultTransportFacilityService.getTransportFacility("LGW"));
	}

	@Test
	public void testGetCountry()
	{
		final LocationModel loc = new LocationModel();
		loc.setLocationType(LocationType.COUNTRY);
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(loc);
		assertEquals(loc, defaultTransportFacilityService.getCountry(transportFacility));
	}

	@Test
	public void testGetCountryNull()
	{
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		assertNull(defaultTransportFacilityService.getCountry(transportFacility));
	}

	@Test
	public void testGetCountryCityLocation()
	{
		final LocationModel loc = new LocationModel();
		loc.setLocationType(LocationType.COUNTRY);
		final LocationModel locCity = new LocationModel();
		locCity.setLocationType(LocationType.CITY);
		locCity.setSuperlocations(Stream.of(loc).collect(Collectors.toList()));
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(locCity);
		assertEquals(loc, defaultTransportFacilityService.getCountry(transportFacility));
	}

	@Test
	public void testGetCountryNoCountryLocation()
	{
		final LocationModel locCity = new LocationModel();
		locCity.setLocationType(LocationType.CITY);
		final LocationModel locAirport = new LocationModel();
		locAirport.setLocationType(LocationType.AIRPORTGROUP);
		locAirport.setSuperlocations(Stream.of(locCity).collect(Collectors.toList()));
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(locAirport);
		assertNull(defaultTransportFacilityService.getCountry(transportFacility));
	}

	@Test
	public void testGetCity()
	{
		final LocationModel loc = new LocationModel();
		loc.setLocationType(LocationType.CITY);
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(loc);
		assertEquals(loc, defaultTransportFacilityService.getCity(transportFacility));
	}

	@Test
	public void testGetCityNull()
	{
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		assertNull(defaultTransportFacilityService.getCity(transportFacility));
	}

	@Test
	public void testGetCityTransportFacility()
	{
		final LocationModel locCity = new LocationModel();
		locCity.setLocationType(LocationType.CITY);
		final LocationModel loc = new LocationModel();
		loc.setLocationType(LocationType.AIRPORTGROUP);
		loc.setSuperlocations(Stream.of(locCity).collect(Collectors.toList()));
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(loc);
		assertEquals(locCity, defaultTransportFacilityService.getCity(transportFacility));
	}

	@Test
	public void testGetCityNoCityLocation()
	{
		final LocationModel locContinent = new LocationModel();
		locContinent.setLocationType(LocationType.CONTINENT);
		final LocationModel locCountry = new LocationModel();
		locCountry.setLocationType(LocationType.COUNTRY);
		locCountry.setSuperlocations(Stream.of(locContinent).collect(Collectors.toList()));
		final TransportFacilityModel transportFacility = new TransportFacilityModel();
		transportFacility.setLocation(locCountry);
		assertNull(defaultTransportFacilityService.getCity(transportFacility));
	}

}
