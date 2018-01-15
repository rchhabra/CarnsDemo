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
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FilterUpgradeBundleHandlerTest
{
	@InjectMocks
	private FilterUpgradeBundleHandler handler;

	@Mock
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	@Test
	public void testHandleWithAvailablePricingInfos()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryPricingInfoData selectedItineraryPricingInfo = testData.createItineraryPricingInfoData(true, "economy",
				true);
		final ItineraryPricingInfoData unSelectedItineraryPricingInfo = testData.createItineraryPricingInfoData(false,
				"economy_plus", true);
		pricedItinerary.setItineraryPricingInfos(
				Stream.of(selectedItineraryPricingInfo, unSelectedItineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateFacade.getSelectedItineraryPricingInfoData(pricedItinerary))
				.thenReturn(selectedItineraryPricingInfo);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy")).thenReturn(0);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy_plus")).thenReturn(1);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(2, CollectionUtils.size(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()));
	}

	@Test
	public void testHandleWithUnAvailablePricingInfos()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryPricingInfoData selectedItineraryPricingInfo = testData.createItineraryPricingInfoData(true, "economy",
				false);
		final ItineraryPricingInfoData unSelectedItineraryPricingInfo = testData.createItineraryPricingInfoData(false,
				"economy_plus", true);
		pricedItinerary.setItineraryPricingInfos(
				Stream.of(selectedItineraryPricingInfo, unSelectedItineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateFacade.getSelectedItineraryPricingInfoData(pricedItinerary))
				.thenReturn(selectedItineraryPricingInfo);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy")).thenReturn(0);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy_plus")).thenReturn(1);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(0, CollectionUtils.size(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()));
	}

	@Test
	public void testHandleWithUnAvailableHigherPricingInfos()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryPricingInfoData selectedItineraryPricingInfo = testData.createItineraryPricingInfoData(true, "economy",
				true);
		final ItineraryPricingInfoData unSelectedItineraryPricingInfo = testData.createItineraryPricingInfoData(false,
				"economy_plus", false);
		pricedItinerary.setItineraryPricingInfos(
				Stream.of(selectedItineraryPricingInfo, unSelectedItineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateFacade.getSelectedItineraryPricingInfoData(pricedItinerary))
				.thenReturn(selectedItineraryPricingInfo);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy")).thenReturn(0);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy_plus")).thenReturn(1);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(1, CollectionUtils.size(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()));
	}

	@Test
	public void testHandleWithPricingInfosOfLowerSequenceNumber()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryPricingInfoData selectedItineraryPricingInfo = testData.createItineraryPricingInfoData(true, "economy",
				true);
		final ItineraryPricingInfoData unSelectedItineraryPricingInfo = testData.createItineraryPricingInfoData(false,
				"economy_minus", true);
		pricedItinerary.setItineraryPricingInfos(
				Stream.of(selectedItineraryPricingInfo, unSelectedItineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateFacade.getSelectedItineraryPricingInfoData(pricedItinerary))
				.thenReturn(selectedItineraryPricingInfo);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy")).thenReturn(1);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy_minus")).thenReturn(0);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(1, CollectionUtils.size(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()));
	}

	class TestDataSetup
	{

		public ItineraryPricingInfoData createItineraryPricingInfoData(final boolean selected, final String bundleType,
				final boolean available)
		{
			final ItineraryPricingInfoData itineraryPricinfInfo = new ItineraryPricingInfoData();
			itineraryPricinfInfo.setSelected(selected);
			itineraryPricinfInfo.setBundleType(bundleType);
			itineraryPricinfInfo.setAvailable(available);
			return itineraryPricinfInfo;
		}
	}

}
