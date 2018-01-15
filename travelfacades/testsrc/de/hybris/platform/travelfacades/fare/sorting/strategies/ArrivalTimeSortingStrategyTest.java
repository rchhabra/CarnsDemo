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
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for {@link ArrivalTimeSortingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ArrivalTimeSortingStrategyTest
{
	@InjectMocks
	ArrivalTimeSortingStrategy arrivalTimeSortingStrategy;

	private static final String TEST_ROUTE = "LDG-GWD";

	private final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> pricedItinerariesWithSameArrivalTime = new ArrayList<>();
	private final List<PricedItineraryData> expectedPricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> expectedPricedItinerariesWithSameArrivalTime = new ArrayList<>();

	@Before
	public void setUp()
	{
		setUpFareSelectionDataforSorting(pricedItineraries);
		setUpExpectedFareSelectionData(expectedPricedItineraries);
		setUpFareSelectionDataWithSameArrivalTime(pricedItinerariesWithSameArrivalTime);
		setUpExpectedFareSelectionDataWithSameArrivalTime(expectedPricedItinerariesWithSameArrivalTime);
	}

	private void setUpFareSelectionDataforSorting(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingData(2);
		final TransportOfferingData toData2 = createTransportOfferingData(1);
		final TransportOfferingData toData3 = createTransportOfferingData(4);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData3 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));
		itineraryData3.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData3 }));

		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary3.setItinerary(itineraryData3);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpExpectedFareSelectionData(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingData(1);
		final TransportOfferingData toData2 = createTransportOfferingData(2);
		final TransportOfferingData toData3 = createTransportOfferingData(4);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData3 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));
		itineraryData3.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData3 }));

		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary3.setItinerary(itineraryData3);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpFareSelectionDataWithSameArrivalTime(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingDataWIthSameArrivalTime(2);
		final TransportOfferingData toData2 = createTransportOfferingDataWIthSameArrivalTime(1);
		final TransportOfferingData toData3 = createTransportOfferingDataWIthSameArrivalTime(4);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData3 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));
		itineraryData3.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData3 }));

		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary3.setItinerary(itineraryData3);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpExpectedFareSelectionDataWithSameArrivalTime(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData1 = createTransportOfferingDataWIthSameArrivalTime(1);
		final TransportOfferingData toData2 = createTransportOfferingDataWIthSameArrivalTime(2);
		final TransportOfferingData toData3 = createTransportOfferingDataWIthSameArrivalTime(4);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData1 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData2 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData3 }));

		itineraryData1.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData1 }));
		itineraryData2.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData2 }));
		itineraryData3.setOriginDestinationOptions(Arrays.asList(new OriginDestinationOptionData[]
		{ odOptionData3 }));

		pricedItinerary1.setItinerary(itineraryData1);
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary3.setItinerary(itineraryData3);

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	public OriginDestinationOptionData createOriginDestinationOptionData(final String routeCode)
	{
		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setTravelRouteCode(TEST_ROUTE);
		return odOptionData;
	}

	public TransportOfferingData createTransportOfferingData(final int arrDiff)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		toData.setArrivalTime(TravelDateUtils.addHours(new Date(), arrDiff));
		return toData;
	}

	public TransportOfferingData createTransportOfferingDataWIthSameArrivalTime(final int val)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		final Date date = TravelDateUtils.getDateWithTime(new Date(), "21/06/1970 00:00:00",
				TravelservicesConstants.DATE_TIME_PATTERN);
		toData.setArrivalTime(TravelDateUtils.addHours(date, 5));
		toData.setDepartureTime(TravelDateUtils.addHours(date, val));
		toData.setDepartureTimeZoneId(ZoneId.systemDefault());
		return toData;
	}

	@Test
	public void testSortDataOnArrivalTime()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pricedItineraries);
		arrivalTimeSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				expectedPricedItineraries.get(0).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getArrivalTime().toString(),
				pricedItineraries.get(0).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getArrivalTime().toString());
	}


	@Test
	public void testSortDataWithSameArrivalTime()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pricedItinerariesWithSameArrivalTime);
		arrivalTimeSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				expectedPricedItinerariesWithSameArrivalTime.get(0).getItinerary().getOriginDestinationOptions().get(0)
						.getTransportOfferings().get(0).getDepartureTime().toString(),
				pricedItinerariesWithSameArrivalTime.get(0).getItinerary().getOriginDestinationOptions().get(0)
						.getTransportOfferings().get(0).getDepartureTime().toString());
	}

}
