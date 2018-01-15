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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfulfilmentprocess.actions.order.CancelWholeOrderAuthorizationAction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CancelWholeOrderAuthorizationActionTest
{
	@InjectMocks
	private CancelWholeOrderAuthorizationAction action;

	@Mock
	private OrderProcessModel orderProcess;

	@Mock
	private PaymentService paymentService;

	private OrderModel order;

	@Mock
	private PaymentTransactionModel transaciton;

	@Mock
	private PaymentTransactionEntryModel transactionEntry;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionAcceptedStatus()
	{
		order = new OrderModel();
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setPaymentTransactions(Stream.of(transaciton).collect(Collectors.toList()));
		Mockito.when(transaciton.getEntries()).thenReturn(Stream.of(transactionEntry).collect(Collectors.toList()));
		Mockito.when(paymentService.cancel(transactionEntry)).thenReturn(transactionEntry);
		Mockito.when(transactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
		Mockito.doNothing().when(modelService).save(order);
		action.executeAction(orderProcess);

		Assert.assertEquals(order.getStatus(), OrderStatus.CANCELLED);
	}

	@Test
	public void testExecuteActionRejectedStatus()
	{
		order = new OrderModel();
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setPaymentTransactions(Stream.of(transaciton).collect(Collectors.toList()));
		Mockito.when(transaciton.getEntries()).thenReturn(Stream.of(transactionEntry).collect(Collectors.toList()));
		Mockito.when(paymentService.cancel(transactionEntry)).thenReturn(transactionEntry);
		Mockito.when(transactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());
		Mockito.doNothing().when(modelService).save(order);
		action.executeAction(orderProcess);

		Assert.assertEquals(order.getStatus(), OrderStatus.PROCESSING_ERROR);
	}

	@Test
	public void testExecuteActionWithMultiplePaymentTransactions()
	{
		order = new OrderModel();
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setPaymentTransactions(Stream.of(transaciton, new PaymentTransactionModel()).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).save(order);
		action.executeAction(orderProcess);

		Assert.assertEquals(order.getStatus(), OrderStatus.PROCESSING_ERROR);
	}

	@Test
	public void testExecuteActionWithMultiplePaymentTransactionEntries()
	{
		order = new OrderModel();
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setPaymentTransactions(Stream.of(transaciton).collect(Collectors.toList()));
		Mockito.when(transaciton.getEntries())
				.thenReturn(Stream.of(transactionEntry, new PaymentTransactionEntryModel()).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).save(order);
		action.executeAction(orderProcess);

		Assert.assertEquals(order.getStatus(), OrderStatus.PROCESSING_ERROR);
	}
}
