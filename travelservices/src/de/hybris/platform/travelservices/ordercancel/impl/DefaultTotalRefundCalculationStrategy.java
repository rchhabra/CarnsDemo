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

package de.hybris.platform.travelservices.ordercancel.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelrulesengine.enums.RefundActionType;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.strategies.RefundActionStrategy;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Default implementation of {@link TotalRefundCalculationStrategy}
 */
public class DefaultTotalRefundCalculationStrategy implements TotalRefundCalculationStrategy
{
	private static final String DEFAULT_REFUND_ACTION = "DEFAULT";

	private TravelRulesService travelRulesService;
	private Map<String, RefundActionStrategy> refundActionStrategyMap;

	@Override
	public BigDecimal getTotalToRefund(final OrderModel order)
	{
		final RefundActionType refundAction = getTravelRulesService().getRefundAction(order);
		final String refundActionType = (refundAction != null && getRefundActionStrategyMap().get(refundAction.getCode()) != null)
				? refundAction.getCode() : DEFAULT_REFUND_ACTION;
		final double totalRefund = getRefundActionStrategyMap().get(refundActionType).applyStrategy(order);
		return BigDecimal.valueOf(totalRefund);
	}

	/**
	 * @return the travelRulesService
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * @param travelRulesService
	 *           the travelRulesService to set
	 */
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 * @return the refundActionStrategyMap
	 */
	protected Map<String, RefundActionStrategy> getRefundActionStrategyMap()
	{
		return refundActionStrategyMap;
	}

	/**
	 * @param refundActionStrategyMap
	 *           the refundActionStrategyMap to set
	 */
	public void setRefundActionStrategyMap(final Map<String, RefundActionStrategy> refundActionStrategyMap)
	{
		this.refundActionStrategyMap = refundActionStrategyMap;
	}

}
