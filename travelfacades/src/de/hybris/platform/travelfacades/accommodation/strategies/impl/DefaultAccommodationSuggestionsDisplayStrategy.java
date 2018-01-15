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

package de.hybris.platform.travelfacades.accommodation.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationSuggestionsDisplayStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link AccommodationSuggestionsDisplayStrategy}
 */
public class DefaultAccommodationSuggestionsDisplayStrategy implements AccommodationSuggestionsDisplayStrategy
{
	private static final String NAMES_SEPARATOR = ", ";

	@Override
	public List<GlobalSuggestionData> createGlobalSuggestionData(final String text,
			final List<AccommodationOfferingDayRateData> results, final String searchType)
	{
		final List<GlobalSuggestionData> globalSuggestionDataList = new ArrayList<>();
		for (final AccommodationOfferingDayRateData accommodationOfferingDayRateData : results)
		{
			final GlobalSuggestionData globalSuggestionData = new GlobalSuggestionData();

			if (StringUtils
					.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION))
			{
				globalSuggestionData.setCode(accommodationOfferingDayRateData.getLocationCodes());
				globalSuggestionData.setName(getLocationName(accommodationOfferingDayRateData.getLocationNames()));
				globalSuggestionData.setSuggestionType(SuggestionType.LOCATION);
			}
			else if (StringUtils
					.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY))
			{
				globalSuggestionData.setCode(accommodationOfferingDayRateData.getAccommodationOfferingCode());
				globalSuggestionData.setName(accommodationOfferingDayRateData.getAccommodationOfferingName());
				globalSuggestionData.setSuggestionType(SuggestionType.PROPERTY);
			}

			globalSuggestionDataList.add(globalSuggestionData);

		}
		return globalSuggestionDataList;
	}

	protected String getLocationName(final List<String> locationNames)
	{
		String nameToDisplay = StringUtils.EMPTY;
		for (int count = 0; count < CollectionUtils.size(locationNames); count++)
		{
			if (count > 0)
			{
				nameToDisplay = nameToDisplay.concat(NAMES_SEPARATOR);
			}
			nameToDisplay = nameToDisplay.concat(locationNames.get(count));
		}

		return nameToDisplay;
	}

}
