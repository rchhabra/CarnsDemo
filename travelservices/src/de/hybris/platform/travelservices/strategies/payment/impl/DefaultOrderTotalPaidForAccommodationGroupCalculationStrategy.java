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
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete strategy calculating the total paid given an Order Entry Group. Since we are dealing with accommodation
 * order entry groups, the valid payment transactions associated with the order are filtered by OrderEntryType, then the
 * transactions associated with the given group are identified by Room Stay Reference Number. This operation is safe if
 * no cancel accommodation functionality is in place, otherwise further logic is needed to normalize room stay ref
 * numbers and update the entries list against payment transactions when an accommodation booking is cancelled.
 */
public class DefaultOrderTotalPaidForAccommodationGroupCalculationStrategy extends TransactionCalculationStrategy
		implements OrderTotalPaidForEntryGroupCalculationStrategy
{
	@Override
	public BigDecimal calculate(final AbstractOrderModel abstractOrder, final AbstractOrderEntryGroupModel entryGroup)
	{
		final AccommodationOrderEntryGroupModel accommodationEntryGroup = (AccommodationOrderEntryGroupModel) entryGroup;
		BigDecimal totalPaid = BigDecimal.ZERO;
		final List<PaymentTransactionModel> paymentTransactions = getValidPaymentTransactions(
				abstractOrder.getPaymentTransactions())
						.stream()
						.filter(transaction -> CollectionUtils.isNotEmpty(transaction.getAbstractOrderEntries())
								&& transaction.getAbstractOrderEntries().stream()
										.allMatch(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType())))
						.filter(transaction -> accommodationEntryGroup.getRoomStayRefNumber()
								.equals(((AccommodationOrderEntryGroupModel) transaction.getAbstractOrderEntries().stream().findFirst()
										.get().getEntryGroup()).getRoomStayRefNumber()))
						.collect(Collectors.toList());


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
			totalPaid = totalPaid.add(
					entryToRefund.stream().map(PaymentTransactionEntryModel::getAmount).reduce(BigDecimal.ZERO, BigDecimal::subtract));
		}
		return totalPaid;
	}

}
