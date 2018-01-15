/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */

package de.hybris.platform.travelfacades.facades.accommodation.search.impl;


import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.AccommodationSearchFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.AccommodationOfferingSearchPipelineManager;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link AccommodationSearchFacade}
 */
public class DefaultAccommodationSearchFacade implements AccommodationSearchFacade
{
	private AccommodationOfferingFacade accommodationOfferingFacade;
	private AccommodationOfferingSearchPipelineManager accommodationOfferingSearchPipelineManager;
	private Map<String, AccommodationOfferingSearchResponseSortStrategy> sortStrategyMap;
	private List<UpdateSearchCriterionStrategy> updateSearchCriterionStrategies;

	@Override
	public AccommodationSearchResponseData doSearch(final AccommodationSearchRequestData accommodationRequest)
	{

		final List<AccommodationOfferingDayRateData> accommodationOfferingAggregatedDayRates = new ArrayList<AccommodationOfferingDayRateData>();

		if (accommodationRequest.getCriterion() == null)
		{
			return null;
		}

		final List<RoomStayCandidateData> roomStayCandidates = accommodationRequest.getCriterion().getRoomStayCandidates();

		final CriterionData searchPageCriterionData = createSearchPageCriterionData();

		for (final RoomStayCandidateData roomStayCandidateData : roomStayCandidates)
		{
			final AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData>
					accommodationSearchPageData = getAccommodationOfferingFacade()
					.searchAccommodationOfferingDayRates(accommodationRequest, roomStayCandidateData);

			if (accommodationSearchPageData != null)
			{
				for (final UpdateSearchCriterionStrategy strategy : getUpdateSearchCriterionStrategies())
				{
					strategy.applyStrategy(searchPageCriterionData, accommodationSearchPageData);
				}

				final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = accommodationSearchPageData.getResults();
				if (CollectionUtils.isNotEmpty(accommodationOfferingDayRates))
				{
					accommodationOfferingDayRates.removeIf(rate -> Objects.isNull(rate.getPrice()));

					accommodationOfferingDayRates.forEach(accommodationOfferingDayRateData -> accommodationOfferingDayRateData
							.setRoomStayCandidateRefNumber(roomStayCandidates.indexOf(roomStayCandidateData)));

					accommodationOfferingAggregatedDayRates.addAll(accommodationOfferingDayRates);
				}
			}
		}

		final AccommodationSearchResponseData accommodationSearchResponseData = getAccommodationOfferingSearchPipelineManager()
				.executePipeline(accommodationOfferingAggregatedDayRates, accommodationRequest);

		if (accommodationSearchResponseData != null)
		{
			// Updating the response with search related params
			updateSearchResponse(accommodationSearchResponseData, searchPageCriterionData);

			// Calling the sorting strategies
			sortProperties(accommodationRequest, accommodationSearchResponseData);
		}

		return accommodationSearchResponseData;
	}

	/**
	 * Sort properties using a map of strategies.
	 *
	 * @param accommodationSearchRequest
	 * 		the accommodation search request
	 * @param accommodationSearchResponse
	 * 		the accommodation search response
	 */
	protected void sortProperties(final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		if (StringUtils.isBlank(accommodationSearchRequest.getCriterion().getSort()))
		{
			getSortStrategyMap().get(TravelfacadesConstants.ACCOMMODATION_SEARCH_DEFAULT_SORT_KEY)
					.sort(accommodationSearchResponse);
		}

		if (getSortStrategyMap().containsKey(accommodationSearchRequest.getCriterion().getSort()))
		{
			getSortStrategyMap().get(accommodationSearchRequest.getCriterion().getSort()).sort(accommodationSearchResponse);
		}
	}

	/**
	 * Create {@link CriterionData} to be used to update search related values.
	 *
	 * @return the criterion data
	 */
	protected CriterionData createSearchPageCriterionData()
	{
		return new CriterionData();
	}

	/**
	 * Update search response with the values from the given criterion data.
	 *
	 * @param searchResponseData
	 * 		the search response data
	 * @param criterion
	 * 		the criterion
	 */
	protected void updateSearchResponse(final AccommodationSearchResponseData searchResponseData, final CriterionData criterion)
	{
		searchResponseData.getCriterion().setFacets(criterion.getFacets());
		searchResponseData.getCriterion().setSorts(criterion.getSorts());
		searchResponseData.getCriterion().setQuery(criterion.getQuery());
		searchResponseData.getCriterion().setFilteredFacets(criterion.getFilteredFacets());
	}

	/**
	 * Gets accommodation offering facade.
	 *
	 * @return the accommodationOfferingFacade
	 */
	protected AccommodationOfferingFacade getAccommodationOfferingFacade()
	{
		return accommodationOfferingFacade;
	}

	/**
	 * Sets accommodation offering facade.
	 *
	 * @param accommodationOfferingFacade
	 * 		the accommodationOfferingFacade to set
	 */
	@Required
	public void setAccommodationOfferingFacade(final AccommodationOfferingFacade accommodationOfferingFacade)
	{
		this.accommodationOfferingFacade = accommodationOfferingFacade;
	}

	/**
	 * Gets accommodation offering search pipeline manager.
	 *
	 * @return the accommodationOfferingSearchPipelineManager
	 */
	protected AccommodationOfferingSearchPipelineManager getAccommodationOfferingSearchPipelineManager()
	{
		return accommodationOfferingSearchPipelineManager;
	}

	/**
	 * Sets accommodation offering search pipeline manager.
	 *
	 * @param accommodationOfferingSearchPipelineManager
	 * 		the accommodationOfferingSearchPipelineManager to set
	 */
	@Required
	public void setAccommodationOfferingSearchPipelineManager(
			final AccommodationOfferingSearchPipelineManager accommodationOfferingSearchPipelineManager)
	{
		this.accommodationOfferingSearchPipelineManager = accommodationOfferingSearchPipelineManager;
	}

	/**
	 * Gets sort strategy map.
	 *
	 * @return the sortStrategyMap
	 */
	protected Map<String, AccommodationOfferingSearchResponseSortStrategy> getSortStrategyMap()
	{
		return sortStrategyMap;
	}

	/**
	 * Sets sort strategy map.
	 *
	 * @param sortStrategyMap
	 * 		the sortStrategyMap to set
	 */
	@Required
	public void setSortStrategyMap(final Map<String, AccommodationOfferingSearchResponseSortStrategy> sortStrategyMap)
	{
		this.sortStrategyMap = sortStrategyMap;
	}

	/**
	 * Gets update search criterion strategies.
	 *
	 * @return the update search criterion strategies
	 */
	protected List<UpdateSearchCriterionStrategy> getUpdateSearchCriterionStrategies()
	{
		return updateSearchCriterionStrategies;
	}

	/**
	 * Sets update search criterion strategies.
	 *
	 * @param updateSearchCriterionStrategies
	 * 		the update search criterion strategies
	 */
	@Required
	public void setUpdateSearchCriterionStrategies(
			final List<UpdateSearchCriterionStrategy> updateSearchCriterionStrategies)
	{
		this.updateSearchCriterionStrategies = updateSearchCriterionStrategies;
	}
}
