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
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.strategies.payment.PaymentTransactionEntryCreationStrategy;
import de.hybris.platform.travelservices.strategies.payment.RefundPaymentTransactionStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete implementation of {@link RefundPaymentTransactionStrategy} to handle refund in an accommodation scenario
 * Refund can be triggered both on a given subset of entries (that is on a group) or on the whole order split by
 * accommodation entry group if no subset is passed
 */
public class AccommodationRefundPaymentTransactionStrategy implements RefundPaymentTransactionStrategy
{
	private PaymentTransactionEntryCreationStrategy accommodationRefundTransactionEntryCreationStrategy;

	@Override
	public void handleRefund(final AbstractOrderModel abstractOrder, final OrderEntryType entryType,
			final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isNotEmpty(entries))
		{
			getAccommodationRefundTransactionEntryCreationStrategy().createTransactionEntries(abstractOrder, entries);
			return;
		}
		final Map<AbstractOrderEntryGroupModel, List<AbstractOrderEntryModel>> entriesByGroup = abstractOrder.getEntries().stream()
				.collect(Collectors.groupingBy(AbstractOrderEntryModel::getEntryGroup));
		entriesByGroup.entrySet().forEach(mapEntry -> getAccommodationRefundTransactionEntryCreationStrategy()
				.createTransactionEntries(abstractOrder, mapEntry.getValue()));
	}

	/**
	 *
	 * @return accommodationRefundTransactionEntryCreationStrategy
	 */
	protected PaymentTransactionEntryCreationStrategy getAccommodationRefundTransactionEntryCreationStrategy()
	{
		return accommodationRefundTransactionEntryCreationStrategy;
	}

	/**
	 *
	 * @param accommodationRefundTransactionEntryCreationStrategy
	 *           the accommodationRefundTransactionEntryCreationStrategy to set
	 */
	public void setAccommodationRefundTransactionEntryCreationStrategy(
			final PaymentTransactionEntryCreationStrategy accommodationRefundTransactionEntryCreationStrategy)
	{
		this.accommodationRefundTransactionEntryCreationStrategy = accommodationRefundTransactionEntryCreationStrategy;
	}


}
