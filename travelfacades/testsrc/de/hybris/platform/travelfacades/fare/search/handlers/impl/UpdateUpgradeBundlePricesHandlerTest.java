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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link UpdateUpgradeBundlePricesHandler}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateUpgradeBundlePricesHandlerTest
{
	@InjectMocks
	private UpdateUpgradeBundlePricesHandler handler;

	@Mock
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	private final ItineraryPricingInfoData selectedItinerayPricingInfo = new ItineraryPricingInfoData();

	@Before
	public void prepare()
	{
		selectedItinerayPricingInfo.setSelected(true);
		final TotalFareData totalFare = new TotalFareData();
		final PriceData totalPrice = new PriceData();
		totalPrice.setValue(BigDecimal.valueOf(100d));
		totalFare.setTotalPrice(totalPrice);
		selectedItinerayPricingInfo.setTotalFare(totalFare);
	}

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryPricingInfoData itineraryPricingInfo1 = testData.createItineraryPricingInfoData(100d, true);
		final ItineraryPricingInfoData itineraryPricingInfo2 = testData.createItineraryPricingInfoData(150d, false);
		pricedItinerary
				.setItineraryPricingInfos(Stream.of(itineraryPricingInfo1, itineraryPricingInfo2).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateFacade.getSelectedItineraryPricingInfoData(pricedItinerary))
				.thenReturn(itineraryPricingInfo1);
		Mockito.doNothing().when(travelBundleTemplateFacade).createUpgradeItineraryPricingInfoTotalPriceData(
				Matchers.any(BigDecimal.class), Matchers.any(ItineraryPricingInfoData.class));
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		Mockito.verify(travelBundleTemplateFacade, Mockito.times(2)).createUpgradeItineraryPricingInfoTotalPriceData(
				Matchers.any(BigDecimal.class), Matchers.any(ItineraryPricingInfoData.class));
	}

	class TestDataSetup
	{

		public ItineraryPricingInfoData createItineraryPricingInfoData(final double totalValue, final boolean selected)
		{
			final ItineraryPricingInfoData itineraryPricinfInfo = new ItineraryPricingInfoData();
			final TotalFareData totalFare = new TotalFareData();
			final PriceData totalPrice = new PriceData();
			totalPrice.setValue(BigDecimal.valueOf(totalValue));
			totalFare.setTotalPrice(totalPrice);
			itineraryPricinfInfo.setTotalFare(totalFare);
			itineraryPricinfInfo.setSelected(selected);
			return itineraryPricinfInfo;
		}
	}
}
