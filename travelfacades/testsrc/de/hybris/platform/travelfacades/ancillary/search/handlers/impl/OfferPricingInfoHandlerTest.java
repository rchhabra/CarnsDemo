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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.OfferPricingInfoHandler;
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test class for OfferPricingInfoHandler.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferPricingInfoHandlerTest
{

	@Mock
	private CategoryService categoryService;

	@Mock
	private ProductService productService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private TravelCartService travelCartService;

	@Mock
	private TravelPromotionsFacade travelPromotionsFacade;

	@InjectMocks
	OfferPricingInfoHandler offerPricingInfoHandler = new OfferPricingInfoHandler();

	/**
	 * given: Only one Ancillary type(category) available to offer on single sector journey.
	 * 
	 * When: There are different products available to offer
	 * 
	 * Then: Offer products which belong to the category and available.
	 */
	@Test
	public void testPopulateForSingleAncillaryTpe()
	{
		final TestDataSetup testDataSetup = new TestDataSetup();

		final List<TransportOfferingData> transportOfferings = Stream
				.of(testDataSetup.createTransportOfferingData("EZY1234221220151100"))
				.collect(Collectors.<TransportOfferingData> toList());

		final List<OriginDestinationOfferInfoData> odOptions = Stream
				.of(testDataSetup.createOriginDestinationOfferInfo(transportOfferings))
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());

		final List<OfferGroupData> offerGroups = Stream.of(testDataSetup.createOfferGroupData("Meal", odOptions))
				.collect(Collectors.<OfferGroupData> toList());

		final OfferResponseData offerResponseData = testDataSetup.createOfferResponseData(offerGroups);

		final ProductModel prodMeal1 = testDataSetup.createProductModel("Meal1");
		final ProductModel prodMeal2 = testDataSetup.createProductModel("Meal2");
		final ProductModel prodMeal3 = testDataSetup.createProductModel("Meal3");
		final ProductModel prodMeal4 = testDataSetup.createProductModel("Meal4");

		final List<ProductModel> products = Stream.of(prodMeal1, prodMeal2, prodMeal3, prodMeal4)
				.collect(Collectors.<ProductModel> toList());

		final CategoryModel catModel = testDataSetup.createCategoryModel("Meal");
		given(categoryService.getCategoryForCode("Meal")).willReturn(catModel);

		given(productService.getProductsForCategory(catModel)).willReturn(products);

		final Set<StockLevelModel> stocksForTO1 = Stream
				.of(testDataSetup.createStockLevelModel("Meal1", 5), testDataSetup.createStockLevelModel("Meal2", 1),
						testDataSetup.createStockLevelModel("Meal3", 0), testDataSetup.createStockLevelModel("Bag1", 15))
				.collect(Collectors.<StockLevelModel> toSet());

		final TransportOfferingModel transportOffering = testDataSetup.createTransportOfferingModel("EZY1234221220151100",
				stocksForTO1);
		given(transportOfferingService.getTransportOffering("EZY1234221220151100")).willReturn(transportOffering);

		final List<TransportOfferingModel> transportOfferingModels = new ArrayList<TransportOfferingModel>();
		transportOfferingModels.add(transportOffering);
		given(travelCartService.getAvailableStock(prodMeal1, transportOffering)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal2, transportOffering)).willReturn(Long.valueOf(1));
		given(travelCartService.getAvailableStock(prodMeal3, transportOffering)).willReturn(Long.valueOf(0));
		given(travelCartService.getAvailableStock(prodMeal4, transportOffering))
				.willThrow(new StockLevelNotFoundException("no stock found"));

		given(productConverter.convert(prodMeal1)).willReturn(testDataSetup.createProductData("Meal1"));
		given(productConverter.convert(prodMeal2)).willReturn(testDataSetup.createProductData("Meal2"));

		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(false);
		offerPricingInfoHandler.handle(null, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos());
		assertEquals(2,
				offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().size());
		assertTrue(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Meal1"))
					{
						return true;
					}
					return false;
				}));

		assertTrue(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Meal2"))
					{
						return true;
					}
					return false;
				}));
	}

	/**
	 * given: Only one Ancillary type(category) available to offer on multi-sector journey.
	 * 
	 * When: There are different products(of same category) available with multipleSector
	 * 
	 * Then: The common products across the transportOfferings should be offered.
	 */
	@Test
	public void testPopulateForSingleAncillaryTpeMultiSector()
	{
		final TestDataSetup testDataSetup = new TestDataSetup();

		final List<TransportOfferingData> transportOfferings = Stream
				.of(testDataSetup.createTransportOfferingData("EZY1234221220151100"),
						testDataSetup.createTransportOfferingData("EZY4567221220151600"))
				.collect(Collectors.<TransportOfferingData> toList());

		final List<OriginDestinationOfferInfoData> odOptions = Stream
				.of(testDataSetup.createOriginDestinationOfferInfo(transportOfferings))
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());

		final List<OfferGroupData> offerGroups = Stream.of(testDataSetup.createOfferGroupData("Meal", odOptions))
				.collect(Collectors.<OfferGroupData> toList());

		final OfferResponseData offerResponseData = testDataSetup.createOfferResponseData(offerGroups);

		final ProductModel prodMeal1 = testDataSetup.createProductModel("Meal1");
		final ProductModel prodMeal2 = testDataSetup.createProductModel("Meal2");
		final ProductModel prodMeal3 = testDataSetup.createProductModel("Meal3");
		final ProductModel prodMeal4 = testDataSetup.createProductModel("Meal4");

		final List<ProductModel> products = Stream.of(prodMeal1, prodMeal2, prodMeal3, prodMeal4)
				.collect(Collectors.<ProductModel> toList());

		final CategoryModel catModel = testDataSetup.createCategoryModel("Meal");
		given(categoryService.getCategoryForCode("Meal")).willReturn(catModel);

		given(productService.getProductsForCategory(catModel)).willReturn(products);

		final Set<StockLevelModel> stocksForTO1 = Stream
				.of(testDataSetup.createStockLevelModel("Meal1", 5), testDataSetup.createStockLevelModel("Meal2", 1),
						testDataSetup.createStockLevelModel("Meal3", 0), testDataSetup.createStockLevelModel("Bag1", 15))
				.collect(Collectors.<StockLevelModel> toSet());

		final Set<StockLevelModel> stocksForTO2 = Stream
				.of(testDataSetup.createStockLevelModel("Meal1", 5), testDataSetup.createStockLevelModel("Meal4", 10),
						testDataSetup.createStockLevelModel("Meal3", 5), testDataSetup.createStockLevelModel("Bag1", 15))
				.collect(Collectors.<StockLevelModel> toSet());

		final TransportOfferingModel transportOffering1 = testDataSetup.createTransportOfferingModel("EZY1234221220151100",
				stocksForTO1);
		final TransportOfferingModel transportOffering2 = testDataSetup.createTransportOfferingModel("EZY4567221220151600",
				stocksForTO2);

		given(transportOfferingService.getTransportOffering("EZY1234221220151100")).willReturn(transportOffering1);
		given(transportOfferingService.getTransportOffering("EZY4567221220151600")).willReturn(transportOffering2);

		Collection<TransportOfferingModel> transportOfferingModels = new ArrayList<TransportOfferingModel>();
		transportOfferingModels.add(transportOffering1);
		given(travelCartService.getAvailableStock(prodMeal1, transportOffering1)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal2, transportOffering1)).willReturn(Long.valueOf(1));
		given(travelCartService.getAvailableStock(prodMeal3, transportOffering1)).willReturn(Long.valueOf(0));
		given(travelCartService.getAvailableStock(prodMeal4, transportOffering1))
				.willThrow(new StockLevelNotFoundException("no stock found"));

		transportOfferingModels = new ArrayList<TransportOfferingModel>();
		transportOfferingModels.add(transportOffering2);
		given(travelCartService.getAvailableStock(prodMeal1, transportOffering2)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal2, transportOffering2))
				.willThrow(new StockLevelNotFoundException("no stock found"));
		given(travelCartService.getAvailableStock(prodMeal3, transportOffering2)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal4, transportOffering2)).willReturn(Long.valueOf(10));

		given(productConverter.convert(prodMeal1)).willReturn(testDataSetup.createProductData("Meal1"));
		offerPricingInfoHandler.handle(null, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos());
		assertEquals(1,
				offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().size());
		assertEquals("Meal1", offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0)
				.getOfferPricingInfos().get(0).getProduct().getCode());
	}

	/**
	 * given : when ancillaries to be offered are available both at RouteLevel & transportOffering Level
	 * 
	 * when : There are different products(of same category) available with multipleSector
	 * 
	 * Then: For the category of product defined at RouteLevel, offer products which are common and available. For the
	 * category of product defined at TransportOfferingLevel, offer products which are available.
	 */
	@Test
	public void testPopulateForMultipleAncillaryTpe()
	{
		final TestDataSetup testDataSetup = new TestDataSetup();

		final List<TransportOfferingData> transportOfferingsMeal = Stream
				.of(testDataSetup.createTransportOfferingData("EZY1234221220151100"),
						testDataSetup.createTransportOfferingData("EZY4567221220151600"))
				.collect(Collectors.<TransportOfferingData> toList());

		final List<TransportOfferingData> transportOfferingsBoarding1 = Stream
				.of(testDataSetup.createTransportOfferingData("EZY1234221220151100"))
				.collect(Collectors.<TransportOfferingData> toList());
		final List<TransportOfferingData> transportOfferingsBoarding2 = Stream
				.of(testDataSetup.createTransportOfferingData("EZY4567221220151600"))
				.collect(Collectors.<TransportOfferingData> toList());


		final List<OriginDestinationOfferInfoData> odOptionsMeal = Stream
				.of(testDataSetup.createOriginDestinationOfferInfo(transportOfferingsMeal))
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());

		final List<OriginDestinationOfferInfoData> odOptionsBoarding = Stream
				.of(testDataSetup.createOriginDestinationOfferInfo(transportOfferingsBoarding1),
						testDataSetup.createOriginDestinationOfferInfo(transportOfferingsBoarding2))
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());


		final List<OfferGroupData> offerGroups = Stream
				.of(testDataSetup.createOfferGroupData("Meal", odOptionsMeal),
						testDataSetup.createOfferGroupData("PriorityBoarding", odOptionsBoarding))
				.collect(Collectors.<OfferGroupData> toList());

		final OfferResponseData offerResponseData = testDataSetup.createOfferResponseData(offerGroups);

		final ProductModel prodMeal1 = testDataSetup.createProductModel("Meal1");
		final ProductModel prodMeal2 = testDataSetup.createProductModel("Meal2");
		final ProductModel prodMeal3 = testDataSetup.createProductModel("Meal3");
		final ProductModel prodMeal4 = testDataSetup.createProductModel("Meal4");

		final ProductModel prodBoard1 = testDataSetup.createProductModel("Boarding1");
		final ProductModel prodBoard2 = testDataSetup.createProductModel("Boarding2");
		final ProductModel prodBoard3 = testDataSetup.createProductModel("Boarding3");
		final ProductModel prodBoard4 = testDataSetup.createProductModel("Boarding4");

		final List<ProductModel> productsMeal = Stream.of(prodMeal1, prodMeal2, prodMeal3, prodMeal4)
				.collect(Collectors.<ProductModel> toList());

		final List<ProductModel> productsBoard = Stream.of(prodBoard1, prodBoard2, prodBoard3, prodBoard4)
				.collect(Collectors.<ProductModel> toList());

		final CategoryModel catModelMeal = testDataSetup.createCategoryModel("Meal");
		given(categoryService.getCategoryForCode("Meal")).willReturn(catModelMeal);

		given(productService.getProductsForCategory(catModelMeal)).willReturn(productsMeal);

		final CategoryModel catModelBoard = testDataSetup.createCategoryModel("PriorityBoarding");
		given(categoryService.getCategoryForCode("PriorityBoarding")).willReturn(catModelBoard);

		given(productService.getProductsForCategory(catModelMeal)).willReturn(productsMeal);
		given(productService.getProductsForCategory(catModelBoard)).willReturn(productsBoard);

		final Set<StockLevelModel> stocksForTO1 = Stream.of(testDataSetup.createStockLevelModel("Meal1", 5),
				testDataSetup.createStockLevelModel("Meal2", 1), testDataSetup.createStockLevelModel("Meal3", 0),
				testDataSetup.createStockLevelModel("Bag1", 15), testDataSetup.createStockLevelModel("Boarding1", 1),
				testDataSetup.createStockLevelModel("Boarding2", 0), testDataSetup.createStockLevelModel("Boarding3", 14))
				.collect(Collectors.<StockLevelModel> toSet());

		final Set<StockLevelModel> stocksForTO2 = Stream.of(testDataSetup.createStockLevelModel("Meal1", 5),
				testDataSetup.createStockLevelModel("Meal4", 10), testDataSetup.createStockLevelModel("Meal3", 5),
				testDataSetup.createStockLevelModel("Bag1", 15), testDataSetup.createStockLevelModel("Boarding1", 10),
				testDataSetup.createStockLevelModel("Boarding2", 5), testDataSetup.createStockLevelModel("Boarding4", 25))
				.collect(Collectors.<StockLevelModel> toSet());

		final TransportOfferingModel transportOffering1 = testDataSetup.createTransportOfferingModel("EZY1234221220151100",
				stocksForTO1);
		final TransportOfferingModel transportOffering2 = testDataSetup.createTransportOfferingModel("EZY4567221220151600",
				stocksForTO2);

		given(transportOfferingService.getTransportOffering("EZY1234221220151100")).willReturn(transportOffering1);
		given(transportOfferingService.getTransportOffering("EZY4567221220151600")).willReturn(transportOffering2);

		Collection<TransportOfferingModel> transportOfferingModels = new ArrayList<TransportOfferingModel>();
		transportOfferingModels.add(transportOffering1);
		given(travelCartService.getAvailableStock(prodMeal1, transportOffering1)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal2, transportOffering1)).willReturn(Long.valueOf(1));
		given(travelCartService.getAvailableStock(prodMeal3, transportOffering1)).willReturn(Long.valueOf(0));
		given(travelCartService.getAvailableStock(prodMeal4, transportOffering1))
				.willThrow(new StockLevelNotFoundException("no stock found"));
		given(travelCartService.getAvailableStock(prodBoard1, transportOffering1)).willReturn(Long.valueOf(1));
		given(travelCartService.getAvailableStock(prodBoard2, transportOffering1)).willReturn(Long.valueOf(0));
		given(travelCartService.getAvailableStock(prodBoard3, transportOffering1)).willReturn(Long.valueOf(14));
		given(travelCartService.getAvailableStock(prodBoard4, transportOffering1))
				.willThrow(new StockLevelNotFoundException("no stock found"));

		transportOfferingModels = new ArrayList<TransportOfferingModel>();
		transportOfferingModels.add(transportOffering2);
		given(travelCartService.getAvailableStock(prodMeal1, transportOffering2)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal2, transportOffering2))
				.willThrow(new StockLevelNotFoundException("no stock found"));
		given(travelCartService.getAvailableStock(prodMeal3, transportOffering2)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodMeal4, transportOffering2)).willReturn(Long.valueOf(10));
		given(travelCartService.getAvailableStock(prodBoard1, transportOffering2)).willReturn(Long.valueOf(10));
		given(travelCartService.getAvailableStock(prodBoard2, transportOffering2)).willReturn(Long.valueOf(5));
		given(travelCartService.getAvailableStock(prodBoard3, transportOffering2))
				.willThrow(new StockLevelNotFoundException("no stock found"));
		given(travelCartService.getAvailableStock(prodBoard4, transportOffering2)).willReturn(Long.valueOf(25));

		given(productConverter.convert(prodMeal1)).willReturn(testDataSetup.createProductData("Meal1"));
		given(productConverter.convert(prodBoard1)).willReturn(testDataSetup.createProductData("Boarding1"));
		given(productConverter.convert(prodBoard3)).willReturn(testDataSetup.createProductData("Boarding3"));
		given(productConverter.convert(prodBoard2)).willReturn(testDataSetup.createProductData("Boarding2"));
		given(productConverter.convert(prodBoard4)).willReturn(testDataSetup.createProductData("Boarding4"));
		offerPricingInfoHandler.handle(null, offerResponseData);

		assertNotNull(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos());
		assertEquals(1,
				offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().size());
		assertEquals("Meal1", offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0)
				.getOfferPricingInfos().get(0).getProduct().getCode());

		assertNotNull(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos());
		assertNotNull(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(1).getOfferPricingInfos());
		assertEquals(2,
				offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().size());

		assertTrue(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Boarding1"))
					{
						return true;
					}
					return false;
				}));

		assertTrue(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(0).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Boarding3"))
					{
						return true;
					}
					return false;
				}));

		assertEquals(3,
				offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(1).getOfferPricingInfos().size());

		assertTrue(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(1).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Boarding1"))
					{
						return true;
					}
					return false;
				}));

		assertTrue(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(1).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Boarding2"))
					{
						return true;
					}
					return false;
				}));
		assertTrue(offerResponseData.getOfferGroups().get(1).getOriginDestinationOfferInfos().get(1).getOfferPricingInfos().stream()
				.anyMatch(offerPriceInfoData -> {
					if (offerPriceInfoData.getProduct().getCode().equals("Boarding4"))
					{
						return true;
					}
					return false;
				}));
	}

	/**
	 * Test data setup
	 */
	private class TestDataSetup
	{

		private OfferResponseData createOfferResponseData(final List<OfferGroupData> offerGroups)
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			offerResponseData.setOfferGroups(offerGroups);
			return offerResponseData;

		}

		private OfferGroupData createOfferGroupData(final String categoryCode,
				final List<OriginDestinationOfferInfoData> odOfferInfos)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode(categoryCode);
			offerGroupData.setOriginDestinationOfferInfos(odOfferInfos);
			return offerGroupData;
		}

		private OriginDestinationOfferInfoData createOriginDestinationOfferInfo(
				final List<TransportOfferingData> transportOfferingData)
		{
			final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
			odOfferInfoData.setTransportOfferings(transportOfferingData);
			return odOfferInfoData;
		}

		private TransportOfferingData createTransportOfferingData(final String code)
		{
			final TransportOfferingData toData = new TransportOfferingData();
			toData.setCode(code);
			return toData;
		}

		private ProductModel createProductModel(final String code)
		{
			final ProductModel productModel = new ProductModel();
			productModel.setCode(code);
			return productModel;
		}

		private CategoryModel createCategoryModel(final String code)
		{
			final CategoryModel catModel = new CategoryModel();
			catModel.setCode(code);
			return catModel;
		}

		private StockLevelModel createStockLevelModel(final String productCode, final int availability)
		{
			final StockLevelModel stockLevel = new StockLevelModel();
			stockLevel.setProductCode(productCode);
			stockLevel.setAvailable(availability);
			return stockLevel;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code, final Set<StockLevelModel> stocks)
		{
			final TransportOfferingModel toModel = new TransportOfferingModel();
			toModel.setCode(code);
			toModel.setStockLevels(stocks);
			return toModel;
		}

		private ProductData createProductData(final String code)
		{
			final ProductData productData = new ProductData();
			productData.setCode(code);
			return productData;
		}

	}

}
