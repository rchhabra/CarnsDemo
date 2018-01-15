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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;



/**
 * Handles common method for pay in advance strategies
 */
public abstract class AbstractPaymentInfoStrategy
{

	protected Double calculateTaxes(final List<AbstractOrderEntryModel> entries)
	{
		final List<TaxValue> taxValues = entries.stream().flatMap(entry -> entry.getTaxValues().stream())
				.collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(taxValues) ? taxValues.stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum()
				: 0.0d;
	}

	protected Double getBookingTimeAmount(final List<AbstractOrderEntryModel> entries, final AbstractOrderModel abstractOrder)
	{
		return getEntriesAmount(
				entries.stream().filter(
						entry -> entry.getActive() && Arrays.asList(AmendStatus.NEW, AmendStatus.SAME).contains(entry.getAmendStatus()))
						.collect(Collectors.toList()));
	}

	protected Double getEntriesAmount(final List<AbstractOrderEntryModel> entries)
	{
		return CollectionUtils.isEmpty(entries) ? 0.0d
				: Double.sum(entries.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum(), calculateTaxes(entries));
	}

	protected Double getEntriesAmountByEntryType(final List<AbstractOrderEntryModel> entries, final OrderEntryType type)
	{
		return getEntriesAmount(entries.stream().filter(entry -> type.equals(entry.getType())).collect(Collectors.toList()));
	}

	protected List<EntryTypePaymentInfo> createPayInAdvancePaymentInfoForEntryType(final AbstractOrderModel abstractOrder,
			final OrderEntryType entryType)
	{
		final List<AbstractOrderEntryModel> entries = abstractOrder.getEntries().stream()
				.filter(entry -> entryType.equals(entry.getType())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(entries))
		{
			return Collections.emptyList();
		}
		final boolean isAmendedCart = Objects.nonNull(abstractOrder.getOriginalOrder());
		final EntryTypePaymentInfo paymentInfo = new EntryTypePaymentInfo();
		paymentInfo.setEntryType(entryType);
		paymentInfo.setEntries(entries);
		final Double paymentTotal = isAmendedCart
				? getAmendedTotalToPay(abstractOrder.getEntries(), abstractOrder.getOriginalOrder().getEntries(), entryType)
				: getBookingTimeAmount(entries, abstractOrder);
		paymentInfo.setBookingTimeAmount(getTotalWithDiscounts(abstractOrder, paymentTotal));
		paymentInfo.setPrePaymentRequested(0.0d);
		paymentInfo.setCheckInPayOff(0.0d);
		return Collections.singletonList(paymentInfo);
	}

	protected Double getTotalWithDiscounts(final AbstractOrderModel abstractOrder,
			final Double paymentTotal)
	{
		BigDecimal discount = BigDecimal.ZERO;
		if (Objects.nonNull(abstractOrder.getOriginalOrder()))
		{
			discount = discount.add(BigDecimal.valueOf(abstractOrder.getOriginalOrder().getTotalDiscounts()))
					.subtract(BigDecimal.valueOf(abstractOrder.getTotalDiscounts()));
			return paymentTotal - discount.doubleValue();
		}
		else
		{
			discount = discount.add(BigDecimal.valueOf(abstractOrder.getTotalDiscounts()));
			return paymentTotal - discount.doubleValue();
		}
	}

	protected Double getAmendedTotalToPay(final List<AbstractOrderEntryModel> amendedEntries,
			final List<AbstractOrderEntryModel> originalEntries, final OrderEntryType entryType)
	{
		BigDecimal amendedTotalToPay = BigDecimal.ZERO;
		amendedTotalToPay = amendedTotalToPay
				.add(BigDecimal.valueOf(getEntriesAmountByEntryType(amendedEntries, entryType)))
				.subtract(BigDecimal.valueOf(getEntriesAmountByEntryType(originalEntries, entryType)));
		return amendedTotalToPay.doubleValue();
	}

}
