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
package de.hybris.platform.travelseatmapservices.controllers;


import de.hybris.platform.travelseatmapservices.jsonresponse.manager.SeatMapJsonResponsePipelineManager;
import de.hybris.platform.travelseatmapservices.seatmap.response.SeatMapJSONObject;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * SeatMap Finder Controller for handling requests for the Seat Map.
 */
@Controller
@RequestMapping(value = "/seatmap")
public class SeatMapFinderController
{
	@Resource(name = "seatMapJsonResponsePipelineManager")
	private SeatMapJsonResponsePipelineManager seatMapJsonResponsePipelineManager;

	@ResponseBody
	@RequestMapping(value = "/getseatmap/{vehiclecode}", method = RequestMethod.GET, produces = "application/json")
	public SeatMapJSONObject performSearch(@PathVariable("vehiclecode") final String vehiclecode)
	{
		return seatMapJsonResponsePipelineManager.executePipeline(vehiclecode);
	}
}
