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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link FareRulesHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareRulesHandlerTest
{
	@InjectMocks
	private FareRulesHandler handler;

	@Mock
	private TravelRulesService travelRulesService;
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testWithDroolsDisabled()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
	}

	@Test
	public void testWithoutPricedItineraries()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Collections.EMPTY_LIST);
		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
	}

	@Test
	public void testWithPricedItineraries()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setAvailable(true);
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setAvailable(true);
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		final List<FareProductData> fareProducts = new ArrayList<>();
		final FareProductData fareProduct1 = new FareProductData();
		fareProduct1.setCode("fareProduct1");
		fareProducts.add(fareProduct1);
		final FareProductData fareProduct2 = new FareProductData();
		fareProduct2.setCode("fareProduct2");
		fareProducts.add(fareProduct2);
		bundleTemplate.setFareProducts(fareProducts);
		itineraryPricingInfo.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));

		final List<FareProductData> excludedFareProducts = new ArrayList<>();
		final FareProductData excludedFareProduct = new FareProductData();
		excludedFareProduct.setCode("fareProduct2");
		excludedFareProducts.add(excludedFareProduct);

		given(travelRulesService.filterFareProducts(Matchers.anyList(), Matchers.any())).willReturn(excludedFareProducts);

		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
		Assert.assertTrue(CollectionUtils.isNotEmpty(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()
				.get(0).getBundleTemplates().get(0).getFareProducts()));
	}

	@Test
	public void testWithEmptyFareProducts()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setAvailable(true);
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setAvailable(true);
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Collections.EMPTY_LIST);
		itineraryPricingInfo.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));
		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
	}

	@Test
	public void testWithNotAvailablePricedInfo()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setAvailable(true);
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setAvailable(false);
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		final List<FareProductData> fareProducts = new ArrayList<>();
		final FareProductData fareProduct1 = new FareProductData();
		fareProduct1.setCode("fareProduct1");
		fareProducts.add(fareProduct1);
		final FareProductData fareProduct2 = new FareProductData();
		fareProduct2.setCode("fareProduct2");
		fareProducts.add(fareProduct2);
		bundleTemplate.setFareProducts(fareProducts);
		itineraryPricingInfo.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));

		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
	}

	@Test
	public void testWithNotAvailablePricedItinerary()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setAvailable(false);
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setAvailable(false);
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		final List<FareProductData> fareProducts = new ArrayList<>();
		final FareProductData fareProduct1 = new FareProductData();
		fareProduct1.setCode("fareProduct1");
		fareProducts.add(fareProduct1);
		final FareProductData fareProduct2 = new FareProductData();
		fareProduct2.setCode("fareProduct2");
		fareProducts.add(fareProduct2);
		bundleTemplate.setFareProducts(fareProducts);
		itineraryPricingInfo.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		pricedItinerary.setItineraryPricingInfos(Stream.of(itineraryPricingInfo).collect(Collectors.toList()));
		fareSelectionData.setPricedItineraries(Stream.of(pricedItinerary).collect(Collectors.toList()));

		final ScheduledRouteData scheduledData = new ScheduledRouteData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		handler.handle(Stream.of(scheduledData).collect(Collectors.toList()), fareSearchRequestData, fareSelectionData);
	}

}
