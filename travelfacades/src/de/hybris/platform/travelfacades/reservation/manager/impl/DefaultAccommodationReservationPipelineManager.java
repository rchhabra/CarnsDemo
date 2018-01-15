/*
 *
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

package de.hybris.platform.travelfacades.reservation.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete Pipeline Manager class that will return a {@link AccommodationReservationData} after executing a list of handlers
 * on the {@link AbstractOrderModel} given as input
 */
public class DefaultAccommodationReservationPipelineManager implements AccommodationReservationPipelineManager
{
	private List<AccommodationReservationHandler> handlers;

	private static final Logger LOG = Logger.getLogger(DefaultAccommodationReservationPipelineManager.class);

	@Override
	public AccommodationReservationData executePipeline(final AbstractOrderModel abstractOrderModel)
	{
		if (!isAccommodationBooking(abstractOrderModel))
		{
			return null;
		}

		final AccommodationReservationData reservationData = new AccommodationReservationData();
		try
		{
			for (final AccommodationReservationHandler handler : getHandlers())
			{
				handler.handle(abstractOrderModel, reservationData);
			}
		}
		catch (final AccommodationPipelineException e)
		{
			LOG.error("Impossible to retrieve and convert AccommodationReservationData", e);
			return null;
		}

		return reservationData;
	}

	private boolean isAccommodationBooking(final AbstractOrderModel abstractOrderModel)
	{
		return abstractOrderModel != null && abstractOrderModel.getEntries().stream()
				.filter(abstractOrderEntry -> OrderEntryType.ACCOMMODATION.equals(abstractOrderEntry.getType())).findAny()
				.isPresent();
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	public List<AccommodationReservationHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * Sets handlers.
	 *
	 * @param handlers
	 * 		the handlers
	 */
	@Required
	public void setHandlers(final List<AccommodationReservationHandler> handlers)
	{
		this.handlers = handlers;
	}

}
