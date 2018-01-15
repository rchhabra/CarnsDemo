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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultOrderTotalByEntryTypeCalculationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderTotalByEntryTypeCalculationStrategyTest
{
	@InjectMocks
	DefaultOrderTotalByEntryTypeCalculationStrategy defaultOrderTotalByEntryTypeCalculationStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testCalculate()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0d);
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100));
		paymentTransactions
				.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.REFUND_STANDALONE, 50));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 0d));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);
		Assert.assertEquals(200,
				defaultOrderTotalByEntryTypeCalculationStrategy.calculate(orderModel, OrderEntryType.TRANSPORT).intValue());
		Assert.assertEquals(50,
				defaultOrderTotalByEntryTypeCalculationStrategy.calculate(orderModel, OrderEntryType.ACCOMMODATION).intValue());
	}

	@Test
	public void testCalculateForEmptyValidTransactions()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0d);
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 0d));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);
		Assert.assertEquals(0,
				defaultOrderTotalByEntryTypeCalculationStrategy.calculate(orderModel, OrderEntryType.TRANSPORT).intValue());
	}

	private PaymentTransactionModel createPaymentTransaction(final OrderEntryType orderType,
			final PaymentTransactionType paymentTransactionType, final double amount)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(orderType));
		final PaymentTransactionModel paymentModel = new PaymentTransactionModel();
		paymentModel.setAbstractOrderEntries(abstractOrderEntries);

		final List<PaymentTransactionEntryModel> entries = new ArrayList<>();
		final PaymentTransactionEntryModel paymentTransactionEntryModel = createPaymentTransactionEntryModel(paymentTransactionType,
				amount);
		paymentTransactionEntryModel.setPaymentTransaction(paymentModel);
		entries.add(paymentTransactionEntryModel);
		paymentModel.setEntries(entries);
		paymentModel.setPlannedAmount(BigDecimal.valueOf(amount));
		return paymentModel;
	}

	private AbstractOrderEntryModel createOrderEntry(final OrderEntryType orderType)
	{
		final AbstractOrderEntryModel order = new AbstractOrderEntryModel();
		order.setType(orderType);
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
