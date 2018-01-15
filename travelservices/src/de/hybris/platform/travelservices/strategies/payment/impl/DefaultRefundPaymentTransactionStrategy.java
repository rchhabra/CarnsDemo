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
import de.hybris.platform.travelservices.strategies.payment.PaymentTransactionEntryCreationStrategy;
import de.hybris.platform.travelservices.strategies.payment.RefundPaymentTransactionStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default concrete implementation of {@link RefundPaymentTransactionStrategy} calling the related creation strategy on
 * each order entry belonging to the given type
 */
public class DefaultRefundPaymentTransactionStrategy implements RefundPaymentTransactionStrategy
{

	private PaymentTransactionEntryCreationStrategy defaultRefundTransactionEntryCreationStrategy;

	@Override
	public void handleRefund(final AbstractOrderModel abstractOrder, final OrderEntryType entryType,
			final List<AbstractOrderEntryModel> entries)
	{
		getDefaultRefundTransactionEntryCreationStrategy().createTransactionEntries(abstractOrder,
				abstractOrder.getEntries().stream().filter(entry -> entryType.equals(entry.getType())).collect(Collectors.toList()));
	}

	/**
	 *
	 * @return the defaultRefundTransactionEntryCreationStrategy
	 */
	protected PaymentTransactionEntryCreationStrategy getDefaultRefundTransactionEntryCreationStrategy()
	{
		return defaultRefundTransactionEntryCreationStrategy;
	}

	/**
	 *
	 * @param defaultRefundTransactionEntryCreationStrategy
	 *           the defaultRefundTransactionEntryCreationStrategy to set
	 */
	@Required
	public void setDefaultRefundTransactionEntryCreationStrategy(
			final PaymentTransactionEntryCreationStrategy defaultRefundTransactionEntryCreationStrategy)
	{
		this.defaultRefundTransactionEntryCreationStrategy = defaultRefundTransactionEntryCreationStrategy;
	}


}
