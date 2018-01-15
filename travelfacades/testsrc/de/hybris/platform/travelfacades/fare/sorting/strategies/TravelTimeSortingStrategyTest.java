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

package de.hybris.platform.travelfacades.fare.sorting.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for {@link TravelTimeSortingStrategy}
 */


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelTimeSortingStrategyTest
{
	@InjectMocks
	TravelTimeSortingStrategy travelTimeSortingStrategy;

	private static final String TEST_ROUTE = "LDG-GWD";

	private static final String TRANSPORT_OFFERING_STATUS_RESULT_MINUTES = "transport.offering.status.result.minutes";
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_HOURS = "transport.offering.status.result.hours";
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_DAYS = "transport.offering.status.result.days";

	private final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> expectedPricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> pIWithSameDuration = new ArrayList<>();
	private final List<PricedItineraryData> eIWithSameDuration = new ArrayList<>();

	@Mock
	TransportOfferingUtils transportOfferingUtils;

	@Before
	public void setUp()
	{
		setUpPricedItineraries(pricedItineraries);
		setUpExpectedFareSelectionData(expectedPricedItineraries);
		setUpPricedItinerariesWithSameDuration(pIWithSameDuration);
		setUpExpectedItinerariesWithSameDuration(eIWithSameDuration);

	}

	private void setUpPricedItineraries(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();

		final ItineraryData itineraryData1 = createItineraryData(24000000L);
		final ItineraryData itineraryData2 = createItineraryData(18000000L);

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));


		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);

	}

	private void setUpExpectedFareSelectionData(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();

		final ItineraryData itineraryData1 = createItineraryData(18000000L);
		final ItineraryData itineraryData2 = createItineraryData(24000000L);

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));

		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);

	}

	private void setUpPricedItinerariesWithSameDuration(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();

		final ItineraryData itineraryData1 = createItineraryData(18000000L);
		final ItineraryData itineraryData2 = createItineraryData(18000000L);

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingData(2);
		final TransportOfferingData toData2 = createTransportOfferingData(1);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));


		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);

	}

	private void setUpExpectedItinerariesWithSameDuration(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();

		final ItineraryData itineraryData1 = createItineraryData(18000000L);
		final ItineraryData itineraryData2 = createItineraryData(18000000L);

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingData(1);
		final TransportOfferingData toData2 = createTransportOfferingData(2);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));


		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);

	}

	public OriginDestinationOptionData createOriginDestinationOptionData(final String routeCode)
	{
		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setTravelRouteCode(TEST_ROUTE);
		return odOptionData;
	}

	public ItineraryData createItineraryData(final Long duration)
	{
		final ItineraryData itineraryData = new ItineraryData();
		final Map<String, Integer> durationMap = getDurationMap(duration);
		itineraryData.setDuration(durationMap);
		return itineraryData;
	}

	public static Map<String, Integer> getDurationMap(final Long duration)
	{
		final Long durationInMinutes = duration / (1000 * 60);
		final Map<String, Integer> durationMap = new LinkedHashMap<>();
		final int days = (int) (durationInMinutes / 60) / 24;
		if (days > 0)
		{
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_DAYS, days);
		}
		final int hours = (int) (durationInMinutes / 60) % 24;
		if (hours > 0)
		{
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_HOURS, hours);
		}
		durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_MINUTES, (int) (durationInMinutes % 60));

		return durationMap;
	}

	public TransportOfferingData createTransportOfferingData(final int depVal)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		toData.setDepartureTime(TravelDateUtils.addHours(new Date(), depVal));
		toData.setDepartureTimeZoneId(ZoneId.systemDefault());
		return toData;
	}

	@Test
	public void testSortDataWithSameArrivalTime()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pIWithSameDuration);
		travelTimeSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				eIWithSameDuration.get(0).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString(),
				pIWithSameDuration.get(0).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString());
	}
}
