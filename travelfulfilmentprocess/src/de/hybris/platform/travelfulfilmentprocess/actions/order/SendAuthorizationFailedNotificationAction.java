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

import de.hybris.platform.orderprocessing.events.AuthorizationFailedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Send authorization failed notification action.
 */
public class SendAuthorizationFailedNotificationAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendAuthorizationFailedNotificationAction.class);

	private EventService eventService;

	@Override
	public void executeAction(final OrderProcessModel process)
	{
		eventService.publishEvent(new AuthorizationFailedEvent(process));
		LOG.info("Process: " + process.getCode() + " in step " + getClass());
	}

	/**
	 * Gets event service.
	 *
	 * @return the event service
	 */
	protected EventService getEventService()
	{
		return eventService;
	}

	/**
	 * Sets event service.
	 *
	 * @param eventService
	 * 		the event service
	 */
	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
