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

package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultFacetSearchService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.solrfacetsearch.AccommodationFacetSearchService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link AccommodationFacetSearchService}. Extends {@link DefaultFacetSearchService} to introduce
 * accommodation specific functionality.
 */
public class DefaultAccommodationFacetSearchService extends DefaultFacetSearchService implements AccommodationFacetSearchService
{
	@Override
	public SearchQuery createFreeTextSearchQuery(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final String userQuery, final String searchType)
	{
		SearchQuery searchQuery = createSearchQuery(facetSearchConfig, indexedType);

		populateGroupCommandFields(facetSearchConfig, indexedType, searchQuery);
		populateFacetFields(facetSearchConfig, indexedType, searchQuery);
		populateFields(facetSearchConfig, indexedType, searchQuery);
		populateFreeTextQuery(facetSearchConfig, indexedType, searchQuery, userQuery, searchType);

		return searchQuery;
	}


	protected void populateFreeTextQuery(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final SearchQuery searchQuery, final String userQuery, final String searchType)
	{
		searchQuery.setFreeTextQueryBuilder(indexedType.getFtsQueryBuilder());

		if (MapUtils.isNotEmpty(indexedType.getFtsQueryBuilderParameters()))
		{
			searchQuery.getFreeTextQueryBuilderParameters().putAll(indexedType.getFtsQueryBuilderParameters());
		}

		searchQuery.setUserQuery(userQuery);

		for (IndexedProperty indexedProperty : indexedType.getIndexedProperties().values())
		{
			if (checkAgainstSearchType(indexedProperty, searchType))
			{
				if (indexedProperty.isFtsQuery())
				{
					searchQuery.addFreeTextQuery(indexedProperty.getName(), indexedProperty.getFtsQueryMinTermLength(),
							indexedProperty.getFtsQueryBoost());
				}

				if (indexedProperty.isFtsFuzzyQuery())
				{
					searchQuery.addFreeTextFuzzyQuery(indexedProperty.getName(), indexedProperty.getFtsFuzzyQueryMinTermLength(),
							indexedProperty.getFtsFuzzyQueryFuzziness(), indexedProperty.getFtsFuzzyQueryBoost());
				}

				if (indexedProperty.isFtsWildcardQuery())
				{
					searchQuery.addFreeTextWildcardQuery(indexedProperty.getName(), indexedProperty
									.getFtsWildcardQueryMinTermLength(),
							indexedProperty.getFtsWildcardQueryType(), indexedProperty.getFtsWildcardQueryBoost());
				}
			}

			if (indexedProperty.isFtsPhraseQuery())
			{
				searchQuery.addFreeTextPhraseQuery(indexedProperty.getName(), indexedProperty.getFtsPhraseQuerySlop(),
						indexedProperty.getFtsPhraseQueryBoost());
			}
		}
	}

	protected boolean checkAgainstSearchType(final IndexedProperty indexedProperty, final String searchType)
	{
		if (StringUtils.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION))
		{
			return !StringUtils.contains(indexedProperty.getName(), TravelservicesConstants.SEARCH_KEY_PROPERTY_NAME);
		}
		else if (
				StringUtils.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY)
						|| StringUtils.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_ACCOMMODATION)
						|| StringUtils.equalsIgnoreCase(searchType, TravelservicesConstants.SOLR_SEARCH_TYPE_SPATIAL))
		{
			return !StringUtils.contains(indexedProperty.getName(), TravelservicesConstants.SEARCH_KEY_LOCATION_NAMES);
		}
		return true;
	}
}
