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

package de.hybris.platform.travelfacades.fare.search.manager.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultFareSearchPipelineManager implements FareSearchPipelineManager
{

	private List<FareSearchHandler> handlers;

	@Override
	public FareSelectionData executePipeline(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData
			fareSearchRequestData)
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();

		for (final FareSearchHandler handler : getHandlers())
		{
			handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);
		}

		return fareSelectionData;
	}

	protected List<FareSearchHandler> getHandlers()
	{
		return handlers;
	}

	@Required
	public void setHandlers(final List<FareSearchHandler> handlers)
	{
		this.handlers = handlers;
	}

}
