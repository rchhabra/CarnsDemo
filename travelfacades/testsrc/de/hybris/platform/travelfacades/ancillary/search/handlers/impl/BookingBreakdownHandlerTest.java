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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for BookingBreakdownHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingBreakdownHandlerTest
{
	@InjectMocks
	private BookingBreakdownHandler handler;

	@Mock
	private ProductService productService;

	@Mock
	private ProductModel productModel;
	@Mock
	private TravelCommercePriceService travelCommercePriceService;
	@Mock
	private PriceInformation priceInfo;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testPopulateBookingBreakdown()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
	}

	@Test
	public void testPopulateBookingBreakdownWithoutTravelRestriction()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(null);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
	}

	@Test
	public void testPopulateBookingBreakdownWithoutCartCriteria()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_LEG.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
	}

	@Test
	public void testPopulateBookingBreakdownWithDifferentCartCriteria()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp.createTravelRestriction(StringUtils.EMPTY);
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
	}

	@Test
	public void testPopulateBookingBreakdownWithDifferentOfferGroup()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup2",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
		assertEquals(Integer.valueOf(0),
				offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown().getQuantity());
	}

	@Test
	public void testPopulateBookingBreakdownWithDifferentProductsInRequestAndResponse()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product2");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
		assertEquals(Integer.valueOf(0),
				offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown().getQuantity());
	}

	@Test
	public void testPopulateBookingBreakdownWithNullPriceData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP")).willReturn(null);
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
		assertNull(
				offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown().getPassengerFare());
	}

	@Test
	public void testPopulateBookingBreakdownWithPriceData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravelRestrictionData travelRestriction = testDataSetUp
				.createTravelRestriction(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData(travelRestriction);
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferPricingInfoData offerPricingInfoForRequest = testDataSetUp.createOfferpricingInfoDataForRequest("product1");
		final OfferGroupData offerGroupDataForRequest = testDataSetUp.createOfferGroupDataForRrequest("offerGroup1",
				offerPricingInfoForRequest);
		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(offerGroupDataForRequest);
		offerRequestData.setSelectedOffers(selectedOffers);
		given(productService.getProductForCode("product1")).willReturn(productModel);
		final PriceValue priceInfoValue = new PriceValue("GBP", 10d, false);
		given(travelCommercePriceService.getProductWebPrice(productModel, null)).willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceInfoValue);
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP")).willReturn(null);
		final PriceData price = new PriceData();
		price.setValue(BigDecimal.valueOf(10d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP")).willReturn(price);
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown());
		assertNotNull(
				offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getBookingBreakdown().getPassengerFare());
	}

	private class TestDataSetUp
	{

		public TravelRestrictionData createTravelRestriction(final String cartCriteriaType)
		{
			final TravelRestrictionData travelRestriction = new TravelRestrictionData();
			travelRestriction.setAddToCartCriteria(cartCriteriaType);
			return travelRestriction;
		}

		public OfferPricingInfoData createOfferpricingInfoData(final TravelRestrictionData travelRestriction)
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode("product1");
			offerPricingInfo.setProduct(product);
			offerPricingInfo.setTravelRestriction(travelRestriction);
			return offerPricingInfo;
		}

		public OfferGroupData createOfferGroupData(final OfferPricingInfoData offerPricingInfo)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode("offerGroup1");
			if (Objects.isNull(offerPricingInfo))
			{
				offerGroupData.setOfferPricingInfos(Collections.EMPTY_LIST);
			}
			else
			{
				offerGroupData.setOfferPricingInfos(Stream.of(offerPricingInfo).collect(Collectors.toList()));
			}
			return offerGroupData;
		}

		public OfferResponseData createOfferResponseData(final OfferGroupData offerGroupData)
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			if (Objects.isNull(offerGroupData))
			{
				offerResponseData.setOfferGroups(Collections.EMPTY_LIST);
			}
			else
			{
				offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));
			}
			return offerResponseData;
		}


		public OfferPricingInfoData createOfferpricingInfoDataForRequest(final String productCode)
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode(productCode);
			offerPricingInfo.setProduct(product);
			final BookingBreakdownData bookingBreakdown = new BookingBreakdownData();
			bookingBreakdown.setQuantity(2);
			offerPricingInfo.setBookingBreakdown(bookingBreakdown);
			return offerPricingInfo;
		}

		public OfferGroupData createOfferGroupDataForRrequest(final String code, final OfferPricingInfoData offerPricingInfo)
		{
			final OfferGroupData offerGroup = new OfferGroupData();
			offerGroup.setCode(code);
			offerGroup.setOfferPricingInfos(Stream.of(offerPricingInfo).collect(Collectors.toList()));
			return offerGroup;
		}

		public SelectedOffersData createSelectedOffersData(final OfferGroupData offerGroupData)
		{
			final SelectedOffersData selectedOffer = new SelectedOffersData();
			selectedOffer.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));
			return selectedOffer;
		}

	}

}
