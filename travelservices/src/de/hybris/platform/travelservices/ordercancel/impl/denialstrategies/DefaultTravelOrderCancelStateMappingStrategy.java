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

package de.hybris.platform.travelservices.ordercancel.impl.denialstrategies;

import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelStateMappingStrategy;


/**
 * Default implementation of {@link OrderCancelStateMappingStrategy}. Determines OrderCancelState for a given Order.
 */
public class DefaultTravelOrderCancelStateMappingStrategy implements OrderCancelStateMappingStrategy
{

	@Override
	public OrderCancelState getOrderCancelState(final OrderModel order)
	{
		final OrderStatus orderStatus = order.getStatus();
		if (OrderStatus.CANCELLED.equals(orderStatus) || OrderStatus.CANCELLING.equals(orderStatus))
		{
			return OrderCancelState.CANCELIMPOSSIBLE;
		}
		return OrderCancelState.CANCELPOSSIBLE;
	}
}
