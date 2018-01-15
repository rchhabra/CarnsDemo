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
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;
import de.hybris.platform.travelservices.strategies.payment.PaymentOptionCreationStrategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy returning a complex object representing a payment option, built through a list of scenario specific strategy
 */
public class FullInAdvancePaymentOptionStrategy implements PaymentOptionCreationStrategy
{
	private List<EntryTypePaymentInfoCreationStrategy> entryTypePaymentInfoCreationStrategies;

	@Override
	public PaymentOptionInfo create(final AbstractOrderModel abstractOrder)
	{
		final PaymentOptionInfo paymentOption = new PaymentOptionInfo();
		paymentOption.setEntryTypeInfos(new ArrayList<>());
		getEntryTypePaymentInfoCreationStrategies().forEach(strategy -> {
			final List<EntryTypePaymentInfo> entryTypeInfos = strategy.create(abstractOrder);
			if (CollectionUtils.isNotEmpty(entryTypeInfos))
			{
				paymentOption.getEntryTypeInfos().addAll(entryTypeInfos);
			}
		});
		return paymentOption;
	}

	/**
	 *
	 * @return entryTypePaymentInfoCreationStrategies
	 */
	protected List<EntryTypePaymentInfoCreationStrategy> getEntryTypePaymentInfoCreationStrategies()
	{
		return entryTypePaymentInfoCreationStrategies;
	}

	/**
	 *
	 * @param entryTypePaymentInfoCreationStrategies
	 *           the entryTypePaymentInfoCreationStrategies to set
	 */
	@Required
	public void setEntryTypePaymentInfoCreationStrategies(
			final List<EntryTypePaymentInfoCreationStrategy> entryTypePaymentInfoCreationStrategies)
	{
		this.entryTypePaymentInfoCreationStrategies = entryTypePaymentInfoCreationStrategies;
	}

}
