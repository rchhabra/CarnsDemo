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
 * Unit Test for {@link NumberOfStopsSortingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NumberOfStopsSortingStrategyTest
{
	@InjectMocks
	NumberOfStopsSortingStrategy numberOfStopsSortingStrategy;

	private static final String TEST_ROUTE = "LDG-GWD";
	private final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> pIWithSameStops = new ArrayList<>();
	private final List<PricedItineraryData> expectedPricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> ePIWithSameStops = new ArrayList<>();

	@Before
	public void setUp()
	{
		setUpFareSelectionDataforSorting(pricedItineraries);
		setUpExpectedFareSelectionData(expectedPricedItineraries);
		setUpFareSelectionDataWithSameStops(pIWithSameStops);
		setUpExpectedFareSelectionDataWithSameStops(ePIWithSameStops);
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

		final TransportOfferingData toData11 = new TransportOfferingData();
		final TransportOfferingData toData12 = new TransportOfferingData();
		final TransportOfferingData toData13 = new TransportOfferingData();
		final TransportOfferingData toData21 = new TransportOfferingData();
		final TransportOfferingData toData31 = new TransportOfferingData();
		final TransportOfferingData toData32 = new TransportOfferingData();

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData11, toData12, toData13 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData21 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData31, toData32 }));

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

		final TransportOfferingData toData11 = new TransportOfferingData();
		final TransportOfferingData toData21 = new TransportOfferingData();
		final TransportOfferingData toData22 = new TransportOfferingData();
		final TransportOfferingData toData31 = new TransportOfferingData();
		final TransportOfferingData toData32 = new TransportOfferingData();
		final TransportOfferingData toData33 = new TransportOfferingData();

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData11 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData21, toData22 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData31, toData32, toData33 }));

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

	private void setUpExpectedFareSelectionDataWithSameStops(final List<PricedItineraryData> pricedItineraries)
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

		final TransportOfferingData toData11 = createTransportOfferingData(1);
		final TransportOfferingData toData21 = createTransportOfferingData(2);
		final TransportOfferingData toData31 = createTransportOfferingData(5);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData11 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData21 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData31 }));

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

	private void setUpFareSelectionDataWithSameStops(final List<PricedItineraryData> pricedItineraries)
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

		final TransportOfferingData toData11 = createTransportOfferingData(2);
		final TransportOfferingData toData21 = createTransportOfferingData(1);
		final TransportOfferingData toData31 = createTransportOfferingData(5);

		odOptionData1.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData11 }));
		odOptionData2.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData21 }));
		odOptionData3.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ toData31 }));

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

	public TransportOfferingData createTransportOfferingData(final int depVal)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		toData.setDepartureTime(TravelDateUtils.addHours(new Date(), depVal));
		toData.setDepartureTimeZoneId(ZoneId.systemDefault());
		return toData;
	}

	@Test
	public void testSortFareSelectionDataOnNumberOfStops()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pricedItineraries);
		numberOfStopsSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				expectedPricedItineraries.get(1).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().size(),
				pricedItineraries.get(1).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().size());
	}

	@Test
	public void testSortFareSelectionDataWithSameStops()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pIWithSameStops);
		numberOfStopsSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				ePIWithSameStops.get(1).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString(),
				pIWithSameStops.get(1).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString());
	}

}
