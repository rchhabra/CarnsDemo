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

package de.hybris.platform.travelfulfilmentprocess.test.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfulfilmentprocess.impl.DefaultTravelCheckOrderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultTravelCheckOrderService}
 */
@UnitTest
public class DefaultTravelCheckOrderServiceTest
{

	private DefaultTravelCheckOrderService travelCheckOrderService;

	@Mock
	private OrderModel order;

	@Mock
	private List<AbstractOrderEntryModel> orderEntries;

	@Mock
	private PaymentInfoModel paymentInfo;


	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelCheckOrderService = new DefaultTravelCheckOrderService();
	}

	/**
	 * Junit Test Method to check various attributes of the order.
	 */
	@Test
	public void testCheckTravelOrder()
	{
		when(order.getCalculated()).thenReturn(Boolean.TRUE);
		when(order.getEntries()).thenReturn(orderEntries);
		when(order.getPaymentInfo()).thenReturn(paymentInfo);
		when(order.getDeliveryAddress()).thenReturn(new AddressModel());

		final boolean orderCheck = travelCheckOrderService.check(order);
		Assert.assertTrue(orderCheck);
	}

	@Test
	public void testCheckTravelOrderWhenPaymentInfoIsNullOrEmpty()
	{
		when(order.getCalculated()).thenReturn(Boolean.TRUE);
		when(order.getEntries()).thenReturn(orderEntries);
		when(order.getPaymentInfo()).thenReturn(null);
		when(order.getDeliveryAddress()).thenReturn(new AddressModel());

		final boolean orderCheck = travelCheckOrderService.check(order);
		Assert.assertFalse(orderCheck);
	}

	@Test
	public void testCheckTravelOrderWhenOrderCalculationIsFalse()
	{
		when(order.getCalculated()).thenReturn(Boolean.FALSE);
		when(order.getEntries()).thenReturn(orderEntries);
		when(order.getPaymentInfo()).thenReturn(null);
		when(order.getDeliveryAddress()).thenReturn(new AddressModel());

		final boolean orderCheck = travelCheckOrderService.check(order);
		Assert.assertFalse(orderCheck);
	}

	@Test
	public void testCheckTravelOrderWhenOrderEntriesIsEmpty()
	{
		when(order.getCalculated()).thenReturn(Boolean.TRUE);
		when(order.getEntries()).thenReturn(new ArrayList<AbstractOrderEntryModel>());
		when(order.getPaymentInfo()).thenReturn(paymentInfo);
		when(order.getDeliveryAddress()).thenReturn(new AddressModel());

		final boolean orderCheck = travelCheckOrderService.check(order);
		Assert.assertFalse(orderCheck);
	}

	@Test
	public void testCheckTravelOrderWhenAddressisEmpty()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setCalculated(Boolean.TRUE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setDeliveryAddress(null);
		entry.setDeliveryPointOfService(null);
		orderModel.setEntries(Arrays.asList(new AbstractOrderEntryModel[]
		{ entry }));
		orderModel.setPaymentInfo(new PaymentInfoModel());
		orderModel.setDeliveryAddress(null);

		final boolean orderCheck = travelCheckOrderService.check(orderModel);
		Assert.assertFalse(orderCheck);

		final OrderModel orderModel2 = new OrderModel();
		orderModel2.setCalculated(Boolean.TRUE);
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setDeliveryAddress(new AddressModel());
		entry2.setDeliveryPointOfService(null);
		final AbstractOrderEntryModel entry3 = new AbstractOrderEntryModel();
		entry3.setDeliveryAddress(new AddressModel());
		entry3.setDeliveryPointOfService(new PointOfServiceModel());
		orderModel2.setEntries(Arrays.asList(entry2, entry3));
		orderModel2.setPaymentInfo(new PaymentInfoModel());
		orderModel2.setDeliveryAddress(null);

		Assert.assertTrue(travelCheckOrderService.check(orderModel2));
	}
}
