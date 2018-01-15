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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationOfferBreakdownHandlerTest
{
	@InjectMocks
	private ReservationOfferBreakdownHandler handler;


	@Mock
	private List<ProductType> notAncillaryProductTypes;
	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testEmptyOffers()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		handler.handle(null, testDataSetUp.createReservationData(Collections.emptyList()));
	}

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferPricingInfoData offerPricingInfoData1 = new OfferPricingInfoData();
		given(notAncillaryProductTypes.contains(ProductType.ANCILLARY)).willReturn(false);
		final ProductData productData = new ProductData();
		productData.setCode("product1");
		given(productConverter.convert(Matchers.any())).willReturn(productData);

		final PriceData priceData2 = new PriceData();
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(30.00), "GBP"))
				.thenReturn(priceData2);
		handler.handle(testDataSetUp.createOrderModel(),
				testDataSetUp.createReservationData(Stream.of(offerPricingInfoData1).collect(Collectors.toList())));
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData(final List<OfferPricingInfoData> offerPricingInfos)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setOfferPricingInfos(offerPricingInfos);
			return reservationData;
		}

		private AbstractOrderModel createOrderModel()
		{
			final AbstractOrderModel orderModel = new AbstractOrderModel();
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("GBP");
			orderModel.setCurrency(currency);
			final AbstractOrderEntryModel orderEntry1 = new AbstractOrderEntryModel();
			orderEntry1.setType(OrderEntryType.TRANSPORT);
			orderEntry1.setActive(true);
			orderEntry1.setBundleNo(1);
			orderEntry1.setQuantity(Long.valueOf(1));
			orderEntry1.setBasePrice(Double.valueOf(10));
			orderEntry1.setTotalPrice(Double.valueOf(20));
			final TravelOrderEntryInfoModel travelOrderEntryInfo1 = new TravelOrderEntryInfoModel();
			travelOrderEntryInfo1.setTransportOfferings(CollectionUtils.EMPTY_COLLECTION);
			travelOrderEntryInfo1.setTravellers(CollectionUtils.EMPTY_COLLECTION);
			orderEntry1.setTravelOrderEntryInfo(travelOrderEntryInfo1);
			final ProductModel product1 = new ProductModel();
			product1.setCode("product1");
			product1.setProductType(ProductType.ANCILLARY);
			orderEntry1.setProduct(product1);
			final AbstractOrderEntryModel orderEntry2 = new AbstractOrderEntryModel();
			orderEntry2.setType(OrderEntryType.TRANSPORT);
			orderEntry2.setActive(true);
			orderEntry2.setBundleNo(0);
			orderEntry2.setQuantity(Long.valueOf(1));
			orderEntry2.setBasePrice(Double.valueOf(10));
			orderEntry2.setTotalPrice(Double.valueOf(20));
			final TravelOrderEntryInfoModel travelOrderEntryInfo2 = new TravelOrderEntryInfoModel();
			travelOrderEntryInfo2.setTransportOfferings(CollectionUtils.EMPTY_COLLECTION);
			final TravellerModel traveller = new TravellerModel();
			travelOrderEntryInfo2.setTravellers(Stream.of(traveller).collect(Collectors.toList()));
			orderEntry2.setTravelOrderEntryInfo(travelOrderEntryInfo2);
			final ProductModel product2 = new ProductModel();
			product2.setCode("product1");
			product2.setProductType(ProductType.ANCILLARY);
			orderEntry2.setProduct(product2);
			orderEntry1.setOrder(orderModel);
			orderEntry2.setOrder(orderModel);
			orderModel.setEntries(Arrays.asList(orderEntry1, orderEntry2));
			return orderModel;
		}

	}
}