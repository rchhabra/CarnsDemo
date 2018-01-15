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
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for OfferGroupOfferRestrictionHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferGroupOfferRestrictionHandlerTest
{
	@InjectMocks
	private OfferGroupOfferRestrictionHandler handler;
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;
	@Mock
	private ProductService productService;
	@Mock
	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;
	@Mock
	private ProductModel productModel;
	@Mock
	private TravelRestrictionModel travelRestrictionModel;

	@Test
	public void testPopulateOfferRestrictionWithEmptyOfferGroups()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(null);
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}

	@Test
	public void testPopulateOfferRestrictionWithEmptyOfferPricingInfo()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(null);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		handler.handle(offerRequestData, offerResponseData);
	}


	@Test
	public void testPopulateOfferRestrictionWithNullTravelRestriction()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData();
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		given(productService.getProductForCode("product1")).willReturn(productModel);
		given(productModel.getTravelRestriction()).willReturn(null);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString())).willReturn(Matchers.anyString());
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getTravelRestriction());
	}

	@Test
	public void testPopulateOfferRestriction()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferPricingInfoData offerPricingInfo = testDataSetUp.createOfferpricingInfoData();
		final OfferGroupData offerGroupData = testDataSetUp.createOfferGroupData(offerPricingInfo);
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(offerGroupData);
		final OfferRequestData offerRequestData = new OfferRequestData();
		given(productService.getProductForCode("product1")).willReturn(productModel);
		given(productModel.getTravelRestriction()).willReturn(travelRestrictionModel);
		final TravelRestrictionData travelRestrictionData = new TravelRestrictionData();
		given(travelRestrictionConverter.convert(travelRestrictionModel)).willReturn(travelRestrictionData);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString())).willReturn(Matchers.anyString());
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups().get(0).getOfferPricingInfos().get(0).getTravelRestriction());
	}

	private class TestDataSetUp
	{

		public OfferPricingInfoData createOfferpricingInfoData()
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode("product1");
			offerPricingInfo.setProduct(product);
			return offerPricingInfo;
		}

		public OfferGroupData createOfferGroupData(final OfferPricingInfoData offerPricingInfo)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
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

	}

}