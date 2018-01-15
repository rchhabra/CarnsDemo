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

package de.hybris.platform.travelfulfilmentprocess.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelfulfilmentprocess.TravelCheckOrderService;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * The type Default travel check order service.
 */
public class DefaultTravelCheckOrderService implements TravelCheckOrderService
{
	@Override
	public boolean check(final OrderModel order)
	{
		if (!order.getCalculated().booleanValue() || CollectionUtils.isEmpty(order.getEntries())
				|| Objects.isNull(order.getPaymentInfo()))
		{
			//If any of the following is incorrect
			// Order must be calculated
			// Order must have some Entries
			// Order must have some payment info to use in the process
			return false;
		}
		else
		{
			// Order delivery options must be valid
			return checkDeliveryOptions(order);
		}
	}

	/**
	 * Check delivery options.
	 *
	 * @param order
	 *           the order
	 * @return the boolean
	 */
	protected boolean checkDeliveryOptions(final OrderModel order)
	{
		if (order.getDeliveryAddress() == null)
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				if (entry.getDeliveryPointOfService() == null && entry.getDeliveryAddress() == null)
				{
					// Order and Entry have no delivery address and some entries are not for pickup
					return false;
				}
			}
		}

		return true;
	}
}
