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

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GlobalTravelReservationBasicHandlerTest
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
	@Mock
	private Converter<UserModel, CustomerData> customerConverter;

	@InjectMocks
	private GlobalTravelReservationBasicHandler globalTravelReservationBasicHandler;

	@Test
	public void testPopulateWithVersion()
	{

		final OrderModel orderModel = new OrderModel()
		{

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
			public String getVersionID()
			{
				return "1.0";
			}

		};

		final BaseStoreModel baseStore = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		given(customerAccountService.getOrderForCode(orderModel.getCode(), baseStore)).willReturn(orderModel);
		final Locale locale = Locale.ENGLISH;
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(enumerationService.getEnumerationName(orderModel.getStatus(), locale)).willReturn("Active");
		given(customerConverter.convert(Matchers.any())).willReturn(new CustomerData());
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		globalTravelReservationBasicHandler.handle(orderModel, globalTravelReservationData);
		assertEquals("ACTIVE", globalTravelReservationData.getBookingStatusCode());
		assertEquals("Active", globalTravelReservationData.getBookingStatusName());

	}

	@Test
	public void testPopulateWithoutVersion()
	{

		final OrderModel orderModel = new OrderModel()
		{

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

		};

		final Locale locale = Locale.ENGLISH;
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(enumerationService.getEnumerationName(orderModel.getStatus(), locale)).willReturn("Active");
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		given(customerConverter.convert(Matchers.any())).willReturn(new CustomerData());
		globalTravelReservationBasicHandler.handle(orderModel, globalTravelReservationData);
		assertEquals("ACTIVE", globalTravelReservationData.getBookingStatusCode());
		assertEquals("Active", globalTravelReservationData.getBookingStatusName());
	}

}
