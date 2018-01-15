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
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Create PaymentInfo for Transport entries in a pay in advance scenario
 */
public class TransportationPayInAdvancePaymentInfoStrategy extends AbstractPaymentInfoStrategy
		implements EntryTypePaymentInfoCreationStrategy
{

	@Override
	public List<EntryTypePaymentInfo> create(final AbstractOrderModel abstractOrder)
	{
		final List<EntryTypePaymentInfo> paymentInfos = createPayInAdvancePaymentInfoForEntryType(abstractOrder,
				OrderEntryType.TRANSPORT);
		if (CollectionUtils.isEmpty(paymentInfos)
				|| abstractOrder.getEntries().stream().filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
						.allMatch(entry -> AmendStatus.SAME.equals(entry.getAmendStatus())))
		{
			return Collections.emptyList();
		}
		return paymentInfos;
	}

}

