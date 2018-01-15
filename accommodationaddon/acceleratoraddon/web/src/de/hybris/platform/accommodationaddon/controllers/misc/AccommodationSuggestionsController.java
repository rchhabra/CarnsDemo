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

package de.hybris.platform.accommodationaddon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper.AccommodationAutoSuggestWrapper;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationSuggestionFacade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for accommodation AutoSuggestion functionality
 */
@Controller
public class AccommodationSuggestionsController extends AbstractController
{

	@Resource(name = "accommodationSuggestionFacade")
	private AccommodationSuggestionFacade accommodationSuggestionFacade;

	@Resource(name = "accommodationAutoSuggestWrapper")
	private AccommodationAutoSuggestWrapper accommodationAutoSuggestWrapper;

	private static final String SUGGESTION_RESULT = "suggestionResult";
	private static final String IS_GOOGLE_RESULT = "isGoogleResult";

	/**
	 * @param text
	 *           the search text
	 * @param model
	 *
	 * @return the location of the jsp with the json response for the autosuggestion
	 */
	@RequestMapping(value = "/accommodation-suggestions", method = RequestMethod.GET, produces = "application/json")
	public String locationSuggestion(@RequestParam(value = "text") final String text, final Model model)
	{
		List<GlobalSuggestionData> suggestionResults = accommodationSuggestionFacade.getLocationSuggestions(text);

		if (CollectionUtils.isEmpty(suggestionResults))
		{
			// google search
			suggestionResults = accommodationAutoSuggestWrapper.getAutoCompleteResults(text);
			model.addAttribute(IS_GOOGLE_RESULT, Boolean.TRUE);
		}

		model.addAttribute(SUGGESTION_RESULT, suggestionResults);

		return AccommodationaddonControllerConstants.Views.Pages.Suggestions.SuggestionsSearchJsonResponse;
	}

}
