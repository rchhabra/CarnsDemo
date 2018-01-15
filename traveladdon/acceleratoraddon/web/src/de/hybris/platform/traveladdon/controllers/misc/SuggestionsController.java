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

package de.hybris.platform.traveladdon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for Suggestions functionality.
 */
@Controller
@RequestMapping("/suggestions")
public class SuggestionsController extends AbstractController
{
	protected static final Logger LOG = Logger.getLogger(SuggestionsController.class);

	@Resource(name = "transportOfferingFacade")
	protected TransportOfferingFacade transportOfferingFacade;

	/**
	 * @param text
	 *           search text
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/origin", method = RequestMethod.GET, produces = "application/json")
	public String originSuggestions(@RequestParam(value = "text") final String text, final Model model)
	{
		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> results = transportOfferingFacade.getOriginSuggestionData(text);
		model.addAttribute("results", results);
		return TraveladdonControllerConstants.Views.Pages.Suggestions.JSONSearchResponse;
	}

	/**
	 * @param code
	 *           selected Origin location code
	 * @param text
	 *           search text
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/destination", method = RequestMethod.GET, produces = "application/json")
	public String destinationSuggestions(@RequestParam(value = "code") final String code,
			@RequestParam(value = "text") final String text, final Model model)
	{
		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> results = transportOfferingFacade
				.getDestinationSuggestionData(code, text);
		model.addAttribute("results", results);
		return TraveladdonControllerConstants.Views.Pages.Suggestions.JSONSearchResponse;
	}
}
