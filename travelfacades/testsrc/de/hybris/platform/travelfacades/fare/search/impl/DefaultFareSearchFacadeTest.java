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

package de.hybris.platform.travelfacades.fare.search.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFareSearchFacadeTest
{
	@Mock
	private FareSearchPipelineManager fareSearchPipelineManager;

	@Mock
	private TransportOfferingFacade transportOfferingFacade;

	@Mock
	private FareSelectionData fareSelectionData;

	@InjectMocks
	DefaultFareSearchFacade fareSearchFacade = new DefaultFareSearchFacade();

	@Test
	public void testDoSearch()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final FareSearchRequestData fareSearchReqData = testDataSetUp.createFareSearchRequestData();
		final ScheduledRouteData scheduleData = testDataSetUp.createScheduledRouteData();
		final List<ScheduledRouteData> schedules = Stream.of(scheduleData).collect(Collectors.toList());
		given(transportOfferingFacade.getScheduledRoutes(fareSearchReqData)).willReturn(schedules);
		when(fareSearchPipelineManager.executePipeline(Matchers.eq(schedules), Matchers.eq(fareSearchReqData)))
				.thenReturn(fareSelectionData);
		assertNotNull(fareSearchFacade.doSearch(fareSearchReqData));
	}

	@Test
	public void testGetRemainingSeats()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final FareDetailsData fareDetails1 = testDataSetUp.createFareDetailsData(10l);
		final FareDetailsData fareDetails11 = testDataSetUp.createFareDetailsData(0l);
		final List<FareDetailsData> fareDetailsList1 = Stream.of(fareDetails1, fareDetails11).collect(Collectors.toList());
		final FareDetailsData fareDetails2 = testDataSetUp.createFareDetailsData(10l);
		final List<FareDetailsData> fareDetailsList2 = Stream.of(fareDetails2).collect(Collectors.toList());

		final FareInfoData fareInfo1 = testDataSetUp.createfareInfoData(fareDetailsList1);
		final FareInfoData fareInfo2 = testDataSetUp.createfareInfoData(fareDetailsList2);
		final List<FareInfoData> fareInfos1 = Stream.of(fareInfo1).collect(Collectors.toList());
		final List<FareInfoData> fareInfos2 = Stream.of(fareInfo2).collect(Collectors.toList());

		final PTCFareBreakdownData ptcFare1 = testDataSetUp.createPTCFareBreakdownData(fareInfos1);
		final PTCFareBreakdownData ptcFare2 = testDataSetUp.createPTCFareBreakdownData(fareInfos2);
		final List<PTCFareBreakdownData> ptcFares1 = Stream.of(ptcFare1).collect(Collectors.toList());
		final List<PTCFareBreakdownData> ptcFares2 = Stream.of(ptcFare2).collect(Collectors.toList());

		final ItineraryPricingInfoData itinPricingInfoData1 = testDataSetUp.createItineraryPricingInfoData(true, ptcFares1,
				"Economy");
		final ItineraryPricingInfoData itinPricingInfoData2 = testDataSetUp.createItineraryPricingInfoData(false, ptcFares2,
				"Primier");
		final List<ItineraryPricingInfoData> itinPricingInfos = Stream.of(itinPricingInfoData1, itinPricingInfoData2)
				.collect(Collectors.toList());

		final PricedItineraryData pricedItin = testDataSetUp.createPricedItineraryData(1, itinPricingInfos);
		final List<PricedItineraryData> pricedItineraries = Stream.of(pricedItin).collect(Collectors.toList());

		final FareSelectionData fareSelectionData = testDataSetUp.createFareSelectionData(pricedItineraries);
		fareSearchFacade.getRemainingSeats(fareSelectionData);
	}

	private class TestDataSetUp
	{
		private FareSearchRequestData createFareSearchRequestData()
		{
			final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
			fareSearchRequestData.setTripType(TripType.RETURN);
			return fareSearchRequestData;
		}

		private ScheduledRouteData createScheduledRouteData()
		{
			final ScheduledRouteData scheduleData = new ScheduledRouteData();
			scheduleData.setReferenceNumber(1);
			return scheduleData;
		}

		private FareProductData createFareProductData(final long stockLevel)
		{
			final FareProductData fare = new FareProductData();
			final StockData stock = new StockData();
			stock.setStockLevel(stockLevel);
			fare.setStock(stock);
			return fare;
		}

		private FareDetailsData createFareDetailsData(final long stockLevel)
		{
			final FareDetailsData fareDetails = new FareDetailsData();
			fareDetails.setFareProduct(createFareProductData(stockLevel));
			return fareDetails;
		}

		private FareInfoData createfareInfoData(final List<FareDetailsData> fareDetails)
		{
			final FareInfoData fareInfo = new FareInfoData();
			fareInfo.setFareDetails(fareDetails);
			return fareInfo;
		}

		private PTCFareBreakdownData createPTCFareBreakdownData(final List<FareInfoData> fareInfos)
		{
			final PTCFareBreakdownData ptcFareBreakDown = new PTCFareBreakdownData();
			ptcFareBreakDown.setFareInfos(fareInfos);
			return ptcFareBreakDown;
		}

		private ItineraryPricingInfoData createItineraryPricingInfoData(final boolean isAvailable,
				final List<PTCFareBreakdownData> ptcFareBreakDown, final String bundleType)
		{
			final ItineraryPricingInfoData itinData = new ItineraryPricingInfoData();
			itinData.setAvailable(isAvailable);
			itinData.setPtcFareBreakdownDatas(ptcFareBreakDown);
			itinData.setBundleType(bundleType);
			return itinData;
		}

		private PricedItineraryData createPricedItineraryData(final int id, final List<ItineraryPricingInfoData> itinPricingInfo)
		{
			final PricedItineraryData priceItinData = new PricedItineraryData();
			priceItinData.setId(id);
			priceItinData.setItineraryPricingInfos(itinPricingInfo);
			return priceItinData;
		}

		private FareSelectionData createFareSelectionData(final List<PricedItineraryData> priceditins)
		{
			final FareSelectionData fareSelectionData = new FareSelectionData();
			fareSelectionData.setPricedItineraries(priceditins);
			return fareSelectionData;
		}
	}

}
