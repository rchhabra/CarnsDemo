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
package de.hybris.platform.travelfulfilmentprocess.actions.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.time.TimeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Abstract order action.
 *
 * @param <T>
 * 		the type parameter
 */
public abstract class AbstractOrderAction<T extends OrderProcessModel> extends AbstractAction<T>
{
	/**
	 * The Time service.
	 */
	protected TimeService timeService; // NOPMD

	/**
	 * Prepares order history entry {@link OrderHistoryEntryModel} for the given order and description and with the
	 * current timestamp. The {@link OrderHistoryEntryModel} is not saved!.
	 *
	 * @param description
	 * 		the description
	 * @param order
	 * 		the order
	 *
	 * @return {@link OrderHistoryEntryModel}
	 */
	protected OrderHistoryEntryModel createHistoryLog(final String description, final OrderModel order)
	{
		final OrderHistoryEntryModel historyEntry = modelService.create(OrderHistoryEntryModel.class);
		historyEntry.setTimestamp(getTimeService().getCurrentTime());
		historyEntry.setOrder(order);
		historyEntry.setDescription(description);
		return historyEntry;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
