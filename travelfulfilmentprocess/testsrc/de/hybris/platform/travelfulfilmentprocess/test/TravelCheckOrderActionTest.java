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

package de.hybris.platform.travelfulfilmentprocess.test;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.TravelCheckOrderService;
import de.hybris.platform.travelfulfilmentprocess.actions.order.TravelCheckOrderAction;
import de.hybris.platform.travelfulfilmentprocess.actions.order.TravelCheckOrderAction.Transition;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TravelCheckOrderActionTest
{
	@InjectMocks
	private TravelCheckOrderAction action;

	@Mock
	private OrderProcessModel orderProcess;

	private OrderModel order;

	@Mock
	private TravelCheckOrderService travelCheckOrderService;

	private PaymentTransactionModel paymentTransaction;

	private PaymentTransactionEntryModel authorizedPaymentTransactionEntry;

	private PaymentTransactionEntryModel capturedPaymentTransactionEntry;

	@Mock
	private ModelService modelService;

	@Before
	public void prepare()
	{
		order = new OrderModel();

		paymentTransaction = new PaymentTransactionModel();
		order.setPaymentTransactions(Stream.of(paymentTransaction).collect(Collectors.toList()));

		authorizedPaymentTransactionEntry = new PaymentTransactionEntryModel();

		capturedPaymentTransactionEntry = new PaymentTransactionEntryModel();

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteWithoutOrder() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(null);
		assertEquals(Transition.NOK.toString(), action.execute(orderProcess));
	}

	@Test
	public void testExecute() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(travelCheckOrderService.check(order)).thenReturn(Boolean.TRUE);

		authorizedPaymentTransactionEntry.setAmount(BigDecimal.TEN);
		authorizedPaymentTransactionEntry.setType(PaymentTransactionType.AUTHORIZATION);

		paymentTransaction.setEntries(Stream.of(authorizedPaymentTransactionEntry).collect(Collectors.toList()));

		assertEquals(Transition.OK.toString(), action.execute(orderProcess));
		assertEquals(OrderStatus.CHECKED_VALID, order.getStatus());
	}

	@Test
	public void testExecuteWithCapturedTransactions() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(travelCheckOrderService.check(order)).thenReturn(Boolean.TRUE);

		authorizedPaymentTransactionEntry.setAmount(BigDecimal.TEN);
		authorizedPaymentTransactionEntry.setType(PaymentTransactionType.AUTHORIZATION);

		capturedPaymentTransactionEntry.setAmount(BigDecimal.TEN);
		capturedPaymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);

		paymentTransaction.setEntries(
				Stream.of(authorizedPaymentTransactionEntry, capturedPaymentTransactionEntry).collect(Collectors.toList()));

		assertEquals(Transition.NO_PAYMENT.toString(), action.execute(orderProcess));
		assertEquals(OrderStatus.CHECKED_VALID, order.getStatus());
	}

	@Test
	public void testExecuteWithInvalidOrder() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(travelCheckOrderService.check(order)).thenReturn(Boolean.FALSE);

		assertEquals(Transition.NOK.toString(), action.execute(orderProcess));
		assertEquals(OrderStatus.CHECKED_INVALID, order.getStatus());
	}

	@Test
	public void testGetTransitions()
	{
		final Set<String> transitionSet = new HashSet<>();
		transitionSet.add(Transition.NOK.toString());
		transitionSet.add(Transition.OK.toString());
		transitionSet.add(Transition.NO_PAYMENT.toString());
		assertEquals(transitionSet.size(), action.getTransitions().size());
	}
}
