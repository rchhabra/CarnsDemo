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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.fare.search.strategies.AncillaryAvailabilityStrategy;
import de.hybris.platform.travelfacades.fare.search.strategies.impl.AncillaryPerLegAvailabilityStrategy;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareAvailabilityHandlerTest
{
	@InjectMocks
	private FareAvailabilityHandler handler;

	private FareSearchRequestData fareSearchRequestData;
	private FareSelectionData fareSelectionData;

	private List<PricedItineraryData> pricedItineraries;
	private List<ScheduledRouteData> scheduledRoutes;

	@Mock
	private ProductService productService;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private Map<String, AncillaryAvailabilityStrategy> ancillaryAvailabilityStrategiesMap;

	@Mock
	private AncillaryPerLegAvailabilityStrategy ancillaryPerLegAvailabilityStrategy;

	@Before
	public void setup()
	{
		fareSearchRequestData = new FareSearchRequestData();
		fareSelectionData = new FareSelectionData();

		pricedItineraries = new ArrayList<>();
		scheduledRoutes = new ArrayList<>();

		final TravelBundleTemplateData travelBundleTemplateData = new TravelBundleTemplateData();
		travelBundleTemplateData.setAvailable(true);

		final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
		bundleTemplates.add(travelBundleTemplateData);

		final ItineraryPricingInfoData ipInfoData = new ItineraryPricingInfoData();
		ipInfoData.setAvailable(true);
		ipInfoData.setBundleTemplates(bundleTemplates);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		itineraryPricingInfos.add(ipInfoData);

		final PricedItineraryData piData = new PricedItineraryData();
		piData.setAvailable(true);
		piData.setItineraryPricingInfos(itineraryPricingInfos);

		pricedItineraries.add(piData);

		fareSelectionData.setPricedItineraries(pricedItineraries);
	}

	@Test
	public void pricedItinerariesNotAvailableTest()
	{
		fareSelectionData.getPricedItineraries().get(0).setAvailable(false);
		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertFalse(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void itineraryPricingInfoNotAvailableButBundleIsTest()
	{
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).setAvailable(false);
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.setAvailable(true);
		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testHandleWithoutTransportOfferings()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0)
				.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).setAvailable(true);
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.setAvailable(true);

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(!fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testHandleWithTransportOfferings()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0)
				.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).setAvailable(true);
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.setAvailable(true);

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		final FareProductModel fareProductModel = new FareProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		given(commerceStockService.getStockLevelQuantity(Matchers.any(), Matchers.anyCollectionOf(WarehouseModel.class)))
				.willReturn(Long.valueOf(10));

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testAvailabilityWithAncillaries()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));

		final IncludedAncillaryData includedAncillaryData = new IncludedAncillaryData();
		includedAncillaryData.setCriteria("PER_LEG");
		bundleTemplate.setIncludedAncillaries(Stream.of(includedAncillaryData).collect(Collectors.toList()));

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0)
				.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).setAvailable(true);
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.setAvailable(true);

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		final FareProductModel fareProductModel = new FareProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		given(commerceStockService.getStockLevelQuantity(Matchers.any(), Matchers.anyCollectionOf(WarehouseModel.class)))
				.willReturn(Long.valueOf(10));

		given(ancillaryAvailabilityStrategiesMap.get("PER_LEG")).willReturn(ancillaryPerLegAvailabilityStrategy);

		given(ancillaryPerLegAvailabilityStrategy.checkIncludedAncillariesAvailability(Matchers.anyList(),
				Matchers.anyInt()))
				.willReturn(Boolean.TRUE);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testNonAvaibilityOfFareProducts()
	{
		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();

		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertFalse(itineraryPricingInfoData.isAvailable());
	}

	@Test
	public void testAvaibilityOfFareProducts()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		final ProductModel fareProductModel = new ProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(commerceStockService.getStockLevelQuantity(Matchers.any(), Matchers.anyCollectionOf(WarehouseModel.class)))
				.willReturn(Long.valueOf(10));

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertTrue(itineraryPricingInfoData.isAvailable());
	}

	@Test
	public void testAvaibilityOfNonFareProducts()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");

		final ProductData ancProductData = new ProductData();
		ancProductData.setCode("AP1");

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		final Map<String, List<ProductData>> ancProductMap = new HashMap<String, List<ProductData>>();
		ancProductMap.put("AP1", Stream.of(ancProductData).collect(Collectors.toList()));
		bundleTemplate.setNonFareProducts(ancProductMap);


		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		final ProductModel fareProductModel = new ProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(commerceStockService.getStockLevelQuantity(Matchers.any(), Matchers.anyCollectionOf(WarehouseModel.class)))
				.willReturn(Long.valueOf(10));

		final ProductModel ancProductModel = new ProductModel();
		ancProductModel.setCode("AP1");
		given(productService.getProductForCode(Matchers.eq("AP1"))).willReturn(ancProductModel);

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertTrue(itineraryPricingInfoData.isAvailable());
	}

	@Test
	public void testNoStockExceptionForFareProduct()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		final ProductModel fareProductModel = new ProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(commerceStockService.getStockLevelQuantity(Matchers.eq(fareProductModel),
				Matchers.anyCollectionOf(WarehouseModel.class))).willThrow(new StockLevelNotFoundException("no stock found"));

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertFalse(itineraryPricingInfoData.isAvailable());
	}

	@Test
	public void testInsufficientStockForFareProduct()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		final ProductModel fareProductModel = new ProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		given(commerceStockService.getStockLevelQuantity(Matchers.eq(fareProductModel),
				Matchers.anyCollectionOf(WarehouseModel.class)))
				.willReturn(Long.valueOf(0));

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertFalse(itineraryPricingInfoData.isAvailable());
	}

	@Test
	public void testInsufficientStockForAncillaryProduct()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode("FP1");

		final ProductData ancProductData = new ProductData();
		ancProductData.setCode("AP1");

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("EZY0001");

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setFareProducts(Stream.of(fareProductData).collect(Collectors.toList()));
		bundleTemplate.setTransportOfferings(Stream.of(transportOfferingData).collect(Collectors.toList()));

		final Map<String, List<ProductData>> ancProductMap = new HashMap<String, List<ProductData>>();
		ancProductMap.put("AP1", Stream.of(ancProductData).collect(Collectors.toList()));
		bundleTemplate.setNonFareProducts(ancProductMap);


		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0001");
		final Map<String, TransportOfferingModel> transportOfferingModelMap = new HashMap<String, TransportOfferingModel>();
		transportOfferingModelMap.put("EZY0001", transportOfferingModel);

		given(transportOfferingService.getTransportOfferingsMap(Matchers.anyListOf(String.class)))
				.willReturn(transportOfferingModelMap);

		final ProductModel fareProductModel = new ProductModel();
		given(productService.getProductForCode(Matchers.eq("FP1"))).willReturn(fareProductModel);

		final ProductModel ancProductModel = new ProductModel();
		ancProductModel.setCode("AP1");
		given(productService.getProductForCode(Matchers.eq("AP1"))).willReturn(ancProductModel);

		given(commerceStockService.getStockLevelQuantity(Matchers.eq(fareProductModel),
				Matchers.anyCollectionOf(WarehouseModel.class))).willReturn(Long.valueOf(10));

		given(commerceStockService.getStockLevelQuantity(Matchers.eq(ancProductModel),
				Matchers.anyCollectionOf(WarehouseModel.class))).willReturn(Long.valueOf(0));

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(1);
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));

		handler.checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
		Assert.assertFalse(itineraryPricingInfoData.isAvailable());
	}

}
