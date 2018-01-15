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
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.strategies.ProductsSortStrategy;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealFareInfoHandlerTest
{
	@InjectMocks
	DealFareInfoHandler handler;

	@Mock
	private TransportOfferingFacade transportOfferingFacade;

	@Mock
	private BundleTemplateModel bundleTemplateModel;

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private ProductsSortStrategy<ProductData> productsSortStrategy;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private PriceInformation priceInfo;

	@Mock
	private PriceValue priceValue;

	@Mock
	private PriceDataFactory priceDataFactory;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CurrencyModel currency;

	@Test
	public void testPopulateWithNullObjects()
	{
		handler.populateProductPricesAndSort(null, null);
	}

	@Test
	public void testPopulateWithPriceInfoByProductPrice()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryData itinerary = testData.createItinerary();
		itinerary.setRoute(testData.createTravelRoute("testRoute"));
		pricedItinerary.setItinerary(itinerary);
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = testData.createBundleTemplate();
		bundleTemplate.setTransportOfferings(
				Stream.of(testData.createTransportOffering(testData.createTravelSector("testSector"), "testTransportOffering"))
						.collect(Collectors.toList()));
		bundleTemplate.setFareProducts(Stream.of(testData.createFareProduct()).collect(Collectors.toList()));
		bundleTemplate.setIncludedAncillaries(Stream.of(testData.createIncludedAncillary()).collect(Collectors.toList()));
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).thenReturn(bundleTemplateModel);
		Mockito.when(travelCommercePriceFacade.getPriceInformationByProductPriceBundleRule(Matchers.any(BundleTemplateModel.class),
				Matchers.anyString())).thenReturn(priceInfo);
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.TEN);
		Mockito.when(
				priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.thenReturn(priceData);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getCurrencyIso()).thenReturn("GBP");
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		Mockito.doNothing().when(productsSortStrategy).applyStrategy(Matchers.anyList());
		handler.populateProductPricesAndSort(pricedItinerary, itineraryPricingInfoData);
	}

	@Test
	public void testPopulateWithPriceInfoByHierarchy()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryData itinerary = testData.createItinerary();
		itinerary.setRoute(testData.createTravelRoute("testRoute"));
		pricedItinerary.setItinerary(itinerary);
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = testData.createBundleTemplate();
		bundleTemplate.setTransportOfferings(
				Stream.of(testData.createTransportOffering(testData.createTravelSector("testSector"), "testTransportOffering"))
						.collect(Collectors.toList()));
		bundleTemplate.setFareProducts(Stream.of(testData.createFareProduct()).collect(Collectors.toList()));
		bundleTemplate.setIncludedAncillaries(Stream.of(testData.createIncludedAncillary()).collect(Collectors.toList()));
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).thenReturn(null);
		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString())).thenReturn(priceInfo);
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.TEN);
		Mockito.when(
				priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.thenReturn(priceData);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getCurrencyIso()).thenReturn("GBP");
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		Mockito.doNothing().when(productsSortStrategy).applyStrategy(Matchers.anyList());
		handler.populateProductPricesAndSort(pricedItinerary, itineraryPricingInfoData);
	}

	@Test
	public void testPopulateWithNoPriceForTO()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryData itinerary = testData.createItinerary();
		itinerary.setRoute(testData.createTravelRoute("testRoute"));
		pricedItinerary.setItinerary(itinerary);
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = testData.createBundleTemplate();
		bundleTemplate.setTransportOfferings(
				Stream.of(testData.createTransportOffering(testData.createTravelSector("testSector"), "testTransportOffering"))
						.collect(Collectors.toList()));
		bundleTemplate.setFareProducts(Stream.of(testData.createFareProduct()).collect(Collectors.toList()));
		bundleTemplate.setIncludedAncillaries(Stream.of(testData.createIncludedAncillary()).collect(Collectors.toList()));
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).thenReturn(null);
		Mockito.when(travelCommercePriceFacade.getPriceInformationByProductPriceBundleRule(Matchers.any(BundleTemplateModel.class),
				Matchers.anyString())).thenReturn(priceInfo);
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.TEN);
		Mockito.when(
				priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.thenReturn(priceData);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getCurrencyIso()).thenReturn("GBP");
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		Mockito.doNothing().when(productsSortStrategy).applyStrategy(Matchers.anyList());
		handler.populateProductPricesAndSort(pricedItinerary, itineraryPricingInfoData);
	}

	@Test
	public void testPopulatePTCBreakdownWithNullParameters()
	{
		handler.populatePTCFareBreakDownData(null, null);
	}

	@Test
	public void testPopulatePTCBreakdown()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(testData.createPassengerTypes()).collect(Collectors.toList()));
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = testData.createBundleTemplate();
		bundleTemplate.setFareProducts(Stream.of(testData.createFareProduct()).collect(Collectors.toList()));
		final Map<String, List<ProductData>> nonFareProducts = new HashMap<>();
		nonFareProducts.put("testId",
				Stream.of(testData.createNonFareProductData("testNonFareProduct")).collect(Collectors.toList()));
		bundleTemplate.setNonFareProducts(nonFareProducts);
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		Mockito.when(travelCommercePriceFacade.getPriceInformationByProductPriceBundleRule(Matchers.any(BundleTemplateModel.class),
				Matchers.anyString())).thenReturn(priceInfo);
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).thenReturn(bundleTemplateModel);
		Mockito.when(travelCommercePriceFacade.getPriceInformation(Matchers.anyString())).thenReturn(priceInfo);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
		Mockito.when(currency.getIsocode()).thenReturn("GBP");

		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(20d));
		Mockito.when(
				priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.thenReturn(priceData);
		handler.populatePTCFareBreakDownData(itineraryPricingInfoData, fareSearchRequestData);
	}

	@Test
	public void testPopulateWithNonFareProducts()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		final ItineraryData itinerary = testData.createItinerary();
		itinerary.setRoute(testData.createTravelRoute("testRoute"));
		pricedItinerary.setItinerary(itinerary);
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TravelBundleTemplateData bundleTemplate = testData.createBundleTemplate();
		bundleTemplate.setTransportOfferings(
				Stream.of(testData.createTransportOffering(testData.createTravelSector("testSector"), "testTransportOffering"))
						.collect(Collectors.toList()));
		bundleTemplate.setFareProducts(Stream.of(testData.createFareProduct()).collect(Collectors.toList()));
		bundleTemplate.setIncludedAncillaries(Stream.of(testData.createIncludedAncillary()).collect(Collectors.toList()));
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).thenReturn(bundleTemplateModel);
		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString())).thenReturn(priceInfo);
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.TEN);
		Mockito.when(
				priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.thenReturn(priceData);
		Mockito.when(priceInfo.getPriceValue()).thenReturn(priceValue);
		Mockito.when(priceValue.getCurrencyIso()).thenReturn("GBP");
		Mockito.when(priceValue.getValue()).thenReturn(10d);
		Mockito.doNothing().when(productsSortStrategy).applyStrategy(Matchers.anyList());
		handler.populateProductPricesAndSort(pricedItinerary, itineraryPricingInfoData);
	}

	class TestDataSetup
	{
		public ItineraryData createItinerary()
		{
			final ItineraryData itinerary = new ItineraryData();
			return itinerary;
		}

		public PassengerTypeQuantityData createPassengerTypes()
		{
			final PassengerTypeQuantityData passengerType = new PassengerTypeQuantityData();
			passengerType.setQuantity(1);
			return passengerType;
		}

		public TravelRouteData createTravelRoute(final String code)
		{
			final TravelRouteData travelRoute = new TravelRouteData();
			travelRoute.setCode(code);
			return travelRoute;
		}

		public TravelBundleTemplateData createBundleTemplate()
		{
			final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
			bundleTemplate.setId("testId");
			bundleTemplate.setFareProductBundleTemplateId("testFareProductBundleId");
			return bundleTemplate;
		}

		public TravelSectorData createTravelSector(final String code)
		{
			final TravelSectorData travelSector = new TravelSectorData();
			travelSector.setCode(code);
			return travelSector;
		}

		public TransportOfferingData createTransportOffering(final TravelSectorData travelSector, final String code)
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setSector(travelSector);
			transportOffering.setCode(code);
			return transportOffering;
		}

		public FareProductData createFareProduct()
		{
			final FareProductData fareProduct = new FareProductData();
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.TEN);
			fareProduct.setPrice(priceData);
			return fareProduct;
		}

		public IncludedAncillaryData createIncludedAncillary()
		{
			final IncludedAncillaryData includedAncillary = new IncludedAncillaryData();
			return includedAncillary;
		}

		public ProductData createNonFareProductData(final String code)
		{
			final ProductData product = new ProductData();
			product.setCode(code);
			return product;
		}
	}
}
