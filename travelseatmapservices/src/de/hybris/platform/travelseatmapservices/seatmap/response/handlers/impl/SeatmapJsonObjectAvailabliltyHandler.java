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
package de.hybris.platform.travelseatmapservices.seatmap.response.handlers.impl;

import de.hybris.platform.travelseatmapservices.seatmap.response.SeatMapJSONObject;
import de.hybris.platform.travelseatmapservices.seatmap.response.handlers.SeatmapJsonObjectHandler;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * This checks if the seatMapJsonObject consist of all required information.
 */
public class SeatmapJsonObjectAvailabliltyHandler implements SeatmapJsonObjectHandler
{

	@Override
	public void populate(final String seatMapPath, final SeatMapJSONObject seatMapJsonObject)
	{
		seatMapJsonObject
				.setAvailable(MapUtils.isNotEmpty(seatMapJsonObject.getSvg()) && StringUtils.isNotBlank(seatMapJsonObject.getCss()));

	}

}
