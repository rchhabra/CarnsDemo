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

package de.hybris.platform.travelservices.accommodation.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransactionCalculationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransactionCalculationStrategyTest
{
	@InjectMocks
	TransactionCalculationStrategy transactionCalculationStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;
	private List<PaymentTransactionModel> paymentTransactions;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(100d);
		paymentTransactions = new ArrayList<>();
		paymentTransactions.add(
				createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100, Collections.emptyList()));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.REFUND_STANDALONE, 50,
				Collections.emptyList()));
		paymentTransactions.add(
				createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200, Collections.emptyList()));
	}

	@Test
	public void testGetValidPaymentTransactions()
	{

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);
		Assert.assertNotNull(transactionCalculationStrategy.getValidPaymentTransactions(paymentTransactions));
	}

	@Test
	public void testGetValidPaymentTransactionsForType()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);

		Assert.assertNotNull(
				transactionCalculationStrategy.getValidPaymentTransactionsForType(orderModel, OrderEntryType.ACCOMMODATION));
		Assert.assertNotNull(
				transactionCalculationStrategy.getValidPaymentTransactionsForType(orderModel, OrderEntryType.TRANSPORT));

	}

	@Test
	public void testGetPaymentAuthourizationAmount()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(100d);
		Assert.assertEquals(100, transactionCalculationStrategy.getPaymentAuthourizationAmount().intValue());
	}

	@Test
	public void testGetAvailableFunds()
	{
		final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(OrderEntryType.ACCOMMODATION,
				PaymentTransactionType.CAPTURE, 100, Collections.emptyList());
		Assert.assertEquals(0.0d, transactionCalculationStrategy.getAvailableFunds(paymentTransactionModel), 2);

		final PaymentTransactionModel paymentTransactionModel2 = createPaymentTransaction(OrderEntryType.ACCOMMODATION,
				PaymentTransactionType.CAPTURE, 200, Collections.singletonList(new TaxValue("TEST_TAX_CODE", 2d, true, 2d, "GBP")));
		final List<PaymentTransactionEntryModel> entries = new ArrayList<>();
		entries.add(createPaymentTransactionEntryModel(PaymentTransactionType.CAPTURE, 200));
		entries.add(createPaymentTransactionEntryModel(PaymentTransactionType.REFUND_STANDALONE, 98));
		paymentTransactionModel2.setEntries(entries);
		Assert.assertEquals(0.0d, transactionCalculationStrategy.getAvailableFunds(paymentTransactionModel2), 2);
	}

	private PaymentTransactionModel createPaymentTransaction(final OrderEntryType orderType,
			final PaymentTransactionType paymentTransactionType, final double amount, final List<TaxValue> taxValues)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(orderType, 100d, taxValues));
		final PaymentTransactionModel paymentModel = new PaymentTransactionModel();
		paymentModel.setAbstractOrderEntries(abstractOrderEntries);

		final List<PaymentTransactionEntryModel> entries = new ArrayList<>();
		entries.add(createPaymentTransactionEntryModel(paymentTransactionType, amount));
		paymentModel.setEntries(entries);
		paymentModel.setPlannedAmount(BigDecimal.valueOf(amount));
		return paymentModel;
	}

	private AbstractOrderEntryModel createOrderEntry(final OrderEntryType orderType, final double totalPrice,
			final List<TaxValue> taxValues)
	{
		final AbstractOrderEntryModel order = new AbstractOrderEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return taxValues;
			}
		};
		order.setType(orderType);
		order.setTotalPrice(totalPrice);
		return order;
	}

	private PaymentTransactionEntryModel createPaymentTransactionEntryModel(final PaymentTransactionType paymentTransactionType,
			final double amount)
	{
		final PaymentTransactionEntryModel entryModel = new PaymentTransactionEntryModel();
		entryModel.setType(paymentTransactionType);
		entryModel.setAmount(BigDecimal.valueOf(amount));
		return entryModel;
	}
}
