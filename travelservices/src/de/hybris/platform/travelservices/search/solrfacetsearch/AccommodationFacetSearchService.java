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
 */

package de.hybris.platform.travelservices.search.solrfacetsearch;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;


/**
 * Extension of {@link FacetSearchService} to pass search type into free text search query builder, needed for Accommodation
 * specific functionality
 */
public interface AccommodationFacetSearchService extends FacetSearchService
{
	/**
	 * Creates free text search query field, which are filtered for accommodation functionality based on tbe search type provided
	 *
	 * @param facetSearchConfig
	 * @param indexedType
	 * @param userQuery
	 * @param searchType
	 * @return
	 */
	SearchQuery createFreeTextSearchQuery(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final String userQuery, final String searchType);
}
