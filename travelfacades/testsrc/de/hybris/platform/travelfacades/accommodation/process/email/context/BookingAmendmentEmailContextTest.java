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

package de.hybris.platform.travelfacades.accommodation.process.email.context;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link BookingAmendmentEmailContext}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingAmendmentEmailContextTest
{

	@InjectMocks
	private BookingAmendmentEmailContext bookingAmendmentEmailContext;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private AccommodationReservationData reservationData;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private EmailPageModel emailPageModel;
	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private OrderModel orderModel;

	@Mock
	private OrderModel originalOrderModel;

	@Mock
	BookingService bookingService;
	
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private TravellerService travellerService;

	private static final String STORE_NAME = "Hotels Store";

	private static final String ORIGINAL_ORDER_CODE = "00001001";


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingAmendmentEmailContext.setReservationFacade(reservationFacade);
		bookingAmendmentEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);

		when(originalOrderModel.getCode()).thenReturn(ORIGINAL_ORDER_CODE);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(AbstractOrderModel.class))).thenReturn(null);
	}

	@Test
	public void testInitWithOrder()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getAccommodationReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		final CurrencyModel curreny = Mockito.mock(CurrencyModel.class);
		when(orderModel.getCurrency()).thenReturn(curreny);
		when(curreny.getIsocode()).thenReturn("£");
		when(orderModel.getOriginalOrder()).thenReturn(originalOrderModel);
		when(originalOrderModel.getCode()).thenReturn("TEST_ORDER_CODE");
		when(bookingService.getOrderTotalPaidForOrderEntryType(originalOrderModel, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.ZERO);
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.TEN);
		final PriceData priceData = new PriceData();
		priceData.setFormattedValue("£10");
		when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.TEN, "£")).thenReturn(priceData);
		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(reservationData, bookingAmendmentEmailContext.getAccommodationReservationData());
		Assert.assertEquals("TEST_ORDER_CODE", bookingAmendmentEmailContext.getReservationCode());
	}

	@Test
	public void testInitWithNoOrder()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getAccommodationReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		final CurrencyModel curreny = Mockito.mock(CurrencyModel.class);
		when(orderModel.getCurrency()).thenReturn(curreny);
		when(curreny.getIsocode()).thenReturn("£");
		when(orderModel.getOriginalOrder()).thenReturn(null);

		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.TEN);

		final PriceData priceData = new PriceData();
		priceData.setFormattedValue("£10");
		when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.TEN, "£")).thenReturn(priceData);

		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertNotNull(bookingAmendmentEmailContext.getAccommodationReservationData());
		Assert.assertNotEquals("TEST_ORDER_CODE", bookingAmendmentEmailContext.getReservationCode());
	}
}
