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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.accommodation.strategies.OrderTotalByEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.PaymentTransactionEntryCreationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation handling the creation of transaction entries in case of refund
 */
public class DefaultRefundTransactionEntryCreationStrategy extends AbstractPaymentTransactionEntryCreationStrategy
		implements PaymentTransactionEntryCreationStrategy
{
	private TotalRefundCalculationStrategy totalRefundCalculationStrategy;
	private OrderTotalByEntryTypeCalculationStrategy orderTotalByEntryTypeCalculationStrategy;
	private TravelCartService travelCartService;
	private BookingService bookingService;

	@Override
	public void createTransactionEntries(final AbstractOrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		if (Objects.isNull(order) || CollectionUtils.isEmpty(entries))
		{
			return;
		}
		final OrderModel originalOrder = getBookingService().getOriginalOrder(order);
		final OrderEntryType entryType = entries.get(0).getType();
		final BigDecimal amountToRefund = (OrderStatus.CANCELLED.equals(order.getStatus()) || entries.stream()
				.filter(entry -> !(ProductType.FEE.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FeeProductModel))
				.allMatch(entry -> BooleanUtils.isNotTrue(entry.getActive())))
				? getTotalRefundCalculationStrategy().getTotalToRefund(originalOrder)
						: getBookingService().getOrderTotalPaidForOrderEntryType(originalOrder, entryType)
								.subtract(getCurrentTransportTotal(order,
										entries));

		final List<AbstractOrderEntryModel> increasedEntries = entries.stream().filter(entry -> hasIncreased(entry, originalOrder))
				.collect(Collectors.toList());
		final List<PaymentTransactionModel> involvedTransactions = order.getPaymentTransactions().stream()
				.filter(transaction -> CollectionUtils
						.isNotEmpty(CollectionUtils.intersection(transaction.getAbstractOrderEntries(), entries)))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(increasedEntries))
		{
			distributeOrphanEntries(increasedEntries, getAvailableTransactions(involvedTransactions), originalOrder);
		}

		createRefundTransactionEntries(involvedTransactions, amountToRefund);
	}

	protected BigDecimal getCurrentTransportTotal(final AbstractOrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		return getTravelCartService().getTransportTotalByEntries(order, entries);
	}

	protected Double calculateTotalExtras(final List<AbstractOrderEntryModel> entries)
	{
		final List<AbstractOrderEntryModel> globalExtrasEntries = entries.stream()
				.filter(entry -> !(ProductType.FEE.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FeeProductModel)
						&& !(ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
								|| entry.getProduct() instanceof FareProductModel)
						&& Objects.isNull(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber())
						&& Objects.isNull(entry.getTravelOrderEntryInfo().getTravelRoute()))
				.collect(Collectors.toList());

		BigDecimal totalExtrasPrice = BigDecimal.ZERO;

		for (final AbstractOrderEntryModel entry : globalExtrasEntries)
		{
			if (entry.getBundleNo() == 0)
			{
				double extrasPriceValue;
				if (entry.getQuantity() == 0)
				{
					extrasPriceValue = (CollectionUtils.isEmpty(entry.getDiscountValues())) ? entry.getTotalPrice()
							: entry.getBasePrice();
				}
				else
				{
					extrasPriceValue = entry.getBasePrice() * entry.getQuantity();
				}
				totalExtrasPrice = totalExtrasPrice.add(BigDecimal.valueOf(extrasPriceValue));
			}
		}
		return totalExtrasPrice.doubleValue();
	}

	protected void createRefundTransactionEntries(final List<PaymentTransactionModel> involvedTransactions,
			final BigDecimal amountToRefund)
	{
		BigDecimal refundAmount = amountToRefund;
		final List<PaymentTransactionModel> availableTransactions = getAvailableTransactions(involvedTransactions);
		if (availableTransactions.stream().count() == 1)
		{
			createRefundEntry(availableTransactions.get(0), refundAmount);
			return;
		}

		for (final PaymentTransactionModel transaction : availableTransactions)
		{
			if (refundAmount.equals(BigDecimal.ZERO))
			{
				break;
			}
			final BigDecimal availableFunds = BigDecimal.valueOf(getTransactionCalculationStrategy().getAvailableFunds(transaction));
			if (availableFunds.compareTo(refundAmount) >= 0)
			{
				createRefundEntry(transaction, refundAmount);
				break;
			}
			createRefundEntry(transaction, availableFunds);
			refundAmount = refundAmount.subtract(availableFunds);
		}
	}

	protected void distributeOrphanEntries(final List<AbstractOrderEntryModel> increasedEntries,
			final List<PaymentTransactionModel> availableTransactions, final AbstractOrderModel originalOrder)
	{
		for (final AbstractOrderEntryModel entry : increasedEntries)
		{
			Double amountDifference = getAmountDifference(entry, originalOrder);
			for (final PaymentTransactionModel transaction : availableTransactions)
			{
				final Double availableFunds = getTransactionCalculationStrategy().getAvailableFunds(transaction);
				if (availableFunds >= amountDifference)
				{
					linkNewEntriesToTransaction(transaction, Collections.singletonList(entry));
					break;
				}

				amountDifference -= availableFunds;
				linkNewEntriesToTransaction(transaction, Collections.singletonList(entry));
			}
		}
	}


	@Override
	void linkNewEntriesToTransaction(final PaymentTransactionModel involvedTransaction,
			final List<AbstractOrderEntryModel> entries)
	{
		final List<AbstractOrderEntryModel> linkedEntries = new ArrayList<>(involvedTransaction.getAbstractOrderEntries());
		linkedEntries.addAll(entries);
		involvedTransaction.setAbstractOrderEntries(linkedEntries);
		getModelService().save(involvedTransaction);
	}


	protected Double getAmountDifference(final AbstractOrderEntryModel entry, final AbstractOrderModel originalOrder)
	{
		return entry.getTotalPrice()
				- getOrderService().getEntryForNumber((OrderModel) originalOrder, entry.getEntryNumber()).getTotalPrice();
	}

	/**
	 *
	 * @return the totalRefundCalculationStrategy
	 */
	protected TotalRefundCalculationStrategy getTotalRefundCalculationStrategy()
	{
		return totalRefundCalculationStrategy;
	}

	/**
	 *
	 * @param totalRefundCalculationStrategy
	 *           the totalRefundCalculationStrategy to set
	 */
	@Required
	public void setTotalRefundCalculationStrategy(final TotalRefundCalculationStrategy totalRefundCalculationStrategy)
	{
		this.totalRefundCalculationStrategy = totalRefundCalculationStrategy;
	}

	/**
	 *
	 * @return the orderTotalByEntryTypeCalculationStrategy
	 */
	protected OrderTotalByEntryTypeCalculationStrategy getOrderTotalByEntryTypeCalculationStrategy()
	{
		return orderTotalByEntryTypeCalculationStrategy;
	}

	/**
	 *
	 * @param orderTotalByEntryTypeCalculationStrategy
	 *           the orderTotalByEntryTypeCalculationStrategy
	 */
	@Required
	public void setOrderTotalByEntryTypeCalculationStrategy(
			final OrderTotalByEntryTypeCalculationStrategy orderTotalByEntryTypeCalculationStrategy)
	{
		this.orderTotalByEntryTypeCalculationStrategy = orderTotalByEntryTypeCalculationStrategy;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

}
