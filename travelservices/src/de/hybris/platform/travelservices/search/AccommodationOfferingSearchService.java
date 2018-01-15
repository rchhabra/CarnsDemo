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
package de.hybris.platform.travelservices.search;

import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;


/**
 * AccommodationOfferingSearchService interface. Its main purpose is to retrieve accommodation offering search results.
 * The search service implementation is stateless, i.e. it does not maintain any state for each search, instead it
 * externalizes the search state in the search page data returned. The search must be initiated by calling the search
 * method {@link #accommodationOfferingSearch.doSearch(SolrSearchQueryData,PageableData)}. From this method's return
 * value, the search page data result can be retrieved.
 *
 * @param <STATE>
 *           The type of the search query state. This is implementation specific. For example
 *           {@link de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData}
 * @param <ITEM>
 *           The type of items returned as part of the search results. For example
 *           {@link de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData}
 * @param <RESULT>
 *           The type of the search page data returned. Must be (or extend) {@link FacetSearchPageData}.
 */
public interface AccommodationOfferingSearchService<STATE, ITEM, RESULT extends AccommodationOfferingSearchPageData<STATE, ITEM>>
{

	/**
	 * @param searchQueryData
	 *           the search query object
	 * @param pageableData
	 *           the page to return
	 * @return the search results
	 */
	RESULT doSearch(final SolrSearchQueryData searchQueryData, final PageableData pageableData);
}
