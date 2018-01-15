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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.List;


/**
 * Sets attribute and sends {@link SubmitOrderEvent} event when order is submitted.
 */
public class TravelEventPublishingSubmitOrderStrategy extends EventPublishingSubmitOrderStrategy
{

	private EventService eventService;
	private SessionService sessionService;

	@Override
	public void submitOrder(final OrderModel order)
	{
		final SubmitOrderEvent event = new SubmitOrderEvent(order);

		if (getSessionService().getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER) != null
				&& getSessionService().getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN) != null)
		{
			final int originDestinationRefNumber = getSessionService()
					.getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER);
			final List<String> travellersToCheckIn = getSessionService()
					.getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN);

			if (!travellersToCheckIn.isEmpty())
			{
				event.setTravellers(travellersToCheckIn);
				event.setOriginDestinationRefNumber(originDestinationRefNumber);
			}
			getSessionService().removeAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER);
			getSessionService().removeAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN);
		}

		getEventService().publishEvent(event);
	}

	/**
	 * @return the eventService
	 */
	protected EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	@Override
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
