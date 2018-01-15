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
import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
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
public class ReservationBasicHandlerTest
{

	@InjectMocks
	ReservationBasicHandler reservationBasicHandler;

	@Mock
	private AbstractOrderModel orderModel;

	private final PaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
	private Map<String, PaymentType> paymentInfoToPaymentTypeMap;

	@Before
	public void setUp()
	{
		given(orderModel.getCode()).willReturn("code1");

		paymentInfoToPaymentTypeMap = new HashMap<>();
		paymentInfoToPaymentTypeMap.put("CreditCardPaymentInfoModel", PaymentType.CREDIT_CARD);
		paymentInfoToPaymentTypeMap.put("InvoicePaymentInfoModel", PaymentType.COST_CENTER);
		reservationBasicHandler.setPaymentInfoToPaymentTypeMap(paymentInfoToPaymentTypeMap);
	}

	@Test
	public void testHandle()
	{
		given(orderModel.getPaymentInfo()).willReturn(paymentInfoModel);

		final ReservationData reservationData = new ReservationData();
		reservationBasicHandler.handle(orderModel, reservationData);
		Assert.assertEquals("code1", reservationData.getCode());
		Assert.assertEquals(PaymentType.CREDIT_CARD, reservationData.getPaymentType());
	}

	@Test
	public void testHandleForNull()
	{
		final ReservationData reservationData = new ReservationData();
		reservationBasicHandler.handle(orderModel, reservationData);
		Assert.assertEquals("code1", reservationData.getCode());
	}

}
