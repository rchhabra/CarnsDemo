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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationSalesApplicationHandlerTest
{
	@InjectMocks
	ReservationSalesApplicationHandler reservationSalesApplicationHandler;

	@Test
	public void testHandle()
	{
		final ReservationData reservationData = new ReservationData();

		reservationSalesApplicationHandler.handle(new CartModel(), reservationData);
		Assert.assertNull(reservationData.getSalesApplication());

		reservationSalesApplicationHandler.handle(new OrderModel(), reservationData);
		Assert.assertNull(reservationData.getSalesApplication());

		final OrderModel orderModel = new OrderModel();
		orderModel.setSalesApplication(SalesApplication.WEB);

		reservationSalesApplicationHandler.handle(orderModel, reservationData);
		Assert.assertEquals(reservationData.getSalesApplication(), SalesApplication.WEB.getCode());

	}
}
