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
package de.hybris.platform.travelbackofficeservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTransportOfferingService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeTransportOfferingServiceTest
{
	@InjectMocks
	private DefaultBackofficeTransportOfferingService transportOfferingService;

	@Mock
	private ModelService modelService;

	@Test
	public void updateTransportOfferingsWithLocationsTest()
	{
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();

		final LocationModel originLocation = new LocationModel();
		originLocation.setSuperlocations(Arrays.asList(createLocation()));
		final LocationModel destination = new LocationModel();
		destination.setSuperlocations(Arrays.asList(createLocation()));

		final TransportFacilityModel originTransportFacility = new TransportFacilityModel();
		originTransportFacility.setLocation(originLocation);
		final TransportFacilityModel destinationTransportFacility = new TransportFacilityModel();
		destinationTransportFacility.setLocation(destination);

		final TravelSectorModel travelSector = new TravelSectorModel();
		travelSector.setOrigin(originTransportFacility);
		travelSector.setDestination(destinationTransportFacility);

		final TransportOfferingModel transportOffering = createTransportOffering(travelSector);
		transportOfferings.add(transportOffering);
		Mockito.doNothing().when(modelService).saveAll(transportOfferings);
		transportOfferingService.updateTransportOfferingsWithLocations(transportOfferings);
		Mockito.verify(modelService, Mockito.times(1)).saveAll(transportOfferings);

	}

	private TransportOfferingModel createTransportOffering(final TravelSectorModel travelSector)
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setTravelSector(travelSector);
		return transportOffering;
	}

	private LocationModel createLocation()
	{
		final LocationModel location = new LocationModel();
		return location;
	}
}
