/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.travelseatmapservices.jsonresponse.manager.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelseatmapservices.jsonresponse.manager.SeatMapJsonResponsePipelineManager;
import de.hybris.platform.travelseatmapservices.seatmap.response.SeatMapJSONObject;
import de.hybris.platform.travelseatmapservices.seatmap.response.handlers.SeatmapJsonObjectHandler;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Pipeline Manager class that will return a {@link SeatMapJSONObject} after executing a list of handlers.
 */
public class DefaultSeatMapJsonResponsePipelineManager implements SeatMapJsonResponsePipelineManager
{
	private ConfigurationService configurationService;

	private List<SeatmapJsonObjectHandler> handlers;

	@Override
	public SeatMapJSONObject executePipeline(final String vehicleCode)
	{
		final SeatMapJSONObject seatMapJsonObject = new SeatMapJSONObject();
		String seatMapPath = getConfigurationService().getConfiguration().getString("travelseatmapservices.seat.svg.path");
		if (StringUtils.isBlank(seatMapPath))
		{
			return seatMapJsonObject;
		}
		seatMapPath = seatMapPath + File.separator + vehicleCode;
		for (final SeatmapJsonObjectHandler handler : getHandlers())
		{
			handler.populate(seatMapPath, seatMapJsonObject);
		}
		return seatMapJsonObject;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @return the handlers
	 */
	protected List<SeatmapJsonObjectHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @param handlers
	 *           the handlers to set
	 */
	@Required
	public void setHandlers(final List<SeatmapJsonObjectHandler> handlers)
	{
		this.handlers = handlers;
	}

}
