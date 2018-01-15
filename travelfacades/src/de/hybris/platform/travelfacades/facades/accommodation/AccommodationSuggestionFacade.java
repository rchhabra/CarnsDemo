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

package de.hybris.platform.travelfacades.facades.accommodation;

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;

import java.util.List;


/**
 * Facade that exposes the methods for the accommodation suggestions functionality
 */
public interface AccommodationSuggestionFacade
{

	/**
	 * Returns a list of GlobalSuggestionData with the suggestions for the location search
	 *
	 * @param text
	 * 		the search text
	 * @return a list of GlobalSuggestionData with the suggestions for the location search
	 */
	List<GlobalSuggestionData> getLocationSuggestions(String text);

}
