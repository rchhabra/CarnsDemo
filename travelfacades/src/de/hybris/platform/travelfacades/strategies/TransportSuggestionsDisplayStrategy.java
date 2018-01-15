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

package de.hybris.platform.travelfacades.strategies;

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;

import java.util.List;
import java.util.Map;


public interface TransportSuggestionsDisplayStrategy
{
	/**
	 * Suggestion display types
	 */
	enum LegSuggestionType
	{
		ORIGIN, DESTINATION
	}

	/**
	 * Builds a map object to store suggestions
	 *
	 * @param searchText
	 * @param transportOfferingDataList
	 * @return
	 */
	Map<GlobalSuggestionData, List<GlobalSuggestionData>> createSuggestionsMap(String searchText,
			List<TransportOfferingData> transportOfferingDataList, LegSuggestionType type);
}
