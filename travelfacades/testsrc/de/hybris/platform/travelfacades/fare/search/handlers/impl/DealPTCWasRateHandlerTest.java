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
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealPTCWasRateHandlerTest
{
	@InjectMocks
	DealPTCWasRateHandler handler;

	@Mock
	private TransportFacilityFacade transportFacilityFacade;

	@Mock
	private ProductService productService;
	@Mock
	private ProductModel product;
	@Mock
	private PriceInformation priceInfo;
	@Mock
	private TravelCommercePriceService travelCommercePriceService;
	@Mock
	private PriceValue priceValue;
	@Mock
	private SessionService sessionService;
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private FareSelectionData fareSelectionData;


	@Before
	public void setup()
	{
		fareSelectionData = TestDataSetup.createFareSelection();

		// transport facility facade mock
		final LocationData country = new LocationData();
		country.setCode("UK");
		given(transportFacilityFacade.getCountry("LTN")).willReturn(country);

		given(travelCommercePriceService.getProductTaxInformations(Mockito.any(ProductModel.class)))
				.willReturn(TestDataSetup.createTaxes());

		final PriceData taxPriceData = new PriceData();
		taxPriceData.setValue(new BigDecimal("10.00"));

		// price data factory mock
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyString())).willReturn(taxPriceData);
		// price data factory mock
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt(), Matchers.anyString()))
				.willReturn(taxPriceData);

	}

	@Test
	public void tesHandleWithEmptyParameters()
	{
		handler.handle(Collections.emptyList(), null, null);
	}

	@Test
	public void testHandle()
	{
		Mockito.when(productService.getProductForCode(Matchers.anyString())).thenReturn(product);
		Mockito.when(travelCommercePriceService.getPriceInformation(product, null, null)).thenReturn(priceInfo);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		handler.handle(Collections.emptyList(), null, fareSelectionData);
	}


	/**
	 * Inner class to set-up test data
	 */
	private static class TestDataSetup
	{
		public static FareSelectionData createFareSelection()
		{
			final FareSelectionData fareSelection = new FareSelectionData();
			fareSelection.setPricedItineraries(createpricedItineraries());

			return fareSelection;
		}

		public static List<TaxInformation> createTaxes()
		{
			final List<TaxInformation> taxInformations = new ArrayList<>();
			taxInformations.add(new TaxInformation(new TaxValue("APD", new Double("5.00"), false, "GB")));
			taxInformations.add(new TaxInformation(new TaxValue("VAT", new Double("10.00"), true, "GB")));
			return taxInformations;
		}

		private static List<PricedItineraryData> createpricedItineraries()
		{
			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption.setTransportOfferings(createTransportOfferings("LTN"));

			final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>();
			originDestinationOptions.add(originDestinationOption);

			final ItineraryData itinerary = new ItineraryData();
			itinerary.setOriginDestinationOptions(originDestinationOptions);

			// available
			final PricedItineraryData availablePricedItinerary = new PricedItineraryData();
			availablePricedItinerary.setItinerary(itinerary);
			availablePricedItinerary.setItineraryPricingInfos(createItineraryPricingInfos());
			availablePricedItinerary.setAvailable(true);

			// unavailable
			final PricedItineraryData unavailablePricedItinerary = new PricedItineraryData();
			unavailablePricedItinerary.setAvailable(false);

			final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
			pricedItineraries.add(availablePricedItinerary);
			pricedItineraries.add(unavailablePricedItinerary);

			return pricedItineraries;
		}

		private static List<TransportOfferingData> createTransportOfferings(final String sectorCode)
		{
			final TravelSectorData sector = new TravelSectorData();
			sector.setCode(sectorCode);
			sector.setOrigin(createTransportFacility("LTN", createLocationData("LON")));
			sector.setDestination(createTransportFacility("CDG", createLocationData("PAR")));

			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setSector(sector);

			final List<TransportOfferingData> transportOfferings = new ArrayList<>();
			transportOfferings.add(transportOffering);

			return transportOfferings;
		}

		private static List<ItineraryPricingInfoData> createItineraryPricingInfos()
		{
			final ItineraryPricingInfoData availableItineraryPricingInfo = new ItineraryPricingInfoData();
			availableItineraryPricingInfo.setBundleTemplates(createBundleTemplates());
			availableItineraryPricingInfo.setAvailable(true);
			availableItineraryPricingInfo.setPtcFareBreakdownDatas(createPTCBreakdown());

			// available
			final ItineraryPricingInfoData unavailableItineraryPricingInfo = new ItineraryPricingInfoData();
			unavailableItineraryPricingInfo.setBundleTemplates(createBundleTemplates());
			unavailableItineraryPricingInfo.setAvailable(false);
			unavailableItineraryPricingInfo.setPtcFareBreakdownDatas(createPTCBreakdown());

			// unavailable
			final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
			itineraryPricingInfos.add(availableItineraryPricingInfo);
			itineraryPricingInfos.add(unavailableItineraryPricingInfo);

			return itineraryPricingInfos;
		}

		private static List<TravelBundleTemplateData> createBundleTemplates()
		{
			final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
			bundleTemplate.setId("testId");
			bundleTemplate.setTransportOfferings(createTransportOfferings("LTN"));
			bundleTemplate.setIncludedAncillaries(createIncludedAncillary());
			bundleTemplate.setNonFareProducts(createNonFareProductData());

			final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
			bundleTemplates.add(bundleTemplate);
			return bundleTemplates;
		}

		private static List<IncludedAncillaryData> createIncludedAncillary()
		{
			final IncludedAncillaryData includedAncillary = new IncludedAncillaryData();
			includedAncillary.setProducts(Arrays.asList(new ProductData()));

			final List<IncludedAncillaryData> includedancillaries = new ArrayList<>();
			includedancillaries.add(includedAncillary);
			return includedancillaries;
		}

		private static Map<String, List<ProductData>> createNonFareProductData()
		{
			final ProductData product = new ProductData();
			product.setCode("nonFareProduct");

			final Map<String, List<ProductData>> nonFareProducts = new HashMap<>();
			nonFareProducts.put("testId", Arrays.asList(product));
			return nonFareProducts;
		}

		private static LocationData createLocationData(final String code)
		{
			final LocationData london = new LocationData();
			london.setCode(code);
			return london;
		}

		private static TransportFacilityData createTransportFacility(final String code, final LocationData location)
		{
			final TransportFacilityData transportFacility = new TransportFacilityData();
			transportFacility.setCode(code);
			transportFacility.setLocation(location);
			return transportFacility;
		}

		private static List<PTCFareBreakdownData> createPTCBreakdown()
		{
			final List<PTCFareBreakdownData> ptcFareBreakdowns = new ArrayList<>();
			ptcFareBreakdowns.add(adultPTCBreakdown("adult", 1, "25.00"));
			ptcFareBreakdowns.add(adultPTCBreakdown("child", 1, "10.00"));

			return ptcFareBreakdowns;
		}

		private static PTCFareBreakdownData adultPTCBreakdown(final String passengerTypeCode, final int qty, final String farePrice)
		{
			final PassengerTypeData passengerType = new PassengerTypeData();
			passengerType.setCode(passengerTypeCode);

			final PassengerTypeQuantityData PassengerTypeQuantity = new PassengerTypeQuantityData();
			PassengerTypeQuantity.setQuantity(qty);
			PassengerTypeQuantity.setPassengerType(passengerType);

			final FareInfoData fareInfo = new FareInfoData();
			fareInfo.setFareDetails(createFareDetails("ECO001"));

			final List<FareInfoData> fareInfos = new ArrayList<>();
			fareInfos.add(fareInfo);

			final PassengerFareData passengerFare = new PassengerFareData();
			final PriceData price = new PriceData();
			price.setCurrencyIso("GBP");
			passengerFare.setBaseFare(price);
			passengerFare.getBaseFare().setValue(new BigDecimal(farePrice));

			final PTCFareBreakdownData ptcBreakdown = new PTCFareBreakdownData();
			ptcBreakdown.setPassengerTypeQuantity(PassengerTypeQuantity);
			ptcBreakdown.setFareInfos(fareInfos);
			ptcBreakdown.setPassengerFare(passengerFare);

			return ptcBreakdown;
		}

		private static List<FareDetailsData> createFareDetails(final String fareCode)
		{
			final PriceData farePrice = new PriceData();
			farePrice.setValue(new BigDecimal("10.00"));

			final FareProductData fareProduct = new FareProductData();
			fareProduct.setCode(fareCode);
			fareProduct.setPrice(farePrice);

			final FareDetailsData fareDetail = new FareDetailsData();
			fareDetail.setFareProduct(fareProduct);

			final List<FareDetailsData> fareDetails = new ArrayList<>();
			fareDetails.add(fareDetail);

			return fareDetails;
		}


	}

}
