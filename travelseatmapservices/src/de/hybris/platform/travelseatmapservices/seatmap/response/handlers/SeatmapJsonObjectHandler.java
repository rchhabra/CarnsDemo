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
package de.hybris.platform.travelseatmapservices.seatmap.response.handlers;

import de.hybris.platform.travelseatmapservices.seatmap.response.SeatMapJSONObject;


/**
 * This is used to handle the populating logic for {@link=SeatMapJSONObject}
 */
public interface SeatmapJsonObjectHandler
{
	/**
	 * This populates the seatMapJsonObject with the SVG and CSS based on the files on seatMapPath
	 *
	 * @param seatMapPath
	 * @param seatMapJsonObject
	 */
	void populate(String seatMapPath, SeatMapJSONObject seatMapJsonObject);
}
