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
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for AccommodationReservationReservedRoomStayServiceDetailsHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationReservedRoomStayServiceDetailsHandlerTest
{
	@InjectMocks
	AccommodationReservationReservedRoomStayServiceDetailsHandler handler;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	private final String TEST_PRODUCT_CODE = "TEST_PRODUCT_CODE";

	private final Date date = new Date();
	private TestSetup testSetup;

	@Before
	public void setUp()
	{
		testSetup = new TestSetup();
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP"))
				.thenReturn(testSetup.createPriceData(10d));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.thenReturn(testSetup.createPriceData(20d));
	}

	@Test
	public void testHandleForEmptyRoomStays()
	{
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		Mockito.when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(OrderModel.class)))
				.thenReturn(Collections.emptyList());
		handler.handle(new OrderModel(), accommodationReservationData);
		Mockito.verify(bookingService, Mockito.times(0)).getAccommodationOrderEntryGroups(Matchers.any(OrderModel.class));

		handler.handle(new OrderModel(), null);
		Mockito.verify(bookingService, Mockito.times(0)).getAccommodationOrderEntryGroups(Matchers.any(OrderModel.class));
	}

	@Test
	public void testHandle()
	{
		final TestSetup testSetup = new TestSetup();
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData
				.setRoomStays(Stream.of(testSetup.createReservedRoomStayData(0, 3), testSetup.createReservedRoomStayData(1, 3))
						.collect(Collectors.toList()));
		Mockito.when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(OrderModel.class)))
				.thenReturn(Stream
						.of(testSetup.createAccommodationOrderEntryGroupModel(0), testSetup.createAccommodationOrderEntryGroupModel(1))
						.collect(Collectors.toList()));

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		final OrderModel orderModel = new OrderModel();
		orderModel.setCurrency(currencyModel);

		handler.handle(orderModel, accommodationReservationData);
		Mockito.verify(bookingService, Mockito.times(1)).getAccommodationOrderEntryGroups(Matchers.any(OrderModel.class));
	}

	private class TestSetup
	{
		private ReservedRoomStayData createReservedRoomStayData(final int roomStayRefNumber,
				final int differencInCheckInAndCheckOut)
		{
			final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
			reservedRoomStayData.setRoomStayRefNumber(roomStayRefNumber);
			reservedRoomStayData.setCheckOutDate(
					Date.from(LocalDateTime.now().plusDays(differencInCheckInAndCheckOut).atZone(ZoneId.systemDefault()).toInstant()));
			reservedRoomStayData.setCheckInDate(date);
			final List<ServiceData> services = new ArrayList<>();
			final ServiceData service1 = new ServiceData();
			final ServiceData service2 = new ServiceData();
			service1.setCode(TEST_PRODUCT_CODE);
			service1.setQuantity(0);
			service2.setCode(StringUtils.EMPTY);
			service2.setQuantity(1);
			services.add(service1);
			services.add(service2);
			reservedRoomStayData.setServices(services);
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setCode("TEST_RATE_PLAN_CODE");
			reservedRoomStayData.setRatePlans(Collections.singletonList(ratePlanData));
			return reservedRoomStayData;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(final int roomStayRefNumber)
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
			accommodationOrderEntryGroupModel.setRoomStayRefNumber(roomStayRefNumber);
			accommodationOrderEntryGroupModel.setEntries(createEntries());
			return accommodationOrderEntryGroupModel;
		}

		private List<AbstractOrderEntryModel> createEntries()
		{
			final ProductModel product1 = createRoomRateProductModel("TEST_ROOM_RATE_PRODUCT");
			final ProductModel product2 = createProductModel("TEST_PRODUCT_1");
			final List<AbstractOrderEntryModel> entries = new ArrayList<>();
			entries.add(createAbstractOrderEntryModel(true, OrderEntryStatus.LIVING, product2, 1, Double.valueOf(10d),
					Double.valueOf(20d)));
			entries.add(createAbstractOrderEntryModel(true, OrderEntryStatus.LIVING, product1, 1, Double.valueOf(10d),
					Double.valueOf(20d)));
			entries.add(createAbstractOrderEntryModel(false, OrderEntryStatus.DEAD, product1, 1, Double.valueOf(10d),
					Double.valueOf(20d)));
			entries.add(
					createAbstractOrderEntryModel(true, OrderEntryStatus.DEAD, product1, 1, Double.valueOf(10d), Double.valueOf(20d)));
			return entries;
		}

		private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive,
				final OrderEntryStatus orderEntryStatus, final ProductModel product, final int quantity, final double basePrice,
				final double totalPrice)
		{
			final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
			abstractOrderEntryModel.setActive(isActive);
			abstractOrderEntryModel.setProduct(product);
			abstractOrderEntryModel.setQuantityStatus(orderEntryStatus);
			abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
			abstractOrderEntryModel.setBasePrice(basePrice);
			abstractOrderEntryModel.setTotalPrice(totalPrice);
			return abstractOrderEntryModel;
		}

		private RoomRateProductModel createRoomRateProductModel(final String productCode)
		{
			final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
			roomRateProductModel.setCode(productCode);
			return roomRateProductModel;
		}

		private ProductModel createProductModel(final String productCode)
		{
			final ProductModel productModel = new ProductModel();
			productModel.setCode(productCode);
			return productModel;
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
