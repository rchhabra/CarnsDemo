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
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.CancelPenaltiesCalculationService;
import de.hybris.platform.travelservices.services.RatePlanService;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.PaymentTransactionEntryCreationStrategy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link PaymentTransactionEntryCreationStrategy} handling the creation of payment
 * transaction entries within payment transactions linked to accommodation order entries
 */
public class AccommodationRefundTransactionEntryCreationStrategy extends AbstractPaymentTransactionEntryCreationStrategy
		implements PaymentTransactionEntryCreationStrategy
{

	private OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy;
	private RatePlanService ratePlanService;
	private CancelPenaltiesCalculationService cancelPenaltiesCalculationService;
	private BookingService bookingService;

	@Override
	public void createTransactionEntries(final AbstractOrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		final List<AbstractOrderEntryGroupModel> groups = entries.stream().map(AbstractOrderEntryModel::getEntryGroup).distinct()
				.collect(Collectors.toList());
		for (final AbstractOrderEntryGroupModel group : groups)
		{
			final List<PaymentTransactionModel> involvedTransactions = order.getPaymentTransactions().stream()
					.filter(transaction -> CollectionUtils.isNotEmpty(transaction.getAbstractOrderEntries()))
					.filter(transaction -> group
							.equals(transaction.getAbstractOrderEntries().stream().findFirst().get().getEntryGroup()))
					.collect(Collectors.toList());
			final List<AbstractOrderEntryModel> involvedEntries = order.getEntries().stream()
					.filter(entry -> group.equals(entry.getEntryGroup())).collect(Collectors.toList());

			BigDecimal refundAmountForAccommodationGroup = calculateAmountToRefund((AccommodationOrderEntryGroupModel) group, order,
					involvedEntries);

			if (refundAmountForAccommodationGroup.compareTo(BigDecimal.ZERO) > 0)
			{
				final List<PaymentTransactionModel> availableTransactions = getAvailableTransactions(involvedTransactions);
				if (availableTransactions.stream().count() == 1)
				{
					createRefundEntry(availableTransactions.get(0), refundAmountForAccommodationGroup);
					continue;
				}

				for (final PaymentTransactionModel transaction : availableTransactions)
				{
					final BigDecimal availableFunds = BigDecimal.valueOf(getAvailableFunds(transaction));
					if (availableFunds.compareTo(refundAmountForAccommodationGroup) >= 0)
					{
						createRefundEntry(transaction, refundAmountForAccommodationGroup);
						break;
					}

					createRefundEntry(transaction, availableFunds);
					refundAmountForAccommodationGroup = refundAmountForAccommodationGroup.subtract(availableFunds);
				}
			}
		}
	}


	protected BigDecimal calculateAmountToRefund(final AccommodationOrderEntryGroupModel involvedGroup,
			final AbstractOrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		final AbstractOrderModel originalOrder = getBookingService().getOriginalOrder(order);
		final BigDecimal totalPaidForGroup = getOrderTotalPaidForAccommodationGroupCalculationStrategy().calculate(originalOrder,
				involvedGroup);
		final BigDecimal orderEntriesTotal = getEntriesAmount(entries);
		if (OrderStatus.CANCELLED.equals(order.getStatus()))
		{
			final CancelPenaltyModel activeCancelPenalty = getCancelPenaltiesCalculationService().getActiveCancelPenalty(
					involvedGroup.getRatePlan().getCancelPenalty(), involvedGroup.getCheckInTime(), totalPaidForGroup);
			return totalPaidForGroup.subtract(Objects.nonNull(activeCancelPenalty)
					? getCancelPenaltiesCalculationService().getCancelPenaltyAmount(activeCancelPenalty, totalPaidForGroup)
					: BigDecimal.ZERO);
		}
		if (totalPaidForGroup.compareTo(orderEntriesTotal) > 0)
		{
			return totalPaidForGroup.subtract(orderEntriesTotal);
		}

		final BigDecimal originalOrderEntriesTotal = getEntriesAmount(
				originalOrder.getEntries().stream().filter(entry -> entries.stream().map(AbstractOrderEntryModel::getEntryNumber)
						.collect(Collectors.toList()).contains(entry.getEntryNumber())).collect(Collectors.toList()));
		final GuaranteeModel guaranteeToApply = getRatePlanService().getGuaranteeToApply(involvedGroup,
				getTimeService().getCurrentTime());

		return totalPaidForGroup.compareTo(originalOrderEntriesTotal) == 0 ? totalPaidForGroup.subtract(orderEntriesTotal)
				: totalPaidForGroup.subtract(getTotalToPayWithGuarantee(guaranteeToApply, involvedGroup, order));

	}

	@Override
	protected void linkNewEntriesToTransaction(final PaymentTransactionModel involvedTransactions,
			final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isNotEmpty(entries))
		{
			final Set<AbstractOrderEntryModel> linkedEntries = new HashSet<>(involvedTransactions.getAbstractOrderEntries());
			linkedEntries.addAll(entries);
			involvedTransactions.setAbstractOrderEntries(linkedEntries);
			getModelService().save(involvedTransactions);
			getModelService().refresh(involvedTransactions.getOrder());
		}
	}

	/**
	 *
	 * @return orderTotalPaidForAccommodationGroupCalculationStrategy
	 */
	protected OrderTotalPaidForEntryGroupCalculationStrategy getOrderTotalPaidForAccommodationGroupCalculationStrategy()
	{
		return orderTotalPaidForAccommodationGroupCalculationStrategy;
	}

	/**
	 *
	 * @param orderTotalPaidForAccommodationGroupCalculationStrategy
	 *           the orderTotalPaidForAccommodationGroupCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidForAccommodationGroupCalculationStrategy(
			final OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy)
	{
		this.orderTotalPaidForAccommodationGroupCalculationStrategy = orderTotalPaidForAccommodationGroupCalculationStrategy;
	}


	/**
	 *
	 * @return the ratePlanService
	 */
	protected RatePlanService getRatePlanService()
	{
		return ratePlanService;
	}


	/**
	 *
	 * @param ratePlanService
	 *           the ratePlanService to set
	 */
	@Required
	public void setRatePlanService(final RatePlanService ratePlanService)
	{
		this.ratePlanService = ratePlanService;
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
