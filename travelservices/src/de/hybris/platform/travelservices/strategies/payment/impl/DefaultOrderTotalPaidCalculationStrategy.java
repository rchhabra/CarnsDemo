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

package de.hybris.platform.travelservices.strategies.payment.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.accommodation.strategies.impl.TransactionCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;



/**
 * Concrete implementation of {@link OrderTotalPaidCalculationStrategy} calculating the total paid for a given order
 */
public class DefaultOrderTotalPaidCalculationStrategy extends TransactionCalculationStrategy
		implements OrderTotalPaidCalculationStrategy
{
	@Override
	public BigDecimal calculate(final AbstractOrderModel abstractOrder)
	{
		BigDecimal totalPaid = BigDecimal.ZERO;
		final List<PaymentTransactionModel> paymentTransactions = getValidPaymentTransactions(
				abstractOrder.getPaymentTransactions());
		if (CollectionUtils.isEmpty(paymentTransactions))
		{
			return totalPaid;
		}

		final Map<PaymentTransactionType, List<PaymentTransactionEntryModel>> transactionsByType = paymentTransactions.stream()
				.flatMap(transaction -> transaction.getEntries().stream())
				.collect(Collectors.groupingBy(PaymentTransactionEntryModel::getType));

		totalPaid = totalPaid.add(transactionsByType.get(PaymentTransactionType.CAPTURE).stream()
				.map(PaymentTransactionEntryModel::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
		final List<PaymentTransactionEntryModel> entryToRefund = transactionsByType.get(PaymentTransactionType.REFUND_STANDALONE);
		if (CollectionUtils.isNotEmpty(entryToRefund))
		{
			totalPaid = totalPaid.subtract(
					entryToRefund.stream().map(PaymentTransactionEntryModel::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
		}
		return totalPaid;

	}
}
