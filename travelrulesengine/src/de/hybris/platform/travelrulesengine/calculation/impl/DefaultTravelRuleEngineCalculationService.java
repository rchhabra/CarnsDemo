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
*/

package de.hybris.platform.travelrulesengine.calculation.impl;

import de.hybris.platform.ruleengineservices.calculation.impl.DefaultRuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.calculation.TravelRuleEngineCalculationService;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation class for travel specific calculation service methods {@link TravelRuleEngineCalculationService}
 */
public class DefaultTravelRuleEngineCalculationService extends DefaultRuleEngineCalculationService
		implements TravelRuleEngineCalculationService
{
	@Override
	public FeeRAO addFee(final CartRAO cartRao)
	{
		ServicesUtil.validateParameterNotNull(cartRao, "cart rao must not be null");
		final FeeRAO feeRao = new FeeRAO();
		// If the fee is for display, no order entry is created.
		// If the fee is to be added to cart, a new order entry is created.
		if (CollectionUtils.isNotEmpty(cartRao.getEntries()))
		{
			final OrderEntryRAO orderEntryRao = new OrderEntryRAO();
			orderEntryRao.setOrder(cartRao);
			feeRao.setAddedOrderEntry(orderEntryRao);
		}
		getRaoUtils().addAction(cartRao, feeRao);
		return feeRao;
	}

	@Override
	public RefundActionRAO addRefundFeeAction(final BookingRAO bookingRao)
	{
		ServicesUtil.validateParameterNotNull(bookingRao, "booking rao must not be null");
		final RefundActionRAO refundActionRao = new RefundActionRAO();
		getRaoUtils().addAction(bookingRao, refundActionRao);
		return refundActionRao;
	}

}
