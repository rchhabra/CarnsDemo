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
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;
import de.hybris.platform.travelservices.strategies.payment.PaymentOptionCreationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Partially delayed payment option strategy.
 */
public class PartiallyDelayedPaymentOptionStrategy implements PaymentOptionCreationStrategy
{
	private List<EntryTypePaymentInfoCreationStrategy> entryTypePaymentInfoCreationStrategies;
	private BookingService bookingService;

	@Override
	public PaymentOptionInfo create(final AbstractOrderModel abstractOrder)
	{
		if (!isEligibleForDelayedPayment(abstractOrder))
		{
			return null;
		}
		final PaymentOptionInfo paymentOption = new PaymentOptionInfo();
		paymentOption.setEntryTypeInfos(new ArrayList<>());
		getEntryTypePaymentInfoCreationStrategies().forEach(strategy ->
		{
			final List<EntryTypePaymentInfo> entryTypeInfos = strategy.create(abstractOrder);
			if (CollectionUtils.isNotEmpty(entryTypeInfos))
			{
				paymentOption.getEntryTypeInfos().addAll(entryTypeInfos);
			}
		});
		return paymentOption;
	}

	/**
	 * Checks if the cart is eligible for a delayed payment (it must not contain only transport entries or at least one
	 * of the rate plans linked to the booked accommodations must provide at least one guarantee)
	 *
	 * @param abstractOrder
	 * @return
	 */
	protected boolean isEligibleForDelayedPayment(final AbstractOrderModel abstractOrder)
	{
		if (abstractOrder.getEntries().stream()
				.filter(entry -> Arrays.asList(AmendStatus.NEW, AmendStatus.CHANGED).contains(entry.getAmendStatus()))
				.allMatch(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())))
		{
			return false;
		}
		if (paidInAdvance(abstractOrder))
		{
			return false;
		}

		final List<AccommodationOrderEntryGroupModel> groups = getBookingService().getAccommodationOrderEntryGroups(abstractOrder);
		return CollectionUtils.isNotEmpty(groups) && groups.stream()
				.anyMatch(group -> CollectionUtils.isNotEmpty(group.getRatePlan().getGuarantee()));
	}

	private boolean paidInAdvance(final AbstractOrderModel abstractOrder)
	{
		return Objects.nonNull(abstractOrder.getOriginalOrder()) && BigDecimal
				.valueOf(getBookingService().getOrderTotalPriceByType(abstractOrder.getOriginalOrder(), OrderEntryType
						.ACCOMMODATION))
				.compareTo(getBookingService().getOrderTotalPaidForOrderEntryType(abstractOrder, OrderEntryType.ACCOMMODATION)) == 0;
	}

	/**
	 * @return entryTypePaymentInfoCreationStrategies
	 */
	protected List<EntryTypePaymentInfoCreationStrategy> getEntryTypePaymentInfoCreationStrategies()
	{
		return entryTypePaymentInfoCreationStrategies;
	}

	/**
	 * @param entryTypePaymentInfoCreationStrategies
	 * 		the entryTypePaymentInfoCreationStrategies to set
	 */
	@Required
	public void setEntryTypePaymentInfoCreationStrategies(
			final List<EntryTypePaymentInfoCreationStrategy> entryTypePaymentInfoCreationStrategies)
	{
		this.entryTypePaymentInfoCreationStrategies = entryTypePaymentInfoCreationStrategies;
	}

	/**
	 * @return bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 * 		the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
