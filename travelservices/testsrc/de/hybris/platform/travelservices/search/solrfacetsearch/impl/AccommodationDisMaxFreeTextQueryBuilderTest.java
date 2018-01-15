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
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationDisMaxFreeTextQueryBuilderTest
{
	@InjectMocks
	private AccommodationDisMaxFreeTextQueryBuilder accommodationDisMaxFreeTextQueryBuilder;

	@Test
	public void testBuildQuery()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType){
			@Override
			public Map<String, String> getFreeTextQueryBuilderParameters()
			{
				 final Map<String, String> freeTextQueryBuilderParameters=new HashMap<>();
				freeTextQueryBuilderParameters.put("tie", "" + 10);
				freeTextQueryBuilderParameters.put("groupByQueryType", Boolean.FALSE.toString());
				return freeTextQueryBuilderParameters;
			}
		};
		searchQuery.setUserQuery("");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));


		searchQuery.setUserQuery("User Query");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));
	}


	@Test
	public void testBuildQueryForGroupByQueryType()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);
		searchQuery.setUserQuery("");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));


		searchQuery.setUserQuery("User Query");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));
	}

	@Test
	public void testBuildQueryForEmptyfreeTextQueryBuilderParameters()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType)
		{
			@Override
			public Map<String, String> getFreeTextQueryBuilderParameters()
			{
				 final Map<String, String> freeTextQueryBuilderParameters=new HashMap<>();
				freeTextQueryBuilderParameters.put("tie", "" + 10);
				freeTextQueryBuilderParameters.put("groupByQueryType", Boolean.TRUE.toString());
				return freeTextQueryBuilderParameters;
			}
		};
		searchQuery.setUserQuery("");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));


		searchQuery.setUserQuery("User Query");
		Assert.assertTrue(StringUtils.isEmpty(accommodationDisMaxFreeTextQueryBuilder.buildQuery(searchQuery)));
	}

}
