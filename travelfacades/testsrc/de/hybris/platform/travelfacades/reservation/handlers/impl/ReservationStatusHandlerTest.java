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
*/

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationStatusHandlerTest
{
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private I18NService i18NService;
	@Mock
	private BookingService bookingService;

	@InjectMocks
	private ReservationStatusHandler reservationStatusHandler;

	@Test
	public void testPopulate()
	{
		final OrderModel order = new OrderModel()
		{
			@Override
			public String getVersionID()
			{
				return "1.0";
			}

			@Override
			public OrderStatus getTransportationOrderStatus()
			{
				return OrderStatus.ACTIVE;
			}

			@Override
			public String getCode()
			{
				return "0001";
			}

			@Override
			public OrderStatus getStatus()
			{
				return OrderStatus.ACTIVE;
			}

			@Override
			public List<AbstractOrderEntryModel> getEntries()
			{
				final OrderEntryModel orderEntry = new OrderEntryModel();
				orderEntry.setType(OrderEntryType.TRANSPORT);
				return Stream.of(orderEntry).collect(Collectors.toList());
			}

		};
		order.setTransportationOrderStatus(order.getStatus());
		final BaseStoreModel baseStore = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		given(customerAccountService.getOrderForCode(order.getCode(), baseStore)).willReturn(order);
		final Locale locale = Locale.ENGLISH;
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(enumerationService.getEnumerationName(order.getStatus(), locale)).willReturn("Active");
		given(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.isReservationCancelled(order, OrderEntryType.TRANSPORT)).willReturn(false);
		final ReservationData reservationData = new ReservationData();
		reservationStatusHandler.handle(order, reservationData);
		assertEquals("ACTIVE", reservationData.getBookingStatusCode());
		assertEquals("Active", reservationData.getBookingStatusName());
	}

	@Test
	public void testPopulateVersionNull()
	{
		final OrderModel order = new OrderModel(){
			@Override
			public String getVersionID()
			{
				return null;
			}

			@Override
			public OrderStatus getTransportationOrderStatus()
			{
				return OrderStatus.ACTIVE;
			}

			@Override
			public String getCode()
			{
				return "0001";
			}

			@Override
			public OrderStatus getStatus()
			{
				return OrderStatus.ACTIVE;
			}

			@Override
			public List<AbstractOrderEntryModel> getEntries()
			{
				final OrderEntryModel orderEntry = new OrderEntryModel();
				orderEntry.setType(OrderEntryType.TRANSPORT);
				return Stream.of(orderEntry).collect(Collectors.toList());
			}
		};
		final Locale locale = Locale.ENGLISH;
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(enumerationService.getEnumerationName(order.getStatus(), locale)).willReturn("Active");
		final ReservationData reservationData = new ReservationData();
		reservationStatusHandler.handle(order, reservationData);
		assertEquals("ACTIVE", reservationData.getBookingStatusCode());
		assertEquals("Active", reservationData.getBookingStatusName());
	}

	@Test
	public void testPopulateWithCartModel()
	{
		final CartModel cart = new CartModel()
		{
			@Override
			public OrderStatus getStatus()
			{
				return OrderStatus.ACTIVE;
			}

			@Override
			public OrderStatus getTransportationOrderStatus()
			{
				return OrderStatus.ACTIVE;
			}

		};
		final Locale locale = Locale.ENGLISH;
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(enumerationService.getEnumerationName(cart.getStatus(), locale)).willReturn("Active");
		final ReservationData reservationData = new ReservationData();
		reservationStatusHandler.handle(cart, reservationData);
		assertEquals("ACTIVE", reservationData.getBookingStatusCode());
		assertEquals("Active", reservationData.getBookingStatusName());
	}
}
