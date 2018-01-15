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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/*
 * Strategy for payment calculations
 */
public class TransactionCalculationStrategy
{

	private ConfigurationService configurationService;

	/**
	 * It returns all the valid payment transaction for the list of paymentTransactions. This method checks if there is
	 * at least one paymentTransactionEntry with paymentTransactionType CAPTURE. This method can be an extension point,
	 * because in a real case scenario, it may be necessary to perform more checks on the paymentTransactionEntry type,
	 * to verify that there are no entries with type CANCEL.
	 *
	 * @param paymentTransactions
	 *           as the list of paymentTransaction to be filtered
	 * @return the list of valid payment transactions
	 */
	public List<PaymentTransactionModel> getValidPaymentTransactions(final List<PaymentTransactionModel> paymentTransactions)
	{
		return paymentTransactions.stream()
				.filter(paymentTransaction -> paymentTransaction.getEntries().stream()
						.anyMatch(entry -> Arrays.asList(PaymentTransactionType.CAPTURE, PaymentTransactionType.REFUND_STANDALONE)
								.contains(entry.getType()))
						&& paymentTransaction.getPlannedAmount().compareTo(BigDecimal.valueOf(getPaymentAuthourizationAmount())) != 0)
				.collect(Collectors.toList());
	}

	/**
	 * This method returns the valid payment transaction for a certain orderEntryType. The assumption is that all the
	 * entries linked to a transaction will be of a certain type
	 *
	 * @param orderModel
	 * @param orderEntryType
	 * @return
	 */
	public List<PaymentTransactionModel> getValidPaymentTransactionsForType(final AbstractOrderModel orderModel,
			final OrderEntryType orderEntryType)
	{
		final List<PaymentTransactionModel> validPaymentTransactions = getValidPaymentTransactions(
				orderModel.getPaymentTransactions());
		final List<PaymentTransactionModel> paymentTransactions = validPaymentTransactions.stream()
				.filter(transaction -> CollectionUtils.isNotEmpty(transaction.getAbstractOrderEntries())
						&& transaction.getAbstractOrderEntries().stream()
								.allMatch(orderEntry -> Objects.equals(orderEntryType, orderEntry.getType())))
				.collect(Collectors.toList());

		if (OrderEntryType.TRANSPORT.equals(orderEntryType))
		{
			paymentTransactions.addAll(validPaymentTransactions.stream()
					.filter(transaction -> CollectionUtils.isEmpty(transaction.getAbstractOrderEntries()))
					.collect(Collectors.toList()));
		}

		return paymentTransactions;
	}

	/**
	 * This method returns the difference between the captured total and the refunded total, if present. Used to
	 * re-distribute entries among available transactions in case of refunding.
	 *
	 * @param transaction
	 * @return
	 */
	public Double getAvailableFunds(final PaymentTransactionModel transaction)
	{
		BigDecimal availableAmount = BigDecimal.ZERO;
		final Map<PaymentTransactionType, List<PaymentTransactionEntryModel>> transactionsByType = transaction.getEntries().stream()
				.collect(Collectors.groupingBy(PaymentTransactionEntryModel::getType));
		if (CollectionUtils.isNotEmpty(transactionsByType.get(PaymentTransactionType.CAPTURE)))
		{
			availableAmount = transactionsByType.get(PaymentTransactionType.CAPTURE).stream()
					.map(PaymentTransactionEntryModel::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			availableAmount = availableAmount.subtract(getEntriesAmount(transaction.getAbstractOrderEntries()));
			final List<PaymentTransactionEntryModel> entryToRefund = transactionsByType
					.get(PaymentTransactionType.REFUND_STANDALONE);
			if (CollectionUtils.isNotEmpty(entryToRefund))
			{
				availableAmount = availableAmount.subtract(
						entryToRefund.stream().map(PaymentTransactionEntryModel::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
			}
		}
		return availableAmount.doubleValue();
	}

	protected BigDecimal getEntriesAmount(final Collection<AbstractOrderEntryModel> entries)
	{
		final List<TaxValue> taxValues = entries.stream().flatMap(entry -> entry.getTaxValues().stream())
				.collect(Collectors.toList());
		final Double taxAmount = CollectionUtils.isNotEmpty(taxValues)
				? taxValues.stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum() : 0.0d;

		return BigDecimal.valueOf(Double.sum(entries.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum(), taxAmount))
				.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * This method returns the minimum amount added to the transaction in order to create the payment transactions in
	 * case the amount is to be paid at check-in Time
	 */
	public Double getPaymentAuthourizationAmount()
	{
		return getConfigurationService().getConfiguration().getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT);
	}

	/**
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
