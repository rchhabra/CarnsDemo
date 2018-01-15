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
 */
package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.travelservices.search.AccommodationOfferingSearchService;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Default implementation of the AccommodationOfferingSearchService
 *
 * @param <ITEM>
 */
public class DefaultSolrAccommodationOfferingSearchService<ITEM>
		implements
		AccommodationOfferingSearchService<SolrSearchQueryData, ITEM, AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM>>
{
	private Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> accommodationSearchQueryPageableConverter;

	private Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;

	private Converter<SolrSearchResponse, AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM>> searchResponseConverter;


	@Override
	public AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM> doSearch(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData)
	{
		validateParameterNotNull(searchQueryData, "SearchQueryData cannot be null");
		// Create the SearchQueryPageableData that contains our parameters
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = buildSearchQueryPageableData(searchQueryData,
				pageableData);
		// Build up the search request
		final SolrSearchRequest solrSearchRequest = getAccommodationSearchQueryPageableConverter().convert(searchQueryPageableData);
		// Execute the search
		final SolrSearchResponse solrSearchResponse = getSearchRequestConverter().convert(solrSearchRequest);
		// Convert the response
		return getSearchResponseConverter().convert(solrSearchResponse);
	}

	protected SearchQueryPageableData<SolrSearchQueryData> buildSearchQueryPageableData(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData)
	{
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = createSearchQueryPageableData();
		searchQueryPageableData.setSearchQueryData(searchQueryData);
		searchQueryPageableData.setPageableData(pageableData);
		return searchQueryPageableData;
	}

	// Create methods for data object - can be overridden in spring config
	protected SearchQueryPageableData<SolrSearchQueryData> createSearchQueryPageableData()
	{
		return new SearchQueryPageableData<SolrSearchQueryData>();
	}

	/**
	 * @return the accommodationSearchQueryPageableConverter
	 */
	protected Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> getAccommodationSearchQueryPageableConverter()
	{
		return accommodationSearchQueryPageableConverter;
	}

	/**
	 * @param accommodationSearchQueryPageableConverter
	 *           the accommodationSearchQueryPageableConverter to set
	 */
	@Required
	public void setAccommodationSearchQueryPageableConverter(
			final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> accommodationSearchQueryPageableConverter)
	{
		this.accommodationSearchQueryPageableConverter = accommodationSearchQueryPageableConverter;
	}

	/**
	 * @return the searchResponseConverter
	 */
	protected Converter<SolrSearchResponse, AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM>> getSearchResponseConverter()
	{
		return searchResponseConverter;
	}

	/**
	 * @param searchResponseConverter
	 *           the searchResponseConverter to set
	 */
	@Required
	public void setSearchResponseConverter(
			final Converter<SolrSearchResponse, AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM>> searchResponseConverter)
	{
		this.searchResponseConverter = searchResponseConverter;
	}

	/**
	 * @return the searchRequestConverter
	 *
	 */
	protected Converter<SolrSearchRequest, SolrSearchResponse> getSearchRequestConverter()
	{
		return searchRequestConverter;
	}

	/**
	 * @param searchRequestConverter
	 *           the searchRequestConverter to set
	 */
	@Required
	public void setSearchRequestConverter(final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter)
	{
		this.searchRequestConverter = searchRequestConverter;
	}

}
