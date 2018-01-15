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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * Populator invoked as part of chain of populators belonging to data conversion from order model to order data, it sets
 * original order code to the order data.
 */
public class OriginalOrderCodePopulator implements Populator<OrderModel, OrderData>
{

	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{
		OrderModel orderModel = source.getOriginalOrder();
		if(orderModel == null)
		{
			Optional<OrderHistoryEntryModel> lastHistoryEntry = source.getHistoryEntries().stream()
					.filter(historyEntry -> historyEntry.getPreviousOrderVersion() != null)
					.sorted((order1, order2) -> order2.getCreationtime().compareTo(order1.getCreationtime())).findFirst();
			orderModel = lastHistoryEntry.isPresent() ? lastHistoryEntry.get().getPreviousOrderVersion() : orderModel;
		}
		target.setOriginalOrderCode((orderModel != null) ? orderModel.getCode() : StringUtils.EMPTY);
	}

}
