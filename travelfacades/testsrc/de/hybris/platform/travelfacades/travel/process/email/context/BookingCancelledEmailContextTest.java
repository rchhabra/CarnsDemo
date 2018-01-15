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
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link BookingCancelledEmailContext}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingCancelledEmailContextTest
{
	@InjectMocks
	private BookingCancelledEmailContext bookingCancelledEmailContext;

	@Mock
	private ReservationPipelineManager reservationPipelineManager;

	@Mock
	private GlobalTravelReservationData reservationData;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private EmailPageModel emailPageModel;

	@Mock
	private LanguageModel languageModel;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private OrderModel orderModel;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private static final String REFUND_AMOUNT = "£10.00";

	private static final String STORE_NAME = "Travel Store";

	@Mock
	private AccommodationReservationData accommodationReservationData;

	@Mock
	private GlobalTravelReservationData travelReservationData;

	@Mock
	private TravellerService travellerService;


	@Before
	public void setUp()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getStore()).thenReturn(baseStoreModel);

		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(travelReservationData);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		when(orderModel.getEntries()).thenReturn(entries);
		when(travellerService.getTravellers(entries)).thenReturn(Collections.emptyList());
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("GBP");
		when(orderModel.getCurrency()).thenReturn(currency);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
	}

	@Test
	public void testInit()
	{

		when(bookingService.getTotalToRefund(Matchers.any(OrderModel.class))).thenReturn(BigDecimal.TEN);
		final PriceData priceData = new PriceData();
		priceData.setFormattedValue(REFUND_AMOUNT);
		when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.TEN,
				orderProcessModel.getOrder().getCurrency()))
				.thenReturn(priceData);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(reservationData);
		bookingCancelledEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(bookingCancelledEmailContext.getStoreName(), STORE_NAME);
		Assert.assertEquals(bookingCancelledEmailContext.getTotalToRefund(), REFUND_AMOUNT);
	}

	@Test
	public void testNoRefund()
	{
		when(bookingService.getTotalToRefund(Matchers.any(OrderModel.class))).thenReturn(BigDecimal.ZERO);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(reservationData);
		bookingCancelledEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(bookingCancelledEmailContext.getStoreName(), STORE_NAME);
	}

	@Test
	public void testNullRefund()
	{
		when(bookingService.getTotalToRefund(Matchers.any(OrderModel.class))).thenReturn(null);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(reservationData);
		bookingCancelledEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(bookingCancelledEmailContext.getStoreName(), STORE_NAME);
	}
}
