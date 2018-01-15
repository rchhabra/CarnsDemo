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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for OfferGroupOfferPricingInfoRequestHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferGroupOfferPricingInfoRequestHandlerTest
{
	@InjectMocks
	private OfferGroupOfferPricingInfoRequestHandler handler;
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;

	@Test
	public void testPopulateOfferPricingInfoWithPerBookingCartCriteriaType()
	{
		final TestDataSetUp test = new TestDataSetUp();
		final ReservationData reservationData = new ReservationData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		final TravelRestrictionData travelRestriction = new TravelRestrictionData();
		travelRestriction.setAddToCartCriteria(AddToCartCriteriaType.PER_BOOKING.getCode());
		given(travelRestrictionFacade.getTravelRestrictionForCategory(Matchers.anyString())).willReturn(travelRestriction);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString()))
				.willReturn(AddToCartCriteriaType.PER_BOOKING.getCode());
		final OfferGroupData offerGroupData = test.createOfferGroupDataForRrequest("offerGroup1");
		final SelectedOffersData selectedOffers = test.createSelectedOffersData(offerGroupData);
		offerRequestData.setSelectedOffers(selectedOffers);
		reservationData.setOfferPricingInfos(
				Stream.of(test.createOfferpricingInfoDataForReservationData("product1")).collect(Collectors.toList()));
		handler.handle(reservationData, offerRequestData);
		assertNotNull(offerRequestData.getSelectedOffers().getOfferGroups().get(0).getOfferPricingInfos());
	}

	@Test
	public void testPopulateOfferPricingInfoWithPerPaxCartCriteriaType()
	{
		final TestDataSetUp test = new TestDataSetUp();
		final ReservationData reservationData = new ReservationData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		final TravelRestrictionData travelRestriction = new TravelRestrictionData();
		travelRestriction.setAddToCartCriteria(AddToCartCriteriaType.PER_PAX.getCode());
		given(travelRestrictionFacade.getTravelRestrictionForCategory(Matchers.anyString())).willReturn(travelRestriction);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString()))
				.willReturn(AddToCartCriteriaType.PER_PAX.getCode());
		final OfferGroupData offerGroupData = test.createOfferGroupDataForRrequest("offerGroup1");
		final SelectedOffersData selectedOffers = test.createSelectedOffersData(offerGroupData);
		offerRequestData.setSelectedOffers(selectedOffers);
		reservationData.setOfferPricingInfos(
				Stream.of(test.createOfferpricingInfoDataForReservationData("product1")).collect(Collectors.toList()));
		handler.handle(reservationData, offerRequestData);
		assertNotNull(offerRequestData.getSelectedOffers().getOfferGroups().get(0).getOfferPricingInfos());
	}

	@Test
	public void testPopulateOfferPricingInfoWithEmptyCriteriaType()
	{
		final TestDataSetUp test = new TestDataSetUp();
		final ReservationData reservationData = new ReservationData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		final TravelRestrictionData travelRestriction = new TravelRestrictionData();
		travelRestriction.setAddToCartCriteria(StringUtils.EMPTY);
		given(travelRestrictionFacade.getTravelRestrictionForCategory(Matchers.anyString())).willReturn(travelRestriction);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString()))
				.willReturn(AddToCartCriteriaType.PER_PAX.getCode());
		final OfferGroupData offerGroupData = test.createOfferGroupDataForRrequest("offerGroup1");
		final SelectedOffersData selectedOffers = test.createSelectedOffersData(offerGroupData);
		offerRequestData.setSelectedOffers(selectedOffers);
		reservationData.setOfferPricingInfos(
				Stream.of(test.createOfferpricingInfoDataForReservationData("product1")).collect(Collectors.toList()));
		handler.handle(reservationData, offerRequestData);
		assertNull(offerRequestData.getSelectedOffers().getOfferGroups().get(0).getOfferPricingInfos());
	}

	@Test
	public void testPopulateOfferPricingInfoWithoutProductCategory()
	{
		final TestDataSetUp test = new TestDataSetUp();
		final ReservationData reservationData = new ReservationData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		final TravelRestrictionData travelRestriction = new TravelRestrictionData();
		travelRestriction.setAddToCartCriteria(StringUtils.EMPTY);
		given(travelRestrictionFacade.getTravelRestrictionForCategory(Matchers.anyString())).willReturn(travelRestriction);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString()))
				.willReturn(AddToCartCriteriaType.PER_PAX.getCode());
		final OfferGroupData offerGroupData = test.createOfferGroupDataForRrequest("offerGroup1");
		final SelectedOffersData selectedOffers = test.createSelectedOffersData(offerGroupData);
		offerRequestData.setSelectedOffers(selectedOffers);
		reservationData.setOfferPricingInfos(
				Stream.of(test.createOfferpricingInfoDataForReservationDataWithoutProductCategory("product1"))
						.collect(Collectors.toList()));
		handler.handle(reservationData, offerRequestData);
		assertNull(offerRequestData.getSelectedOffers().getOfferGroups().get(0).getOfferPricingInfos());
	}

	private class TestDataSetUp
	{
		public OfferPricingInfoData createOfferpricingInfoDataForReservationData(final String productCode)
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode(productCode);
			final CategoryData category = new CategoryData();
			product.setCategories(Stream.of(category).collect(Collectors.toList()));
			offerPricingInfo.setProduct(product);
			return offerPricingInfo;
		}

		public OfferPricingInfoData createOfferpricingInfoDataForReservationDataWithoutProductCategory(final String productCode)
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode(productCode);
			offerPricingInfo.setProduct(product);
			return offerPricingInfo;
		}

		public OfferGroupData createOfferGroupDataForRrequest(final String code)
		{
			final OfferGroupData offerGroup = new OfferGroupData();
			offerGroup.setCode(code);
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
