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

package de.hybris.platform.travelservices.ordercancel.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.accommodation.strategies.impl.TransactionCalculationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.services.CancelPenaltiesCalculationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TotalRefundCalculationStrategy}
 */
public class DefaultAccommodationTotalRefundCalculationStrategy extends TransactionCalculationStrategy
		implements TotalRefundCalculationStrategy
{

	private CancelPenaltiesCalculationService cancelPenaltiesCalculationService;

	@Override
	public BigDecimal getTotalToRefund(final OrderModel orderModel)
	{
		final List<PaymentTransactionModel> validPaymentTransactions = getValidPaymentTransactionsForType(orderModel,
				OrderEntryType.ACCOMMODATION);

		BigDecimal totalToRefund = BigDecimal.ZERO;
		final Map<PaymentTransactionType, List<PaymentTransactionEntryModel>> transactionEntriesByType = validPaymentTransactions
				.stream().flatMap(transaction -> transaction.getEntries().stream())
				.collect(Collectors.groupingBy(PaymentTransactionEntryModel::getType));

		if (MapUtils.isEmpty(transactionEntriesByType))
		{
			return totalToRefund;
		}

		for (final PaymentTransactionEntryModel transactionEntry : transactionEntriesByType.get(PaymentTransactionType.CAPTURE))
		{
			final BigDecimal plannedAmount = transactionEntry.getAmount();

			//the assumption is that there is one transaction for each accommodation order entry group in the accommodation context
			final Optional<AbstractOrderEntryGroupModel> orderEntryGroupOptional = transactionEntry.getPaymentTransaction()
					.getAbstractOrderEntries().stream().map(AbstractOrderEntryModel::getEntryGroup)
					.filter(AccommodationOrderEntryGroupModel.class::isInstance).findFirst();

			BigDecimal totalPenalty = BigDecimal.ZERO;
			if (orderEntryGroupOptional.isPresent())
			{
				final AccommodationOrderEntryGroupModel orderEntryGroup = (AccommodationOrderEntryGroupModel) orderEntryGroupOptional
						.get();

				final CancelPenaltyModel cancelPenalty = getCancelPenaltiesCalculationService().getActiveCancelPenalty(
						orderEntryGroup.getRatePlan().getCancelPenalty(), orderEntryGroup.getStartingDate(), plannedAmount);

				if (cancelPenalty != null)
				{
					totalPenalty = totalPenalty
							.add(getCancelPenaltiesCalculationService().getCancelPenaltyAmount(cancelPenalty, plannedAmount));
				}
			}
			totalToRefund = totalToRefund.add(plannedAmount.subtract(totalPenalty));
		}
		final List<PaymentTransactionEntryModel> refundTransactionEntries = transactionEntriesByType
				.get(PaymentTransactionType.REFUND_STANDALONE);
		if (CollectionUtils.isNotEmpty(refundTransactionEntries))
		{
			totalToRefund = totalToRefund.subtract(refundTransactionEntries.stream().map(PaymentTransactionEntryModel::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add));
		}

		return totalToRefund.doubleValue() < 0d ? BigDecimal.ZERO : totalToRefund;
	}

	/**
	 *
	 * @return the defaultCancelPenaltiesCalculationService
	 */
	protected CancelPenaltiesCalculationService getCancelPenaltiesCalculationService()
	{
		return cancelPenaltiesCalculationService;
	}

	/**
	 *
	 * @param defaultCancelPenaltiesCalculationService
	 *           the defaultCancelPenaltiesCalculationService to set
	 */
	@Required
	public void setCancelPenaltiesCalculationService(final CancelPenaltiesCalculationService cancelPenaltiesCalculationService)
	{
		this.cancelPenaltiesCalculationService = cancelPenaltiesCalculationService;
	}

}
