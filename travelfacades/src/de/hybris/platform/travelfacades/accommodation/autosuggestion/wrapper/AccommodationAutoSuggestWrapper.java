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

package de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper;

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.accommodation.PlaceDetailsResponseData;

import java.util.List;


/**
 * Wrapper responsible for getting the predictions for the input text and getting the information for Places.
 */
public interface AccommodationAutoSuggestWrapper
{

	/**
	 * Provides list of GlobalSuggestionData consisting the information of places by related to the search text.
	 *
	 * @param searchText
	 * 		Unique ID to identify the Place.
	 * @return the auto complete results
	 */
	List<GlobalSuggestionData> getAutoCompleteResults(String searchText);

	/**
	 * Provides the information of Place related to referenceID. The information includes geometry,types of place,
	 * place_id
	 *
	 * @param placeID
	 * 		Unique ID to identify the Place.
	 * @return placeDetails place details
	 */
	PlaceDetailsResponseData getPlaceDetails(String placeID);
}
