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

package de.hybris.platform.travelfacades.travel.process.email.context;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
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
import java.util.Collections;

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
	private GlobalTravelReservationData reservationData;

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
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	BookingService bookingService;

	@Mock
	private TravellerService travellerService;

	private static final String STORE_NAME = "Hotels Store";

	private static final String ORIGINAL_ORDER_CODE = "00001001";
	private TestDataSetUp testDataSetUp;

	@Before
	public void setUp() throws Exception
	{
		testDataSetUp = new TestDataSetUp();
		MockitoAnnotations.initMocks(this);
		bookingAmendmentEmailContext.setReservationFacade(reservationFacade);
		bookingAmendmentEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);

		when(originalOrderModel.getCode()).thenReturn(ORIGINAL_ORDER_CODE);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(AbstractOrderModel.class))).thenReturn(null);
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		when(orderModel.getCurrency()).thenReturn(currencyModel);

		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(200d), "GBP"))
				.thenReturn(testDataSetUp.createPriceData(200d));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(400d), "GBP"))
				.thenReturn(testDataSetUp.createPriceData(400d));
	}

	@Test
	public void testInitWithOrder()
	{

		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(orderModel.getOriginalOrder()).thenReturn(originalOrderModel);
		when(originalOrderModel.getCode()).thenReturn("TEST_ORDER_CODE");

		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		entry1.setType(OrderEntryType.TRANSPORT);
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setType(OrderEntryType.TRANSPORT);
		when(originalOrderModel.getEntries()).thenReturn(Collections.singletonList(entry1));
		when(orderModel.getEntries()).thenReturn(Collections.singletonList(entry2));

		when(bookingService.getOrderTotalPaidForOrderEntryType(originalOrderModel, OrderEntryType.TRANSPORT))
				.thenReturn(BigDecimal.valueOf(400d));
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.TRANSPORT))
				.thenReturn(BigDecimal.valueOf(200d));

		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(reservationData, bookingAmendmentEmailContext.getTravelReservationData());
		Assert.assertEquals("TEST_ORDER_CODE", bookingAmendmentEmailContext.getReservationCode());
		Assert.assertEquals(testDataSetUp.createPriceData(200d).getFormattedValue(),
				bookingAmendmentEmailContext.getTotalToRefund());

		when(bookingService.getOrderTotalPaidForOrderEntryType(originalOrderModel, OrderEntryType.TRANSPORT))
				.thenReturn(BigDecimal.valueOf(200d));
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.TRANSPORT))
				.thenReturn(BigDecimal.valueOf(400d));

		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(testDataSetUp.createPriceData(200d).getFormattedValue(), bookingAmendmentEmailContext.getTotalToPay());
	}

	@Test
	public void testInitWithNoOrder()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(orderModel.getOriginalOrder()).thenReturn(null);

		Mockito.when(
				travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
						Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceData(200d));

		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertNotNull(bookingAmendmentEmailContext.getTravelReservationData());
		Assert.assertNotEquals("TEST_ORDER_CODE", bookingAmendmentEmailContext.getReservationCode());
	}

	private class TestDataSetUp
	{

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
