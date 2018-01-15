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
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationBasicHandlerTest
{
	@InjectMocks
	AccommodationReservationBasicHandler accommodationReservationBasicHandler;

	@Mock
	private AbstractOrderModel orderModel;

	private final PaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
	@Mock
	private CurrencyModel currencyModel;
	private Map<String, PaymentType> paymentInfoToPaymentTypeMap;

	@Before
	public void setUp()
	{
		given(orderModel.getCode()).willReturn("code1");
		paymentInfoToPaymentTypeMap = new HashMap<>();
		paymentInfoToPaymentTypeMap.put("CreditCardPaymentInfoModel", PaymentType.CREDIT_CARD);
		paymentInfoToPaymentTypeMap.put("InvoicePaymentInfoModel", PaymentType.COST_CENTER);
		accommodationReservationBasicHandler.setPaymentInfoToPaymentTypeMap(paymentInfoToPaymentTypeMap);
	}

	@Test
	public void testHandle()
	{
		given(orderModel.getPaymentInfo()).willReturn(paymentInfoModel);
		given(orderModel.getCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn("TEST_ISO_CODE");
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationBasicHandler.handle(orderModel, accommodationReservationData);
		Assert.assertEquals("code1", accommodationReservationData.getCode());
		Assert.assertEquals(PaymentType.CREDIT_CARD, accommodationReservationData.getPaymentType());
		Assert.assertEquals("TEST_ISO_CODE", accommodationReservationData.getCurrencyIso());
	}

	@Test
	public void testHandleForNull()
	{
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationBasicHandler.handle(orderModel, accommodationReservationData);
		Assert.assertEquals("code1", accommodationReservationData.getCode());
	}
}
