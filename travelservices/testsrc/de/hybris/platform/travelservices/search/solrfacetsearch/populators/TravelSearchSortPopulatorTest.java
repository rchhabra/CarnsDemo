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

package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.solrsearch.config.SolrSortFieldModel;
import de.hybris.platform.commerceservices.model.solrsearch.config.SolrSortModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSearchSortPopulatorTest
{
	@Test
	public void testForPageableData()
	{
		final SolrSearchRequest target = new SolrSearchRequest();
		final SearchQueryPageableData source = new SearchQueryPageableData();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel = new SolrSortFieldModel();
		sortFieldModel.setFieldName("TEST_SOLR_FIELD_MODEL_A");
		sortFieldModel.setAscending(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel1 = new SolrSortFieldModel();
		sortFieldModel1.setFieldName("TEST_SOLR_FIELD_MODEL_B");
		sortFieldModel1.setAscending(Boolean.FALSE);
		sortModel.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sortModel1.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));

		final Map<String, IndexedTypeSort> sortsByCode = new HashMap<>();
		sortsByCode.put("TEST_SORT_CODE_1", sort1);
		sortsByCode.put("TEST_SORT_CODE", sort);
		indexedType.setSortsByCode(sortsByCode);

		target.setIndexedType(indexedType);
		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_SORT_CODE");
		target.setPageableData(pageableData);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSort("TEST_SORT_CODE");
		target.setSearchQueryData(searchQueryData);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		facetSearchConfig.setSearchConfig(searchConfig);
		target.setFacetSearchConfig(facetSearchConfig);

		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		target.setSearchQuery(searchQuery);
		final TravelSearchSortPopulator travelSearchSortPopulator = new TravelSearchSortPopulator()
		{
			@Override
			protected List<IndexedTypeSort> getFilteredSorts(final IndexedType indexedType)
			{
				return Stream.of(sort, sort1).collect(Collectors.toList());
			}
		};
		travelSearchSortPopulator.populate(source, target);
		Assert.assertNotNull(target.getCurrentSort());
	}

	@Test
	public void testForSearchQueryData()
	{
		final SolrSearchRequest target = new SolrSearchRequest();
		final SearchQueryPageableData source = new SearchQueryPageableData();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel = new SolrSortFieldModel();
		sortFieldModel.setFieldName("TEST_SOLR_FIELD_MODEL_A");
		sortFieldModel.setAscending(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel1 = new SolrSortFieldModel();
		sortFieldModel1.setFieldName("TEST_SOLR_FIELD_MODEL_B");
		sortFieldModel1.setAscending(Boolean.FALSE);
		sortModel.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sortModel1.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));

		final Map<String, IndexedTypeSort> sortsByCode = new HashMap<>();
		sortsByCode.put("TEST_SORT_CODE_1", sort1);
		sortsByCode.put("TEST_SORT_CODE", sort);
		indexedType.setSortsByCode(sortsByCode);

		target.setIndexedType(indexedType);

		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSort("TEST_SORT_CODE");
		target.setSearchQueryData(searchQueryData);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		facetSearchConfig.setSearchConfig(searchConfig);
		target.setFacetSearchConfig(facetSearchConfig);

		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		target.setSearchQuery(searchQuery);
		final TravelSearchSortPopulator travelSearchSortPopulator = new TravelSearchSortPopulator()
		{
			@Override
			protected List<IndexedTypeSort> getFilteredSorts(final IndexedType indexedType)
			{
				return Stream.of(sort, sort1).collect(Collectors.toList());
			}
		};
		travelSearchSortPopulator.populate(source, target);
		Assert.assertNotNull(target.getCurrentSort());
	}

	@Test
	public void testForDefaultOrderSorts()
	{
		final SolrSearchRequest target = new SolrSearchRequest();
		final SearchQueryPageableData source = new SearchQueryPageableData();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel = new SolrSortFieldModel();
		sortFieldModel.setFieldName("TEST_SOLR_FIELD_MODEL_A");
		sortFieldModel.setAscending(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel1 = new SolrSortFieldModel();
		sortFieldModel1.setFieldName("TEST_SOLR_FIELD_MODEL_B");
		sortFieldModel1.setAscending(Boolean.FALSE);
		sortModel.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sortModel1.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));

		final Map<String, IndexedTypeSort> sortsByCode = new HashMap<>();
		sortsByCode.put("TEST_SORT_CODE_1", sort1);
		sortsByCode.put("TEST_SORT_CODE", sort);
		indexedType.setSortsByCode(sortsByCode);

		target.setIndexedType(indexedType);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		target.setSearchQueryData(searchQueryData);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		facetSearchConfig.setSearchConfig(searchConfig);
		target.setFacetSearchConfig(facetSearchConfig);

		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		target.setSearchQuery(searchQuery);
		final TravelSearchSortPopulator travelSearchSortPopulator = new TravelSearchSortPopulator()
		{
			@Override
			protected List<IndexedTypeSort> getFilteredSorts(final IndexedType indexedType)
			{
				return Stream.of(sort, sort1).collect(Collectors.toList());
			}
		};
		travelSearchSortPopulator.populate(source, target);
		Assert.assertNotNull(target.getCurrentSort());
	}

	@Test
	public void testForFirstAvailableSort()
	{
		final SolrSearchRequest target = new SolrSearchRequest();
		final SearchQueryPageableData source = new SearchQueryPageableData();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel = new SolrSortFieldModel();
		sortFieldModel.setFieldName("TEST_SOLR_FIELD_MODEL_A");
		sortFieldModel.setAscending(Boolean.TRUE);

		final SolrSortFieldModel sortFieldModel1 = new SolrSortFieldModel();
		sortFieldModel1.setFieldName("TEST_SOLR_FIELD_MODEL_B");
		sortFieldModel1.setAscending(Boolean.FALSE);
		sortModel.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sortModel1.setFields(Stream.of(sortFieldModel, sortFieldModel1).collect(Collectors.toList()));
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));

		final Map<String, IndexedTypeSort> sortsByCode = new HashMap<>();
		sortsByCode.put("TEST_SORT_CODE_1", sort1);
		sortsByCode.put("TEST_SORT_CODE", sort);
		indexedType.setSortsByCode(sortsByCode);

		target.setIndexedType(indexedType);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		target.setSearchQueryData(searchQueryData);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		facetSearchConfig.setSearchConfig(searchConfig);
		target.setFacetSearchConfig(facetSearchConfig);

		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		target.setSearchQuery(searchQuery);
		final TravelSearchSortPopulator travelSearchSortPopulator = new TravelSearchSortPopulator()
		{
			@Override
			protected List<IndexedTypeSort> getFilteredSorts(final IndexedType indexedType)
			{
				return Stream.of(sort, sort1).collect(Collectors.toList());
			}
		};
		travelSearchSortPopulator.populate(source, target);
		Assert.assertNotNull(target.getCurrentSort());
	}
}
