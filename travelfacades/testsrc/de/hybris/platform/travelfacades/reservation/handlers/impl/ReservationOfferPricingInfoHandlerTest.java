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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationOfferPricingInfoHandlerTest
{

	@InjectMocks
	ReservationOfferPricingInfoHandler reservationOfferPricingInfoHandler;

	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Mock
	private AbstractOrderModel orderModel;

	@Mock
	private List<ProductType> notAncillaryProductTypes;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private CurrencyModel currency;

	private TestDataSetup testDataSetUp;
	private TravellerData travellerData;

	@Before
	public void setUp()
	{
		testDataSetUp = new TestDataSetup();
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		travellerData = new TravellerData();
		travellerData.setLabel("Test_Traveller");
		given(travellerDataConverter.convert(Matchers.any(TravellerModel.class))).willReturn(travellerData);
		given(travelCommercePriceFacade.createPriceData(Double.valueOf(10)))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(10)));
		given(travelCommercePriceFacade.createPriceData(Double.valueOf(15)))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(15)));
		given(orderModel.getCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn("GBP");
	}

	@Test
	public void testPopulate()
	{
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.addAll(testDataSetUp.createOrderEntriesForPerPax());
		given(orderModel.getEntries()).willReturn(entries);
		given(notAncillaryProductTypes.contains(ProductType.ANCILLARY)).willReturn(false);
		final ProductData productData = new ProductData();
		productData.setCode("product1");
		given(productConverter.convert(Matchers.any())).willReturn(productData);

		given(travelCommercePriceFacade.createPriceData(Double.valueOf(10), "GBP"))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(10)));
		given(travelCommercePriceFacade.createPriceData(Double.valueOf(15), "GBP"))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(15)));

		final ReservationData reservationData = new ReservationData();
		reservationOfferPricingInfoHandler.handle(orderModel, reservationData);
		assertNotNull(reservationData.getOfferPricingInfos());
	}

	@Test
	public void testPopulateForPerBookingEntry()
	{
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.addAll(testDataSetUp.createOrderEntriesForPerBookingEntry());
		given(orderModel.getEntries()).willReturn(entries);
		given(notAncillaryProductTypes.contains(ProductType.ANCILLARY)).willReturn(false);
		final ProductData productData = new ProductData();
		productData.setCode("product2");
		given(productConverter.convert(Matchers.any())).willReturn(productData);

		given(travelCommercePriceFacade.createPriceData(Double.valueOf(10), "GBP"))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(10)));
		given(travelCommercePriceFacade.createPriceData(Double.valueOf(15), "GBP"))
				.willReturn(testDataSetUp.createPriceData(Double.valueOf(15)));

		final ReservationData reservationData = new ReservationData();
		reservationOfferPricingInfoHandler.handle(orderModel, reservationData);
		assertNotNull(reservationData.getOfferPricingInfos());
	}

	@Test
	public void testPopulateForPerPax()
	{
		final List<AbstractOrderEntryModel> entries = testDataSetUp.createOrderEntriesForPerBookingEntry();
		given(orderModel.getEntries()).willReturn(entries);
		given(notAncillaryProductTypes.contains(ProductType.ANCILLARY)).willReturn(false);
		final ProductData productData = new ProductData();
		productData.setCode("product1");
		given(productConverter.convert(Matchers.any())).willReturn(productData);
		final ReservationData reservationData = new ReservationData();
		reservationOfferPricingInfoHandler.handle(orderModel, reservationData);
		assertNotNull(reservationData.getOfferPricingInfos());
	}

	@Test
	public void testPopulateWithNoAncillaries()
	{
		final List<AbstractOrderEntryModel> entries = testDataSetUp.createOrderEntriesForPerBookingEntry();
		given(orderModel.getEntries()).willReturn(entries);
		given(notAncillaryProductTypes.contains(ProductType.ANCILLARY)).willReturn(true);
		final ProductData productData = new ProductData();
		productData.setCode("product1");
		given(productConverter.convert(Matchers.any())).willReturn(productData);
		final ReservationData reservationData = new ReservationData();
		reservationOfferPricingInfoHandler.handle(orderModel, reservationData);
		assertNull(reservationData.getOfferPricingInfos());
	}

	private class TestDataSetup
	{
		public List<AbstractOrderEntryModel> createOrderEntriesForPerPax()
		{

			final ProductModel product1 = createProductModel("product1", ProductType.ANCILLARY);
			final TravellerModel travellerModel = createTravellerModel("Test_Traveller");

			final TravelOrderEntryInfoModel travelOrderEntryInfo = createTravelOrderEntryInfoModel(
					Stream.of(travellerModel).collect(Collectors.toList()), Collections.emptyList());
			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.TRUE);

			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.TRUE);

			final TravellerModel travellerModel3 = createTravellerModel("TEST_TRAVELLER_3");
			final TravelOrderEntryInfoModel travelOrderEntryInfo3 = createTravelOrderEntryInfoModel(
					Stream.of(travellerModel3).collect(Collectors.toList()), Collections.emptyList());

			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo3, Boolean.TRUE);

			final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 1, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo3, Boolean.TRUE);

			final AbstractOrderEntryModel entry5 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 1, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo3, Boolean.FALSE);

			final ProductModel product6 = createProductModel("FareProduct", ProductType.FARE_PRODUCT);
			final TravelOrderEntryInfoModel travelOrderEntryInfo6 = new TravelOrderEntryInfoModel();
			final AbstractOrderEntryModel entry6 = createAbstractOrderEntryModel(product6, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo6, Boolean.TRUE);

			return Arrays.asList(entry1, entry2, entry3, entry4, entry5, entry6);
		}

		public List<AbstractOrderEntryModel> createOrderEntriesForPerBookingEntry()
		{

			final ProductModel product = createProductModel("product2", ProductType.ANCILLARY);

			final TravelOrderEntryInfoModel travelOrderEntryInfo = new TravelOrderEntryInfoModel();

			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(product, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.TRUE);

			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(product, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.FALSE);

			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(product, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.TRUE);

			final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(product, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 1, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, Boolean.TRUE);

			return Arrays.asList(entry1, entry2, entry3, entry4);
		}

		private AbstractOrderEntryModel createAbstractOrderEntryModel(final ProductModel product,
				final OrderEntryType orderEntryType, final OrderEntryStatus orderEntryStatus, final int bundleNo, final int quantity,
				final double basePrice, final double totalPrice, final TravelOrderEntryInfoModel travelOrderEntryInfo,
				final boolean isActive)
		{
			final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
			entry.setProduct(product);
			entry.setType(orderEntryType);
			entry.setBundleNo(bundleNo);
			entry.setQuantity(Long.valueOf(quantity));
			entry.setBasePrice(basePrice);
			entry.setTotalPrice(totalPrice);
			entry.setActive(isActive);
			if (product.getProductType().equals(ProductType.FARE_PRODUCT))
			{
				travelOrderEntryInfo.setOriginDestinationRefNumber(0);
			}
			entry.setTravelOrderEntryInfo(travelOrderEntryInfo);
			entry.setOrder(orderModel);
			return entry;
		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel();
			product.setCode(code);
			product.setProductType(productType);
			return product;
		}

		private TravelOrderEntryInfoModel createTravelOrderEntryInfoModel(final List<TravellerModel> travellers,
				final List<TransportOfferingModel> transportOfferings)
		{
			final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
			travelOrderEntryInfoModel.setTravellers(travellers);
			travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
			return travelOrderEntryInfoModel;
		}

		private TravellerModel createTravellerModel(final String label)
		{
			final TravellerModel travellerModel = new TravellerModel();
			travellerModel.setLabel(label);
			return travellerModel;
		}

		private PriceData createPriceData(final double price)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(price));
			priceData.setCurrencyIso("GBP");
			priceData.setFormattedValue("GBP " + price);
			return priceData;
		}
	}

}