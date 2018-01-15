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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageFilterBundlesHandlerTest
{
	@InjectMocks
	PackageFilterBundlesHandler handler;

	@Test
	public void testHandleWithEmptyPricedItineraries()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Collections.emptyList());
		handler.handle(Collections.emptyList(), null, fareSelectionData);
	}

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData unAvailablePricedItinerary = testData.createPricedItinerary(0, false, false, false);
		final PricedItineraryData pricedItineraryWithAvailableItinerayPricingInfo = testData.createPricedItinerary(0, true, true,
				true);

		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream
				.of(unAvailablePricedItinerary, pricedItineraryWithAvailableItinerayPricingInfo)
				.collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(1, CollectionUtils.size(fareSelectionData.getPricedItineraries()));

	}

	@Test
	public void testHandleWithUnavailableItineraryPricingInfo()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItineraryWithUnavailableItineraryPricingInfo = testData.createPricedItinerary(0, true,
				false, true);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData
				.setPricedItineraries(Stream.of(pricedItineraryWithUnavailableItineraryPricingInfo).collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(0, CollectionUtils.size(fareSelectionData.getPricedItineraries()));
	}

	@Test
	public void testHandleWithUnavailableBundleTemplate()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItineraryWithUnAvaiableBundleTemplate = testData.createPricedItinerary(0, true, true,
				false);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData
				.setPricedItineraries(Stream.of(pricedItineraryWithUnAvaiableBundleTemplate).collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(0, CollectionUtils.size(fareSelectionData.getPricedItineraries()));
	}

	@Test
	public void testHandleWithUnavailablePricedItineraries()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData unAvailablePricedItinerary = testData.createPricedItinerary(0, false, false, false);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream
				.of(unAvailablePricedItinerary)
				.collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(0, CollectionUtils.size(fareSelectionData.getPricedItineraries()));
	}

	class TestDataSetup
	{
		public PricedItineraryData createPricedItinerary(final int orgDesRefNum, final boolean availablePricedItinerary,
				final boolean availableitineraryPricingInfo, final boolean availableBundleTemplate)
		{
			final PricedItineraryData pricedItinerary = new PricedItineraryData();
			pricedItinerary.setAvailable(availablePricedItinerary);
			pricedItinerary.setOriginDestinationRefNumber(orgDesRefNum);
			final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
			itineraryPricingInfo.setAvailable(availableitineraryPricingInfo);
			pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
			final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
			bundleTemplate.setAvailable(availableBundleTemplate);
			itineraryPricingInfo.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
			return pricedItinerary;
		}
	}
}
