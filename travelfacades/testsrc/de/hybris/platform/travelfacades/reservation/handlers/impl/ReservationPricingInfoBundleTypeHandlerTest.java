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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelservices.enums.BundleType;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import jersey.repackaged.com.google.common.base.Objects;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationPricingInfoBundleTypeHandlerTest
{
	@InjectMocks
	ReservationPricingInfoBundleTypeHandler reservationPricingInfoBundleTypeHandler;

	@Mock
	private EnumerationService enumerationService;

	private TestSetup testSetup;

	private final String DEFAULT_BUNDLE_TEMPLATE_NAME = "TEST_BUNDLE_TEMPLATE_NAME";

	@Before
	public void setUp()
	{
		testSetup = new TestSetup();
		Mockito.when(enumerationService.getEnumerationName(Matchers.any())).thenReturn(DEFAULT_BUNDLE_TEMPLATE_NAME);
	}

	@Test
	public void testHandleForEmptyReservationItems()
	{
		final ReservationData reservationData = new ReservationData();
		reservationPricingInfoBundleTypeHandler.handle(new OrderModel(), reservationData);
		Assert.assertNull(reservationData.getReservationItems());
	}

	@Test
	public void testHandleForAccomodationOnlyJourney()
	{
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.ACCOMMODATION);
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(Collections.singletonList(testSetup.createReservationItemData(false, 0)));

		final OrderModel orderModel = new OrderModel();
		orderModel
				.setEntries(Collections.singletonList(testSetup.createAbstractOrderEntryModel(product1, OrderEntryType.ACCOMMODATION,
						OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), null, null, Boolean.TRUE)));
		reservationPricingInfoBundleTypeHandler.handle(orderModel, reservationData);
		Assert.assertNull(
				reservationData.getReservationItems().get(0).getReservationPricingInfo().getItineraryPricingInfo().getBundleType());
	}

	@Test
	public void testHandle()
	{
		final List<ReservationItemData> reservationItemDatas = new ArrayList<>();
		reservationItemDatas.add(testSetup.createReservationItemData(false, 0));
		reservationItemDatas.add(testSetup.createReservationItemData(true, 0));
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(reservationItemDatas);

		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(testSetup.createOrderEntries());
		reservationPricingInfoBundleTypeHandler.handle(orderModel, reservationData);
		Assert.assertTrue(
				reservationData.getReservationItems().stream()
						.filter(reservationItem -> Objects.equal(BundleType.BUSINESS.getCode(),
								reservationItem.getReservationPricingInfo().getItineraryPricingInfo().getBundleType()))
						.findAny().isPresent());
	}

	private class TestSetup
	{
		private ReservationItemData createReservationItemData(final boolean hasReservationPricingInfo,
				final int originDestinationRefNumber)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setOriginDestinationRefNumber(originDestinationRefNumber);
			if (hasReservationPricingInfo)
			{
				reservationItemData.setReservationPricingInfo(createReservationPricingInfo());
			}
			return reservationItemData;

		}

		private ReservationPricingInfoData createReservationPricingInfo()
		{
			final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
			return reservationPricingInfo;
		}

		public List<AbstractOrderEntryModel> createOrderEntries()
		{

			final ProductModel product1 = createProductModel("product1", ProductType.ANCILLARY);
			final ProductModel product2 = createProductModel("product1", ProductType.FARE_PRODUCT);

			final TravelOrderEntryInfoModel travelOrderEntryInfo = new TravelOrderEntryInfoModel();
			travelOrderEntryInfo.setOriginDestinationRefNumber(0);

			final TravelOrderEntryInfoModel travelOrderEntryInfo2 = new TravelOrderEntryInfoModel();
			travelOrderEntryInfo.setOriginDestinationRefNumber(1);

			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, null, Boolean.FALSE);


			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(product1, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, null, Boolean.TRUE);


			final BundleTemplateModel parentBundleTemplate = createBundleTemplateModel(true, BundleType.ECONOMY, null);

			final BundleTemplateModel bundleTemplate1 = createBundleTemplateModel(true, BundleType.BUSINESS, parentBundleTemplate);
			final BundleTemplateModel bundleTemplate2 = createBundleTemplateModel(false, BundleType.ECONOMY, parentBundleTemplate);
			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(product2, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, bundleTemplate1,
					Boolean.TRUE);

			final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(product2, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, bundleTemplate1,
					Boolean.FALSE);

			final AbstractOrderEntryModel entry5 = createAbstractOrderEntryModel(product2, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 1, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo, bundleTemplate1,
					Boolean.TRUE);


			final AbstractOrderEntryModel entry6 = createAbstractOrderEntryModel(product2, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 0, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo2, bundleTemplate2,
					Boolean.TRUE);

			final AbstractOrderEntryModel entry7 = createAbstractOrderEntryModel(product2, OrderEntryType.TRANSPORT,
					OrderEntryStatus.LIVING, 1, 1, Double.valueOf(10), Double.valueOf(15), travelOrderEntryInfo2, bundleTemplate2,
					Boolean.TRUE);


			return Arrays.asList(entry1, entry2, entry3, entry4, entry5, entry6, entry7);
		}

		private AbstractOrderEntryModel createAbstractOrderEntryModel(final ProductModel product,
				final OrderEntryType orderEntryType, final OrderEntryStatus orderEntryStatus, final int bundleNo, final int quantity,
				final double basePrice, final double totalPrice, final TravelOrderEntryInfoModel travelOrderEntryInfo,
				final BundleTemplateModel bundleTemplate, final boolean isActive)
		{
			final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
			entry.setProduct(product);
			entry.setType(orderEntryType);
			entry.setBundleTemplate(bundleTemplate);
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
			return entry;
		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel();
			product.setCode(code);
			product.setProductType(productType);
			return product;
		}

		private BundleTemplateModel createBundleTemplateModel(final boolean hasType, final BundleType bundleType,
				final BundleTemplateModel parentBundleTemplate)
		{
			final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
			if (hasType)
			{
				bundleTemplateModel.setType(bundleType);
			}
			bundleTemplateModel.setParentTemplate(parentBundleTemplate);
			return bundleTemplateModel;

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