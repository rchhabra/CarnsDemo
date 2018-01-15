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

package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationFacetSearchServiceTest
{
	@InjectMocks
	private final DefaultAccommodationFacetSearchService accommodationFacetSearchService = new DefaultAccommodationFacetSearchService()
	{
		@Override
		protected void populateGroupCommandFields(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
				final SearchQuery searchQuery)
		{

		}

		@Override
		protected void populateFacetFields(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
				final SearchQuery searchQuery)
		{

		}

		@Override
		protected void populateFields(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
				final SearchQuery searchQuery)
		{

		}
	};

	@Test
	public void testCreateFreeTextSearchQuery()
	{
		final FacetSearchConfig facetSearchConfig = Mockito.mock(FacetSearchConfig.class);
		final IndexedType indexedType = Mockito.mock(IndexedType.class);
		final String userQuery="User Query";

		final Map<String, String> ftsQueryBuilderParameters = Collections.singletonMap("SUGGESTIONS_ACCOMMODATION_PROPERTY",
				"value");
		BDDMockito.given(indexedType.getFtsQueryBuilderParameters()).willReturn(ftsQueryBuilderParameters);
		final Map<String, IndexedProperty> indexedProp = new HashMap<>();
		final IndexedProperty indexedProperty = Mockito.mock(IndexedProperty.class);
		indexedProp.put("indexedProperty", indexedProperty);
		BDDMockito.given(indexedProperty.getName()).willReturn("name");
		BDDMockito.given(indexedProperty.isFtsQuery()).willReturn(true);
		BDDMockito.given(indexedProperty.isFtsFuzzyQuery()).willReturn(true);
		BDDMockito.given(indexedProperty.isFtsWildcardQuery()).willReturn(true);
		BDDMockito.given(indexedProperty.isFtsPhraseQuery()).willReturn(true);

		final IndexedProperty indexedProperty2 = Mockito.mock(IndexedProperty.class);
		indexedProp.put("indexedProperty2", indexedProperty2);
		BDDMockito.given(indexedProperty2.getName()).willReturn(TravelservicesConstants.SEARCH_KEY_PROPERTY_NAME);
		BDDMockito.given(indexedProperty2.isFtsQuery()).willReturn(false);
		BDDMockito.given(indexedProperty2.isFtsFuzzyQuery()).willReturn(false);
		BDDMockito.given(indexedProperty2.isFtsWildcardQuery()).willReturn(false);
		BDDMockito.given(indexedProperty2.isFtsPhraseQuery()).willReturn(false);

		final IndexedProperty indexedProperty3 = Mockito.mock(IndexedProperty.class);
		indexedProp.put("indexedProperty3", indexedProperty3);
		BDDMockito.given(indexedProperty3.getName()).willReturn(TravelservicesConstants.SEARCH_KEY_LOCATION_NAMES);
		BDDMockito.given(indexedProperty3.isFtsQuery()).willReturn(false);
		BDDMockito.given(indexedProperty3.isFtsFuzzyQuery()).willReturn(false);
		BDDMockito.given(indexedProperty3.isFtsWildcardQuery()).willReturn(false);
		BDDMockito.given(indexedProperty3.isFtsPhraseQuery()).willReturn(false);

		BDDMockito.given(indexedType.getIndexedProperties()).willReturn(indexedProp);
		Assert.assertNotNull(accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery,
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION));

		Assert.assertNotNull(
				accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery,
						TravelservicesConstants.SOLR_SEARCH_TYPE_SPATIAL));

		Assert.assertNotNull(
				accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery,
						TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY));

		Assert.assertNotNull(accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery,
				TravelservicesConstants.SOLR_SEARCH_TYPE_ACCOMMODATION));
		Assert.assertNotNull(
				accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery, "other"));

		BDDMockito.given(indexedType.getFtsQueryBuilderParameters()).willReturn(null);
		Assert.assertNotNull(
				accommodationFacetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType, userQuery, "other"));

	}

}
