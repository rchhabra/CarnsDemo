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

package de.hybris.platform.travelfacades.accommodation.strategies;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;

import java.util.List;


/**
 * Strategy to create the display view of suggestion results.
 */
public interface AccommodationSuggestionsDisplayStrategy
{

	/**
	 * Method to create a list of GlobalSuggestionData from a list of AccommodationOfferingDayRateData.
	 *
	 * @param text
	 * 		the search text
	 * @param results
	 * 		the list of accommodationOfferingDayRateData
	 * @param searchType
	 * 		the search type
	 * @return the list of GlobalSuggestionData
	 */
	List<GlobalSuggestionData> createGlobalSuggestionData(String text, List<AccommodationOfferingDayRateData> results,
			String searchType);

}
