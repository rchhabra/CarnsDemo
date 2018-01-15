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
public class DealFilterBundlesHandlerTest
{
	@InjectMocks
	DealFilterBundlesHandler handler;


	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData unAvailablePricedItinerary = testData.createPricedItinerary(0, false, false);
		final PricedItineraryData outboundAvailablePricedItinerary = testData.createPricedItinerary(0, true, true);
		final PricedItineraryData inboundUnAvailablePricedItinerary = testData.createPricedItinerary(1, true, false);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(
				Stream.of(unAvailablePricedItinerary, outboundAvailablePricedItinerary, inboundUnAvailablePricedItinerary)
						.collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(2, CollectionUtils.size(fareSelectionData.getPricedItineraries()));
	}

	class TestDataSetup
	{
		public PricedItineraryData createPricedItinerary(final int orgDesRefNum, final boolean availablePricedItinerary,
				final boolean availableitineraryPricingInfo)
		{
			final PricedItineraryData pricedItinerary = new PricedItineraryData();
			pricedItinerary.setAvailable(availablePricedItinerary);
			pricedItinerary.setOriginDestinationRefNumber(orgDesRefNum);
			final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
			itineraryPricingInfo.setAvailable(availableitineraryPricingInfo);
			pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
			return pricedItinerary;
		}
	}
}
