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

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.accommodation.strategies.impl.TransactionCalculationStrategy;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class handling basic operations involved in the creation of payment transaction entries.
 */
public abstract class AbstractPaymentTransactionEntryCreationStrategy
{
	private TimeService timeService;
	private ModelService modelService;
	private PaymentService paymentService;
	private OrderService orderService;
	private TransactionCalculationStrategy transactionCalculationStrategy;

	protected void createRefundEntry(final PaymentTransactionModel transaction, final BigDecimal amountToRefund)
	{
		final PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);
		entry.setType(PaymentTransactionType.REFUND_STANDALONE);
		entry.setTime(getTimeService().getCurrentTime());
		entry.setPaymentTransaction(transaction);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCode(getPaymentService().getNewPaymentTransactionEntryCode(transaction, PaymentTransactionType.REFUND_STANDALONE));
		entry.setAmount(amountToRefund);
		entry.setRequestId(transaction.getRequestId());
		entry.setRequestToken(transaction.getRequestToken());

		getModelService().saveAll(entry);
		getModelService().refresh(transaction);
	}

	protected BigDecimal getTotalToPayWithGuarantee(final GuaranteeModel guaranteeToApply,
			final AccommodationOrderEntryGroupModel group, final AbstractOrderModel abstractOrder)
	{
		return Objects.nonNull(guaranteeToApply.getFixedAmount()) ? BigDecimal.valueOf(guaranteeToApply.getFixedAmount())
				: getGuaranteeAmount(guaranteeToApply.getPercentageAmount(), getRoomRatesTotalPrice(group, abstractOrder));
	}

	protected BigDecimal getGuaranteeAmount(final Double amountPercent, final BigDecimal totalPrice)
	{
		return Objects.nonNull(amountPercent) ? BigDecimal.valueOf(totalPrice.doubleValue() * amountPercent / 100)
				: BigDecimal.ZERO;
	}

	private BigDecimal getRoomRatesTotalPrice(final AccommodationOrderEntryGroupModel group,
			final AbstractOrderModel abstractOrder)
	{
		return getBookingTimeAmount(group.getEntries().stream().filter(entry -> entry.getProduct() instanceof RoomRateProductModel)
				.collect(Collectors.toList()), abstractOrder);
	}

	protected Double calculateTaxes(final List<AbstractOrderEntryModel> entries)
	{
		final List<TaxValue> taxValues = entries.stream().flatMap(entry -> entry.getTaxValues().stream())
				.collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(taxValues) ? taxValues.stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum()
				: 0.0d;
	}

	protected BigDecimal getBookingTimeAmount(final List<AbstractOrderEntryModel> entries, final AbstractOrderModel abstractOrder)
	{
		return getEntriesAmount(entries.stream()
				.filter(
						entry -> entry.getActive() && Arrays.asList(AmendStatus.NEW, AmendStatus.SAME).contains(entry.getAmendStatus()))
				.collect(Collectors.toList()));
	}

	protected BigDecimal getEntriesAmount(final List<AbstractOrderEntryModel> entries)
	{
		final List<AbstractOrderEntryModel> validEntriesToCalculate = entries.stream()
				.filter(entry -> !OrderEntryStatus.DEAD.equals(entry.getQuantityStatus())).filter(AbstractOrderEntryModel::getActive)
				.collect(Collectors.toList());
		return CollectionUtils.isEmpty(validEntriesToCalculate) ? BigDecimal.ZERO
				: BigDecimal
						.valueOf(Double.sum(validEntriesToCalculate.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum(),
								calculateTaxes(validEntriesToCalculate)));
	}

	/**
	 * @deprecated Deprecated since version 4.0. use {@link BookingService#getOriginalOrder(AbstractOrderModel)}
	 *
	 * @param abstractOrder
	 * @return
	 */
	@Deprecated
	protected OrderModel getOriginalOrder(final AbstractOrderModel abstractOrder)
	{
		if (Objects.nonNull(abstractOrder.getOriginalOrder()))
		{
			return abstractOrder.getOriginalOrder();
		}
		final Optional<OrderHistoryEntryModel> lastHistoryEntry = ((OrderModel) abstractOrder).getHistoryEntries().stream()
				.filter(historyEntry -> historyEntry.getPreviousOrderVersion() != null)
				.sorted((historyEntry1, historyEntry2) -> historyEntry2.getCreationtime().compareTo(historyEntry1.getCreationtime()))
				.findFirst();
		return lastHistoryEntry.isPresent() ? lastHistoryEntry.get().getPreviousOrderVersion() : null;
	}

	protected boolean hasIncreased(final AbstractOrderEntryModel entry, final AbstractOrderModel originalOrder)
	{
		return AmendStatus.NEW.equals(entry.getAmendStatus()) || entry.getQuantity() > getOrderService()
				.getEntryForNumber((OrderModel) originalOrder, entry.getEntryNumber()).getQuantity();
	}

	protected Double getAvailableFunds(final PaymentTransactionModel transaction)
	{
		return getTransactionCalculationStrategy().getAvailableFunds(transaction);
	}

	protected List<PaymentTransactionModel> getAvailableTransactions(final List<PaymentTransactionModel> involvedTransactions)
	{
		final List<PaymentTransactionModel> availableTransactions = involvedTransactions.stream()
				.filter(transaction -> getAvailableFunds(transaction) > 0).collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(availableTransactions) ? availableTransactions
				: Collections.singletonList(involvedTransactions.get(0));
	}


	abstract void linkNewEntriesToTransaction(final PaymentTransactionModel involvedTransaction,
			final List<AbstractOrderEntryModel> entries);


	/**
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 *
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 *
	 * @return thepaymentService
	 */
	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	/**
	 *
	 * @param paymentService
	 *           the paymentService to set
	 */
	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	/**
	 *
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 *
	 * @return the orderService
	 */
	protected OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 *
	 * @param orderService
	 *           the orderService to set
	 */
	@Required
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 *
	 * @return the transactionCalculationStrategy
	 */
	protected TransactionCalculationStrategy getTransactionCalculationStrategy()
	{
		return transactionCalculationStrategy;
	}

	/**
	 *
	 * @param transactionCalculationStrategy
	 *           the transactionCalculationStrategy to set
	 */
	@Required
	public void setTransactionCalculationStrategy(final TransactionCalculationStrategy transactionCalculationStrategy)
	{
		this.transactionCalculationStrategy = transactionCalculationStrategy;
	}

}
