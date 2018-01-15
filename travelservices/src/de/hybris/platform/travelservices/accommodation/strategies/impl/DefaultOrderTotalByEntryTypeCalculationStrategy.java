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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.accommodation.strategies.OrderTotalByEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link OrderTotalByEntryTypeCalculationStrategy}
 */
public class DefaultOrderTotalByEntryTypeCalculationStrategy extends TransactionCalculationStrategy
		implements OrderTotalByEntryTypeCalculationStrategy
{
	@Override
	public BigDecimal calculate(final OrderModel orderModel, final OrderEntryType orderEntryType)
	{
		final List<PaymentTransactionModel> paymentTransactions = getValidPaymentTransactionsForType(orderModel, orderEntryType);

		if (CollectionUtils.isEmpty(paymentTransactions))
		{
			return BigDecimal.ZERO;
		}

		BigDecimal orderTotal = BigDecimal.ZERO;
		final Map<PaymentTransactionType, List<PaymentTransactionEntryModel>> transactionEntriesByType = paymentTransactions
				.stream().flatMap(transaction -> transaction.getEntries().stream())
				.collect(Collectors.groupingBy(PaymentTransactionEntryModel::getType));

		final List<PaymentTransactionEntryModel> paidTransactionEntries = transactionEntriesByType
				.get(PaymentTransactionType.CAPTURE);
		orderTotal = orderTotal.add(paidTransactionEntries.stream().map(PaymentTransactionEntryModel::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add));

		final List<PaymentTransactionEntryModel> refundTransactionEntries = transactionEntriesByType
				.get(PaymentTransactionType.REFUND_STANDALONE);
		if (CollectionUtils.isNotEmpty(refundTransactionEntries))
		{
			orderTotal = orderTotal.subtract(refundTransactionEntries.stream().map(PaymentTransactionEntryModel::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add));
		}

		return orderTotal;
	}

}
