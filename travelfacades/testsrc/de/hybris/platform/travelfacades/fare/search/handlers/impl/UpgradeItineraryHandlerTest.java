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
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;

import java.util.ArrayList;
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
public class UpgradeItineraryHandlerTest
{
	@InjectMocks
	UpgradeItineraryHandler handler;

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSearchRequestData fareSearchRequestData = testData.createFareSearchRequest();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(new ArrayList<PricedItineraryData>());
		handler.handle(Collections.emptyList(), fareSearchRequestData, fareSelectionData);
		assertEquals(1, CollectionUtils.size(fareSelectionData.getPricedItineraries()));

	}

	class TestDataSetup
	{
		public FareSearchRequestData createFareSearchRequest()
		{
			final FareSearchRequestData fareSearchRequest = new FareSearchRequestData();
			fareSearchRequest.setOriginDestinationInfo(Stream.of(createOriginDestinationInfoData()).collect(Collectors.toList()));
			return fareSearchRequest;
		}

		private OriginDestinationInfoData createOriginDestinationInfoData()
		{
			final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
			final ItineraryData itinerary = new ItineraryData();
			originDestinationInfo.setItinerary(itinerary);
			originDestinationInfo.setReferenceNumber(0);
			return originDestinationInfo;
		}
	}
}
