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
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
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
 * Unit Test for {@link PriceSortingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PriceSortingStrategyTest
{
	@InjectMocks
	PriceSortingStrategy priceSortingStrategy;

	private static final String TEST_ROUTE = "LDG-GWD";
	private final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> pIWithSamePrice = new ArrayList<>();
	private final List<PricedItineraryData> expectedPricedItineraries = new ArrayList<>();
	private final List<PricedItineraryData> ePIWithSamePrice = new ArrayList<>();

	@Before
	public void setUp()
	{
		setUpFareSelectionDataforSorting(pricedItineraries);
		setUpExpectedFareSelectionData(expectedPricedItineraries);
		setUpFareSelectionDataWithSamePrice(pIWithSamePrice);
		setUpExpectedFareSelectionDataWithSamePrice(ePIWithSamePrice);
	}

	private void setUpFareSelectionDataforSorting(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryPricingInfoData itineraryInfoData11 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData12 = createItineraryInfoData(30L);
		final ItineraryPricingInfoData itineraryInfoData21 = createItineraryInfoData(60L);
		final ItineraryPricingInfoData itineraryInfoData22 = createItineraryInfoData(10L);
		final ItineraryPricingInfoData itineraryInfoData23 = createItineraryInfoData(20L);
		final ItineraryPricingInfoData itineraryInfoData31 = createItineraryInfoData(70L);


		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData11 = new TransportOfferingData();
		final TransportOfferingData toData21 = new TransportOfferingData();
		final TransportOfferingData toData31 = new TransportOfferingData();

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
		pricedItinerary1.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData11, itineraryInfoData12 }));
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary2.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData21, itineraryInfoData22, itineraryInfoData23 }));
		pricedItinerary3.setItinerary(itineraryData3);
		pricedItinerary3.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData31 }));

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpExpectedFareSelectionData(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryPricingInfoData itineraryInfoData11 = createItineraryInfoData(60L);
		final ItineraryPricingInfoData itineraryInfoData12 = createItineraryInfoData(10L);
		final ItineraryPricingInfoData itineraryInfoData13 = createItineraryInfoData(20L);
		final ItineraryPricingInfoData itineraryInfoData21 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData22 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData31 = createItineraryInfoData(70L);


		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData11 = new TransportOfferingData();
		final TransportOfferingData toData21 = new TransportOfferingData();
		final TransportOfferingData toData31 = new TransportOfferingData();

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
		pricedItinerary1.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData11, itineraryInfoData12, itineraryInfoData13 }));
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary2.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData21, itineraryInfoData22 }));
		pricedItinerary3.setItinerary(itineraryData3);
		pricedItinerary3.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData31 }));

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpFareSelectionDataWithSamePrice(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryPricingInfoData itineraryInfoData1 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData2 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData3 = createItineraryInfoData(50L);


		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData11 = createTransportOfferingData(3);
		final TransportOfferingData toData21 = createTransportOfferingData(4);
		final TransportOfferingData toData31 = createTransportOfferingData(0);

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
		pricedItinerary1.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData1 }));
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary2.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData2 }));
		pricedItinerary3.setItinerary(itineraryData3);
		pricedItinerary3.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData3 }));

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}

	private void setUpExpectedFareSelectionDataWithSamePrice(final List<PricedItineraryData> pricedItineraries)
	{
		final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary2 = new PricedItineraryData();
		final PricedItineraryData pricedItinerary3 = new PricedItineraryData();

		final ItineraryPricingInfoData itineraryInfoData1 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData2 = createItineraryInfoData(50L);
		final ItineraryPricingInfoData itineraryInfoData3 = createItineraryInfoData(50L);


		final ItineraryData itineraryData1 = new ItineraryData();
		final ItineraryData itineraryData2 = new ItineraryData();
		final ItineraryData itineraryData3 = new ItineraryData();

		final OriginDestinationOptionData odOptionData1 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData2 = createOriginDestinationOptionData(TEST_ROUTE);
		final OriginDestinationOptionData odOptionData3 = createOriginDestinationOptionData(TEST_ROUTE);

		final TransportOfferingData toData11 = createTransportOfferingData(0);
		final TransportOfferingData toData21 = createTransportOfferingData(3);
		final TransportOfferingData toData31 = createTransportOfferingData(4);

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
		pricedItinerary1.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData1 }));
		pricedItinerary2.setItinerary(itineraryData2);
		pricedItinerary2.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData2 }));
		pricedItinerary3.setItinerary(itineraryData3);
		pricedItinerary3.setItineraryPricingInfos(Arrays.asList(new ItineraryPricingInfoData[]
		{ itineraryInfoData3 }));

		pricedItineraries.add(pricedItinerary1);
		pricedItineraries.add(pricedItinerary2);
		pricedItineraries.add(pricedItinerary3);
	}


	public ItineraryPricingInfoData createItineraryInfoData(final long val)
	{
		final ItineraryPricingInfoData itineraryInfoData = new ItineraryPricingInfoData();
		itineraryInfoData.setAvailable(Boolean.TRUE);
		final TotalFareData totalFareData = new TotalFareData();
		final PriceData priceData = new PriceData();
		priceData.setValue(java.math.BigDecimal.valueOf(val));
		totalFareData.setTotalPrice(priceData);
		itineraryInfoData.setTotalFare(totalFareData);
		return itineraryInfoData;
	}

	public OriginDestinationOptionData createOriginDestinationOptionData(final String routeCode)
	{
		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setTravelRouteCode(TEST_ROUTE);
		return odOptionData;
	}

	public TransportOfferingData createTransportOfferingData(final int diff)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		toData.setDepartureTime(TravelDateUtils.addHours(new Date(), diff));
		toData.setDepartureTimeZoneId(ZoneId.systemDefault());
		return toData;
	}

	@Test
	public void testSortFareSelectionDataOnPrice()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pricedItineraries);
		priceSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				expectedPricedItineraries.get(1).getItineraryPricingInfos().get(0).getTotalFare().getTotalPrice().getValue(),
				pricedItineraries.get(1).getItineraryPricingInfos().get(0).getTotalFare().getTotalPrice().getValue());
	}

	@Test
	public void testSortFareSelectionDataWithSamePrice()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pIWithSamePrice);
		priceSortingStrategy.sortFareSelectionData(fareSelectionData);
		Assert.assertEquals(
				ePIWithSamePrice.get(2).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString(),
				pIWithSamePrice.get(2).getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0)
						.getDepartureTime().toString());
	}
}
