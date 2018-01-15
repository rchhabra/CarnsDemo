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

package de.hybris.platform.travelservices.price.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.jalo.TravelPriceFactory;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.util.PriceValue;

import java.util.Collections;
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


/**
 * unit test for {@link DefaultTravelCommercePriceService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCommercePriceServiceTest
{
	@InjectMocks
	DefaultTravelCommercePriceService defaultTravelCommercePriceService = new DefaultTravelCommercePriceService()
	{
		@Override
		protected TravelPriceFactory getCurrentPriceFactory()
		{
			return travelPriceFactory;
		}
	};

	@Mock
	private TravelPriceFactory travelPriceFactory;
	@Mock
	private ModelService modelService;

	@Mock
	private ProductService productService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private SessionService sessionService;

	@Mock
	private TransportFacilityService transportFacilityService;

	@Mock
	private CartService cartService;

	@Mock
	private TravelCommerceCartService travelCommerceCartService;

	@Mock
	private TravellerService travellerService;

	@Mock
	private TimeService timeService;

	private Map<String, String> offerGroupToOriginDestinationMapping;

	private final String TEST_PRODUCT_CODE = "TEST_PRODUCT_CODE";

	@Before
	public void setUp()
	{
		offerGroupToOriginDestinationMapping = new HashMap<>();
		offerGroupToOriginDestinationMapping.put("PRIORITYCHECKIN", "TravelRoute");
		offerGroupToOriginDestinationMapping.put("HOLDITEM", "TravelRoute");
		offerGroupToOriginDestinationMapping.put("PRIORITYBOARDING", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("LOUNGEACCESS", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("MEAL", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("ACCOMMODATION", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("DEFAULT", "TravelRoute");

		defaultTravelCommercePriceService.setOfferGroupToOriginDestinationMapping(offerGroupToOriginDestinationMapping);
	}
	@Test
	public void testGetProductWebPriceForJaloPriceFactoryException() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);
		final PriceInformation priceInformation = new PriceInformation(priceValue);
		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenThrow(new JaloPriceFactoryException("Exception", 1));
		Assert.assertNull(defaultTravelCommercePriceService.getProductWebPrice(product, new HashMap<>()));
	}

	@Test
	public void testGetProductWebPriceForEmptyPriceList() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Collections.emptyList());

		Assert.assertNull(defaultTravelCommercePriceService.getProductWebPrice(product, new HashMap<>()));
	}

	@Test
	public void testGetProductWebPrice() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals(priceInformation2, defaultTravelCommercePriceService.getProductWebPrice(product, new HashMap<>()));
	}

	@Test
	public void testGetPriceInformationForNullSearchKey() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals(priceInformation2,
				defaultTravelCommercePriceService.getPriceInformation(product, null, "TEST_SEARCH_VALUE"));
	}

	@Test
	public void testGetPriceInformation() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals(priceInformation2,
				defaultTravelCommercePriceService.getPriceInformation(product, "TEST_SEARCH_KEY", "TEST_SEARCH_VALUE"));
	}

	@Test
	public void testGetPriceLevelInfoForProdcutTypeFEE() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FEE);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYBOARDING");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals("TEST_TRANSPORT_OFFERING_CODE", defaultTravelCommercePriceService
				.getPriceLevelInfo(TEST_PRODUCT_CODE, transportOfferings, "TEST_ROUTE_CODE", false).getValue());
	}

	@Test
	public void testGetPriceLevelInfoForProdcutTypeFEEAndPRIORITYCHECKIN() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FEE);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYCHECKIN");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);
		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals("TEST_ROUTE_CODE", defaultTravelCommercePriceService
				.getPriceLevelInfo(TEST_PRODUCT_CODE, transportOfferings, "TEST_ROUTE_CODE", false).getValue());
	}

	@Test
	public void testGetPriceLevelInfoForProdcutTypeFEEAndNullTravelRoute() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FEE);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYCHECKIN");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());
		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals(null,
				defaultTravelCommercePriceService.getPriceLevelInfo(TEST_PRODUCT_CODE, transportOfferings, null, false).getValue());
	}

	@Test
	public void testGetPriceLevelInfo() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYBOARDING");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals("TEST_TRANSPORT_OFFERING_CODE", defaultTravelCommercePriceService
				.getPriceLevelInfo(TEST_PRODUCT_CODE, transportOfferings, "TEST_ROUTE_CODE", false).getValue());
	}

	@Test
	public void testGetPriceLevelInfoForMultiSector() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYBOARDING");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Stream.of(priceInformation1, priceInformation2).collect(Collectors.toList()));

		Assert.assertEquals("TEST_ROUTE_CODE", defaultTravelCommercePriceService
				.getPriceLevelInfo(TEST_PRODUCT_CODE, transportOfferings, "TEST_ROUTE_CODE", true).getValue());
	}

	@Test
	public void testGetPriceLevelInfoByHierarchy() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode("PRIORITYBOARDING");
		product.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(product);

		final List<String> transportOfferings = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Collections.emptyList());

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		final TravelSectorModel travelSector = new TravelSectorModel();
		travelSector.setCode("TEST_TRAVEL_SECTOR_CODE");
		transportOffering.setTravelSector(travelSector);
		when(transportOfferingService.getTransportOffering("TEST_TRANSPORT_OFFERING_CODE")).thenReturn(transportOffering);

		Assert.assertEquals("default", defaultTravelCommercePriceService
				.getPriceLevelInfoByHierarchy(product, "TEST_TRANSPORT_OFFERING_CODE", "TEST_ROUTE_CODE").getCode());

	}

	@Test
	public void testIsPriceInformationAvailableForNullProduct() throws JaloPriceFactoryException
	{
		final ProductModel product = new ProductModel();
		final Product productItem = new Product();
		when(modelService.getSource(product)).thenReturn(productItem);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO_CODE", 100d, true);

		final Map<String, Object> pricingInfo1 = new HashMap<>();
		pricingInfo1.put("minqtd", new Long(4));
		final PriceInformation priceInformation1 = new PriceInformation(pricingInfo1, priceValue);

		final Map<String, Object> pricingInfo2 = new HashMap<>();
		pricingInfo2.put("minqtd", new Long(2));
		final PriceInformation priceInformation2 = new PriceInformation(pricingInfo2, priceValue);


		when(travelPriceFactory.getProductPriceInformations(Matchers.any(Product.class), Matchers.anyMap()))
				.thenReturn(Collections.emptyList());

		Assert.assertFalse(defaultTravelCommercePriceService.isPriceInformationAvailable(product, null, "TEST_SEARCH_VALUE"));

	}

	@Test
	public void testSetPriceAndTaxSearchCriteriaInContextForNullArguments()
	{
		defaultTravelCommercePriceService.setPriceAndTaxSearchCriteriaInContext(null, null, null);
	}

	@Test
	public void testSetPriceAndTaxSearchCriteriaInContext()
	{
		final List<String> transportOfferingCodes = Stream.of("TEST_TRANSPORT_OFFERING_CODE").collect(Collectors.toList());

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		final TravelSectorModel travelSector = new TravelSectorModel();
		travelSector.setCode("TEST_TRAVEL_SECTOR_CODE");

		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		travelSector.setOrigin(transportFacilityModel);
		transportOffering.setTravelSector(travelSector);
		when(transportOfferingService.getTransportOffering("TEST_TRANSPORT_OFFERING_CODE")).thenReturn(transportOffering);

		final LocationModel countryModel = new LocationModel();
		countryModel.setCode("TEST_COUNTRY_CODE");
		when(transportFacilityService.getCountry(transportFacilityModel)).thenReturn(countryModel);

		final PriceLevel priceLevel1 = new PriceLevel();
		priceLevel1.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
		priceLevel1.setValue("TEST_PRICE_LEVEL_VALUE");
		defaultTravelCommercePriceService.setPriceAndTaxSearchCriteriaInContext(priceLevel1, transportOfferingCodes,
				"TEST_PASSENGER_TYPE");

		final PriceLevel priceLevel2 = new PriceLevel();
		priceLevel2.setCode(TravelservicesConstants.PRICING_LEVEL_TRANSPORT_OFFERING);
		priceLevel2.setValue("TEST_PRICE_LEVEL_VALUE");
		defaultTravelCommercePriceService.setPriceAndTaxSearchCriteriaInContext(priceLevel2, transportOfferingCodes,
				"TEST_PASSENGER_TYPE");

		final PriceLevel priceLevel3 = new PriceLevel();
		priceLevel3.setCode(TravelservicesConstants.PRICING_LEVEL_SECTOR);
		priceLevel3.setValue("TEST_PRICE_LEVEL_VALUE");
		defaultTravelCommercePriceService.setPriceAndTaxSearchCriteriaInContext(priceLevel3, transportOfferingCodes,
				"TEST_PASSENGER_TYPE");

		final PriceLevel priceLevel4 = new PriceLevel();
		priceLevel4.setValue("TEST_PRICE_LEVEL_VALUE");
		priceLevel4.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
		defaultTravelCommercePriceService.setPriceAndTaxSearchCriteriaInContext(priceLevel4, transportOfferingCodes,
				"TEST_PASSENGER_TYPE");
	}

	@Test
	public void testAddPropertyPriceLevelToCartEntry()
	{
		defaultTravelCommercePriceService.addPropertyPriceLevelToCartEntry(null, null, 0);

		final PriceLevel priceLevel = new PriceLevel();
		priceLevel.setCode("TEST_PRICE_LEVEL_CODE");

		final ProductModel productModel = new ProductModel();
		when(productService.getProductForCode(TEST_PRODUCT_CODE)).thenReturn(productModel);
		defaultTravelCommercePriceService.addPropertyPriceLevelToCartEntry(priceLevel, TEST_PRODUCT_CODE, 1);


	}
}