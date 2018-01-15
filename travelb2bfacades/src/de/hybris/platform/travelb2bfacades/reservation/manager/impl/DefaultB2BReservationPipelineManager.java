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
 */
package de.hybris.platform.travelb2bfacades.reservation.manager.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelb2bfacades.reservation.handlers.B2BReservationHandler;
import de.hybris.platform.travelb2bfacades.reservation.manager.B2BReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * The default implementation for {@link B2BReservationPipelineManager}
 */
public class DefaultB2BReservationPipelineManager implements B2BReservationPipelineManager
{
	private GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager;

	private List<B2BReservationHandler> handlers;

	@Override
	public B2BReservationData executePipeline(final AbstractOrderModel abstractOrderModel)
	{
		final B2BReservationData b2bReservationData = new B2BReservationData();
		getGlobalTravelReservationPipelineManager().executePipeline(abstractOrderModel, b2bReservationData);

		for (final B2BReservationHandler handler : getHandlers())
		{
			handler.handle(abstractOrderModel, b2bReservationData);
		}

		return b2bReservationData;
	}

	/**
	 * Gets global travel reservation pipeline manager.
	 *
	 * @return the global travel reservation pipeline manager
	 */
	protected GlobalTravelReservationPipelineManager getGlobalTravelReservationPipelineManager()
	{
		return globalTravelReservationPipelineManager;
	}

	/**
	 * Sets global travel reservation pipeline manager.
	 *
	 * @param globalTravelReservationPipelineManager
	 * 		the global travel reservation pipeline manager
	 */
	@Required
	public void setGlobalTravelReservationPipelineManager(
			final GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager)
	{
		this.globalTravelReservationPipelineManager = globalTravelReservationPipelineManager;
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	protected List<B2BReservationHandler> getHandlers()
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
	public void setHandlers(final List<B2BReservationHandler> handlers)
	{
		this.handlers = handlers;
	}
}
