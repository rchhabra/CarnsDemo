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

package de.hybris.platform.ndcfacades.resolvers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.model.NDCOfferMappingModel;
import de.hybris.platform.ndcservices.services.NDCOfferMappingService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCOfferItemIdResolverTest
{
	@InjectMocks
	DefaultNDCOfferItemIdResolver defaultNDCOfferItemIdResolver;

	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private ModelService modelService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	@Mock
	private PassengerTypeService passengerTypeService;
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private NDCOfferMappingService ndcOfferMappingService;

	@Test
	public void testGetNDCOfferItemIdFromString() throws NDCOrderException
	{
		final String offerItemID = "offerItemID";

		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.NDC_OFFER_ITEM_ID_MAPPING))
				.thenReturn("true");
		final NDCOfferMappingModel ndcOfferMappingModel = new NDCOfferMappingModel();
		ndcOfferMappingModel.setNDCOfferItemID("PTC|0|LTN_CDG|BUNDLE1#BUNDLE2");
		Mockito.when(ndcOfferMappingService.getNDCOfferMappingFromCode(offerItemID)).thenReturn(ndcOfferMappingModel);

		Assert.assertNotNull(defaultNDCOfferItemIdResolver.getNDCOfferItemIdFromString(offerItemID));
	}

	@Test(expected = NDCOrderException.class)
	public void testGetNDCOfferItemIdFromStringWithException() throws NDCOrderException
	{
		final String offerItemID = "offerItemID";

		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.NDC_OFFER_ITEM_ID_MAPPING))
				.thenReturn("true");
		final NDCOfferMappingModel ndcOfferMappingModel = new NDCOfferMappingModel();
		ndcOfferMappingModel.setNDCOfferItemID("WRONG_NDC_OFFER_ITEMID");
		Mockito.when(ndcOfferMappingService.getNDCOfferMappingFromCode(offerItemID)).thenReturn(ndcOfferMappingModel);

		defaultNDCOfferItemIdResolver.getNDCOfferItemIdFromString(offerItemID);
	}

	@Test
	public void testGenerateAirShoppingNDCOfferItemId()
	{
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setOriginDestinationRefNumber(0);
		final ItineraryData itinerary = new ItineraryData();
		final TravelRouteData route = new TravelRouteData();
		route.setCode("LTN_CDG");
		itinerary.setRoute(route);
		pricedItinerary.setItinerary(itinerary);

		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode("OOW");
		final List<FareProductData> fareProducts = Collections.singletonList(fareProduct);
		bundleTemplate.setFareProducts(fareProducts);
		bundleTemplate.setId("bundleTemplateId");
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("8823");
		final List<TransportOfferingData> transportOfferings = Collections.singletonList(transportOffering);
		bundleTemplate.setTransportOfferings(transportOfferings);

		final List<TravelBundleTemplateData> bundleTemplates = Collections.singletonList(bundleTemplate);
		itineraryPricingInfo.setBundleTemplates(bundleTemplates);

		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setNdcCode("adult");

		Assert.assertTrue(StringUtils
				.isNotEmpty(defaultNDCOfferItemIdResolver.generateAirShoppingNDCOfferItemId(pricedItinerary, itineraryPricingInfo)));
	}

	@Test
	public void testGenerateAirShoppingNDCOfferItemIdWithPTCFareBreakdownData()
	{
		final PTCFareBreakdownData ptcFareBreakdownData = new PTCFareBreakdownData();
		final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode("adult");
		passengerTypeQuantity.setPassengerType(passengerType);
		ptcFareBreakdownData.setPassengerTypeQuantity(passengerTypeQuantity);

		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setOriginDestinationRefNumber(0);
		final ItineraryData itinerary = new ItineraryData();
		final TravelRouteData route=new TravelRouteData();
		route.setCode("LTN_CDG");
		itinerary.setRoute(route);
		pricedItinerary.setItinerary(itinerary);

		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode("OOW");
		final List<FareProductData> fareProducts = Collections.singletonList(fareProduct);
		bundleTemplate.setFareProducts(fareProducts);
		bundleTemplate.setId("bundleTemplateId");
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("8823");
		final List<TransportOfferingData> transportOfferings = Collections.singletonList(transportOffering);
		bundleTemplate.setTransportOfferings(transportOfferings);

		final List<TravelBundleTemplateData> bundleTemplates = Collections.singletonList(bundleTemplate);
		itineraryPricingInfo.setBundleTemplates(bundleTemplates);

		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setNdcCode("adult");

		Mockito.when(passengerTypeService
				.getPassengerType(ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode()))
				.thenReturn(passengerTypeModel);

		Assert.assertTrue(StringUtils.isNotEmpty(defaultNDCOfferItemIdResolver.generateAirShoppingNDCOfferItemId(ptcFareBreakdownData, pricedItinerary,
						itineraryPricingInfo)));
	}

}
