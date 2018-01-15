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
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfulfilmentprocess.actions.order.TakePaymentAction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TakePaymentActionTest
{
	@InjectMocks
	private TakePaymentAction action;

	@Mock
	private OrderProcessModel orderProcess;

	private OrderModel order;
	@Mock
	private CreditCardPaymentInfoModel paymentInfoModel;
	@Mock
	private PaymentTransactionModel transaction;
	@Mock
	private PaymentTransactionEntryModel transactionEntry;
	@Mock
	private PaymentService paymentService;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		order = new OrderModel();
		order.setPaymentInfo(paymentInfoModel);
		order.setPaymentTransactions(Stream.of(transaction).collect(Collectors.toList()));
	}

	@Test
	public void testExecuteActionWithCreditCardPaymentInfo()
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(paymentService.capture(transaction)).thenReturn(transactionEntry);
		Mockito.when(transactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

		assertEquals(Transition.OK, action.executeAction(orderProcess));
		assertEquals(OrderStatus.PAYMENT_CAPTURED, order.getStatus());
	}

	@Test
	public void testExecuteActionWithDebitPaymentInfo()
	{
		order.setPaymentInfo(new DebitPaymentInfoModel());
		Mockito.when(orderProcess.getOrder()).thenReturn(order);

		assertEquals(Transition.OK, action.executeAction(orderProcess));
	}


	@Test
	public void testExecuteActionNOK()
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(paymentService.capture(transaction)).thenReturn(transactionEntry);
		Mockito.when(transactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

		assertEquals(Transition.NOK, action.executeAction(orderProcess));
		assertEquals(OrderStatus.PAYMENT_NOT_CAPTURED, order.getStatus());
	}

	@Test
	public void testExecuteActionOK()
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		Mockito.when(transaction.getEntries()).thenReturn(Stream.of(transactionEntry).collect(Collectors.toList()));
		Mockito.when(transactionEntry.getType()).thenReturn(PaymentTransactionType.CAPTURE);

		assertEquals(Transition.OK, action.executeAction(orderProcess));
	}
}
