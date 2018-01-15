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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.enums.TripType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hybris.platform.travelfacades.fare.search.handlers.impl.ItineraryHandler;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItineraryHandlerTest
{
	private final ItineraryHandler itineraryHandler = new ItineraryHandler();

	private FareSearchRequestData fareSearchRequestData;
	private FareSelectionData fareSelectionData;

	@Before
	public void setup()
	{
		fareSearchRequestData = new FareSearchRequestData();
		fareSelectionData = TestDataSetup.createFareSelection();
	}

	@Test
	public void populateItinerariesTest()
	{
		final TravelRouteData route = TestDataSetup.createRoute("LTN", "CDG", "LTNCDG");
		final TransportOfferingData transportOffering = mock(TransportOfferingData.class);
		final List<ScheduledRouteData> scheduledRoutes = TestDataSetup.createScheduledRoutes(1);

		given(fareSelectionData.getPricedItineraries()).willReturn(new ArrayList<>());
		given(scheduledRoutes.get(0).getReferenceNumber()).willReturn(0001);
		given(scheduledRoutes.get(0).getRoute()).willReturn(route);
		given(scheduledRoutes.get(0).getTransportOfferings()).willReturn(Arrays.asList(new TransportOfferingData[]
		{ transportOffering }));

		fareSearchRequestData.setTripType(TripType.SINGLE);

		itineraryHandler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(CollectionUtils.isNotEmpty(fareSelectionData.getPricedItineraries()));
		Assert.assertEquals(1, CollectionUtils.size(fareSelectionData.getPricedItineraries()));
	}

	/**
	 * Inner class to set-up test data
	 */
	private static class TestDataSetup
	{
		public static FareSelectionData createFareSelection()
		{
			return mock(FareSelectionData.class);
		}

		public static List<ScheduledRouteData> createScheduledRoutes(final int amount)
		{
			final List<ScheduledRouteData> scheduledRoutes = new ArrayList<ScheduledRouteData>();
			for (int i = 0; i < amount; i++)
			{
				scheduledRoutes.add(mock(ScheduledRouteData.class));
			}
			return scheduledRoutes;
		}

		private static TravelRouteData createRoute(final String origin, final String destination, final String code)
		{
			final LocationData london = createLocationData(origin);
			final LocationData dubai = createLocationData(destination);

			final TransportFacilityData originAirport = createTransportFacility(london);
			final TransportFacilityData destinationAirport = createTransportFacility(dubai);

			final TravelRouteData lhrDXBRoute = new TravelRouteData();
			lhrDXBRoute.setCode(code);
			lhrDXBRoute.setOrigin(originAirport);
			lhrDXBRoute.setDestination(destinationAirport);
			return lhrDXBRoute;
		}

		private static TransportFacilityData createTransportFacility(final LocationData location)
		{
			/* TRANSPORT FACILITIES */
			final TransportFacilityData lhrAirport = new TransportFacilityData();
			lhrAirport.setLocation(location);
			return lhrAirport;
		}

		private static LocationData createLocationData(final String code)
		{
			final LocationData london = new LocationData();
			london.setCode(code);
			return london;
		}
	}
}
