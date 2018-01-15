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
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

import java.util.Collections;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelFacetSearchQueryFacetsPopulatorTest
{
	@InjectMocks
	DefaultTravelFacetSearchQueryFacetsPopulator travelFacetSearchQueryFacetsPopulator;

	@Mock
	FieldNameTranslator fieldNameTranslator;

	@Test
	public void testDoSearch()
	{
		travelFacetSearchQueryFacetsPopulator.setDefaultFacetValuesMaxLimit(100);
		travelFacetSearchQueryFacetsPopulator.setDefaultFacetValuesMinCount(0);
		final SearchQueryConverterData source = new SearchQueryConverterData();
		final SearchQuery searchQuery = Mockito.mock(SearchQuery.class);
		source.setSearchQuery(searchQuery);
		final SolrQuery target = new SolrQuery();
		final FacetField facetField = new FacetField("field", FacetType.MULTISELECTOR);
		BDDMockito.given(searchQuery.getFacets()).willReturn(Collections.singletonList(facetField));
		BDDMockito.given(fieldNameTranslator.translate(searchQuery, "field", FieldType.INDEX)).willReturn("translatedField");

		travelFacetSearchQueryFacetsPopulator.populate(source, target);

		Assert.assertEquals(100, target.getFacetLimit());
	}

}
