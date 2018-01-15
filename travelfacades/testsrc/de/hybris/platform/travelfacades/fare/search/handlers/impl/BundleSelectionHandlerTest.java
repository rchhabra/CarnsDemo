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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;
import de.hybris.platform.travelservices.order.TravelCartService;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BundleSelectionHandlerTest
{
	@InjectMocks
	BundleSelectionHandler handler;

	@Mock
	private TravelCartService travelCartService;
	@Mock
	private TravelBundleTemplateFacade travelBundleTemplateFacade;
	@Mock
	private TravelBundleTemplateService travelBundleTemplateService;

	@Mock
	private CartModel cart;

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setItineraryPricingInfos(
				Stream.of(testData.createItineraryPricingInfo("economy"), testData.createItineraryPricingInfo("economy_plus"))
						.collect(Collectors.toList()));
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateService.getBundleTemplateIdFromOrder(cart, 0)).thenReturn("economy");
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy")).thenReturn(0);
		Mockito.when(travelBundleTemplateFacade.getSequenceNumber("economy_plus")).thenReturn(1);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertEquals(true, fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).isSelected());

	}

	class TestDataSetup
	{
		public ItineraryPricingInfoData createItineraryPricingInfo(final String bundleType)
		{
			final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
			itineraryPricingInfo.setBundleType(bundleType);
			return itineraryPricingInfo;
		}
	}

}
