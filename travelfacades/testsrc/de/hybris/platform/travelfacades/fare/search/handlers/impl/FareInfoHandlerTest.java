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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
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
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareInfoHandlerTest
{
	@InjectMocks
	private final FareInfoHandler handler = new FareInfoHandler();

	@Mock
	private TransportOfferingFacade transportOfferingFacade;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private ProductsSortStrategy<ProductData> productsSortStrategy;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private BundleTemplateService bundleTemplateService;

	private FareSearchRequestData fareSearchRequestData;
	private FareSelectionData fareSelectionData;

	private List<PricedItineraryData> pricedItineraries;
	private List<ScheduledRouteData> scheduledRoutes;

	@Before
	public void setup()
	{
		scheduledRoutes = new ArrayList<>();

		final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
		passengerTypeQuantity.setQuantity(2);

		final List<PassengerTypeQuantityData> passengerTypes = new ArrayList<>();
		passengerTypes.add(passengerTypeQuantity);

		fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setPassengerTypes(passengerTypes);

		final TravelSectorData travelSector = new TravelSectorData();
		travelSector.setCode("TS001");

		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("TO001");
		transportOffering.setSector(travelSector);

		final List<TransportOfferingData> transportOfferings = new ArrayList<>();
		transportOfferings.add(transportOffering);

		final FareProductData fareProduct = new FareProductData();
		fareProduct.setProductType(ProductType.ACCOMMODATION.getCode());

		final List<FareProductData> fareProducts = new ArrayList<>();
		fareProducts.add(fareProduct);

		final ProductData nonFareProduct = new ProductData();
		nonFareProduct.setPrice(new PriceData());

		final List<ProductData> nonFareProductList = new ArrayList<>();
		nonFareProductList.add(nonFareProduct);

		final Map<String, List<ProductData>> nonFareProducts = new HashMap<>();
		nonFareProducts.put("ancillaryProduct", nonFareProductList);

		final ProductData ancillaryProduct = new ProductData();
		ancillaryProduct.setPrice(new PriceData());
		final List<ProductData> ancillaryProducts = new ArrayList<>();
		ancillaryProducts.add(ancillaryProduct);
		final IncludedAncillaryData includedAncillaryData = new IncludedAncillaryData();
		includedAncillaryData.setProducts(ancillaryProducts);
		includedAncillaryData.setCriteria(AddToCartCriteriaType.PER_LEG.getCode());
		final List<IncludedAncillaryData> includedAncillaries = new ArrayList<>();
		includedAncillaries.add(includedAncillaryData);

		final TravelBundleTemplateData travelBundleTemplateData = new TravelBundleTemplateData();
		travelBundleTemplateData.setId("travelBundleTemplate");
		travelBundleTemplateData.setAvailable(true);
		travelBundleTemplateData.setTransportOfferings(transportOfferings);
		travelBundleTemplateData.setFareProducts(fareProducts);
		travelBundleTemplateData.setNonFareProducts(nonFareProducts);
		travelBundleTemplateData.setIncludedAncillaries(includedAncillaries);

		final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
		bundleTemplates.add(travelBundleTemplateData);

		final ItineraryPricingInfoData ipInfoData = new ItineraryPricingInfoData();
		ipInfoData.setAvailable(true);
		ipInfoData.setBundleTemplates(bundleTemplates);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		itineraryPricingInfos.add(ipInfoData);

		final TravelRouteData travelRouteData = new TravelRouteData();
		travelRouteData.setCode("TR001");

		final ItineraryData itinerary = new ItineraryData();
		itinerary.setRoute(travelRouteData);

		final PricedItineraryData piData = new PricedItineraryData();
		piData.setAvailable(true);
		piData.setItineraryPricingInfos(itineraryPricingInfos);
		piData.setItinerary(itinerary);

		pricedItineraries = new ArrayList<>();
		pricedItineraries.add(piData);

		fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(pricedItineraries);

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GB");

		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Mockito.anyString())).thenReturn(null);
	}

	@Test
	public void populateProductPricesAndSortForMultiSectorRouteTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("25.00"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("cur", 25.00, false));

		Mockito.when(travelCommercePriceFacade.getPriceInformation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(priceInformation);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(true);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNotNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());
	}

	@Test
	public void populateProductPricesAndSortForSingleSectorRouteTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("12.50"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("cur", 12.50, false));

		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(priceInformation);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(false);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNotNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());
	}

	@Test
	public void populateProductPricesAndSortForSingleSectorRouteNoFareProductsTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("12.50"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("cur", 12.50, false));

		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(priceInformation);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(false);

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.setFareProducts(null);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts());
	}

	@Test
	public void populateProductPricesAndSortForSingleSectorRouteNoProductTypeTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("12.50"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("cur", 12.50, false));

		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(priceInformation);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(false);

		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Mockito.anyString())).thenReturn(bundleTemplateModel);

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.getFareProducts().get(0).setProductType(null);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNotNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());
	}

	@Test
	public void populateProductPricesAndSortForMultiSectorRouteNoPriceInfoTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("25.00"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		Mockito.when(travelCommercePriceFacade.getPriceInformation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(true);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

	@Test
	public void populateProductPricesAndSortForMultiSectorRouteNoProductTypeTest()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal("12.50"));

		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(priceData);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("cur", 12.50, false));

		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(priceInformation);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(true);

		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Mockito.anyString())).thenReturn(bundleTemplateModel);

		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.getFareProducts().get(0).setProductType(null);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().get(0).getPrice());

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

	@Test
	public void itineraryPricingInfoNoFareProductPriceAvailableTest()
	{
		Mockito.when(travelCommercePriceFacade.getPriceInformationByHierarchy(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(null);

		Mockito.when(transportOfferingFacade.isMultiSectorRoute(Mockito.anyListOf(String.class))).thenReturn(false);

		Assert.assertTrue(!fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

	@Test
	public void itineraryPricingInfoFareProductNoPriceAvailableTest()
	{

		fareSelectionData.getPricedItineraries().forEach(pricedItinerary -> pricedItinerary.getItineraryPricingInfos().forEach(
				itineraryPricingInfo -> itineraryPricingInfo.getBundleTemplates().forEach(bundleTemplateData -> bundleTemplateData
						.getNonFareProducts().values().stream().findAny().get().stream().findAny().get().setPrice(null))));

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);
		Assert.assertFalse(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).isAvailable());
		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());

	}

	@Test
	public void itineraryPricingInfoNoBundleTemplateAvailableTest()
	{
		// remove any bundle templates
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().clear();

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(
				fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().isEmpty());
	}

	@Test
	public void itineraryPricingInfoNoFareProductFromBundleTemplateAvailableTest()
	{
		// remove any fare products from the bundle templates
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates().get(0)
				.getFareProducts().clear();

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

	@Test
	public void pricedItineraryNotAvailableTest()
	{
		fareSelectionData.getPricedItineraries().get(0).setAvailable(false);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertFalse(fareSelectionData.getPricedItineraries().get(0).isAvailable());
		Assert.assertTrue(!fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

	@Test
	public void pricedItineraryInfoNotAvailableTest()
	{
		fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).setAvailable(false);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertTrue(fareSelectionData.getPricedItineraries().get(0).isAvailable());
		Assert.assertFalse(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).isAvailable());
		Assert.assertTrue(!fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getBundleTemplates()
				.get(0).getFareProducts().isEmpty());
	}

}
