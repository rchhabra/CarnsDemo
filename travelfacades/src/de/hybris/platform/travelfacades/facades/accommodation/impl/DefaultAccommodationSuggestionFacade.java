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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationSuggestionsDisplayStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationSuggestionFacade;
import de.hybris.platform.travelfacades.search.AccommodationOfferingSearchFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationSuggestionFacade}
 */
public class DefaultAccommodationSuggestionFacade implements AccommodationSuggestionFacade
{
	private AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade;
	private AccommodationSuggestionsDisplayStrategy accommodationSuggestionsDisplayStrategy;
	private ConfigurationService configurationService;

	@Override
	public List<GlobalSuggestionData> getLocationSuggestions(final String text)
	{
		// We have to do 2 solr suggestion searches because we need 2 different groupings, one based on location and another one
		// based on property.

		final int pageSize = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_SUGGESTIONS_PAGE_SIZE);

		// First search finds a list of locations that match the text typed in by the user.
		final int maxLocationNumber = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_SUGGESTIONS_MAX_LOCATION_SIZE);

		final List<GlobalSuggestionData> locationSuggestions = getSuggestions(text, Math.min(maxLocationNumber, pageSize),
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION);

		// Second search finds a list of properties that match the text typed in by the user.
		// Max property number is calculated to fill the max number of suggestions allowed in the accommodation finder component.
		final int maxPropertyNumber = pageSize - CollectionUtils.size(locationSuggestions);

		if (maxPropertyNumber <= 0)
		{
			return locationSuggestions;
		}

		final List<GlobalSuggestionData> propertySuggestions = getSuggestions(text, maxPropertyNumber,
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY);

		if (CollectionUtils.isNotEmpty(locationSuggestions))
		{
			if (CollectionUtils.isNotEmpty(propertySuggestions))
			{
				locationSuggestions.addAll(propertySuggestions);
			}
			return locationSuggestions;
		}

		return propertySuggestions;
	}

	protected List<GlobalSuggestionData> getSuggestions(final String text, final int pageSize, final String searchType)
	{
		final SearchData searchData = populateSuggestionsSearchData(searchType, text);
		final PageableData pageableData = populateSuggestionsPageableData(pageSize);

		final AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData>
				accommodationOfferingSearchPageData = getAccommodationOfferingSearchFacade()
				.accommodationOfferingSearch(searchData, pageableData);

		if (CollectionUtils.isNotEmpty(accommodationOfferingSearchPageData.getResults()))
		{
			// Creates the List of GlobalSuggestionData
			return getAccommodationSuggestionsDisplayStrategy()
					.createGlobalSuggestionData(text, accommodationOfferingSearchPageData.getResults(), searchType);
		}

		return Collections.emptyList();
	}

	protected SearchData populateSuggestionsSearchData(final String searchType, final String text)
	{
		final SearchData searchData = new SearchData();
		searchData.setFreeTextSearch(text);
		searchData.setSearchType(searchType);
		return searchData;
	}

	protected PageableData populateSuggestionsPageableData(final int pageSize)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		return pageableData;
	}

	/**
	 * @return the accommodationOfferingSearchFacade
	 */
	protected AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> getAccommodationOfferingSearchFacade()
	{
		return accommodationOfferingSearchFacade;
	}

	/**
	 * @param accommodationOfferingSearchFacade the accommodationOfferingSearchFacade to set
	 */
	@Required
	public void setAccommodationOfferingSearchFacade(
			final AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade)
	{
		this.accommodationOfferingSearchFacade = accommodationOfferingSearchFacade;
	}

	/**
	 * @return the accommodationSuggestionsDisplayStrategy
	 */
	protected AccommodationSuggestionsDisplayStrategy getAccommodationSuggestionsDisplayStrategy()
	{
		return accommodationSuggestionsDisplayStrategy;
	}

	/**
	 * @param accommodationSuggestionsDisplayStrategy the accommodationSuggestionsDisplayStrategy to set
	 */
	@Required
	public void setAccommodationSuggestionsDisplayStrategy(
			final AccommodationSuggestionsDisplayStrategy accommodationSuggestionsDisplayStrategy)
	{
		this.accommodationSuggestionsDisplayStrategy = accommodationSuggestionsDisplayStrategy;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
