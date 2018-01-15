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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for OfferGroupOfferPricingInfoHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferGroupOfferPricingInfoHandlerTest
{
	@InjectMocks
	private OfferGroupOfferPricingInfoHandler handler;
	@Mock
	private TravelPromotionsFacade travelPromotionsFacade;
	@Mock
	private ProductService productService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private CategoryModel categoryModel;
	@Mock
	private ProductModel product;
	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private TravelCartService travelCartService;
	@Mock
	private TravelCommerceStockService travelCommerceStockService;

	@Test
	public void testPopulateOfferPricingInfoWithUserEligibleForPromotions()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(true);
		final TravelRestrictionData travelRestriction = testData
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		Mockito.doNothing().when(travelPromotionsFacade).populatePotentialPromotions(product, productData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithPerPaxCartCriteria()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(true);
		final TravelRestrictionData travelRestriction = testData.createTravelRestriction(AddToCartCriteriaType.PER_PAX.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		Mockito.doNothing().when(travelPromotionsFacade).populatePotentialPromotions(product, productData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithStockNotFoundException()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(true);
		final TravelRestrictionData travelRestriction = testData.createTravelRestriction(AddToCartCriteriaType.PER_PAX.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList()))
				.willThrow(new StockLevelNotFoundException("Stock Not found"));
		final ProductData productData = new ProductData();
		Mockito.doNothing().when(travelPromotionsFacade).populatePotentialPromotions(product, productData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithUserNotEligibleForPromotions()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(false);
		final TravelRestrictionData travelRestriction = testData
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithNullTravelRestriction()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(false);
		final TravelRestrictionData travelRestriction = testData
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(null);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithEmptyCartCriteria()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(false);
		final TravelRestrictionData travelRestriction = testData
				.createTravelRestriction(StringUtils.EMPTY);
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferPricingInfoWithdifferentCartCriteria()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(travelPromotionsFacade.isCurrentUserEligibleForTravelPromotions()).willReturn(false);
		final TravelRestrictionData travelRestriction = testData
				.createTravelRestriction(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
		final OfferGroupData offerGroupData = testData.createOfferGroupData(travelRestriction);
		final OfferResponseData offerResponseData = testData.createOfferResponseData(offerGroupData);
		given(categoryService.getCategoryForCode(Matchers.anyString())).willReturn(categoryModel);
		given(productService.getProductsForCategory(categoryModel)).willReturn(Stream.of(product).collect(Collectors.toList()));
		given(travelCommerceStockService.getStockLevel(product, Collections.emptyList())).willReturn(10L);
		final ProductData productData = new ProductData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	private class TestDataSetUp
	{

		public TravelRestrictionData createTravelRestriction(final String addToCartCriteriaType)
		{
			final TravelRestrictionData travelRestriction = new TravelRestrictionData();
			travelRestriction.setAddToCartCriteria(addToCartCriteriaType);
			return travelRestriction;
		}

		public OfferGroupData createOfferGroupData(final TravelRestrictionData travelRestriction)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setTravelRestriction(travelRestriction);
			return offerGroupData;
		}

		public OfferResponseData createOfferResponseData(final OfferGroupData offerGroupData)
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));
			return offerResponseData;
		}

	}
}
