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
import de.hybris.platform.commerceservices.model.solrsearch.config.SolrSortModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSearchResponseSortsPopulatorTest
{
	@InjectMocks
	private TravelSearchResponseSortsPopulator travelSearchResponseSortsPopulator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void test()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		request.setCurrentSort(sort);
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE,TEST_PAGEABLE_SORT_CODE_1");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(3, target.getSorts().size());

	}

	@Test
	public void testForBlankCustomSortOrders()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		request.setCurrentSort(sort);
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn(StringUtils.EMPTY);
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(1, target.getSorts().size());

	}

	@Test
	public void testForNullIndexedType()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();


		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), null);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(1, target.getSorts().size());

	}

	@Test
	public void testForNullCurrentSort()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(2, target.getSorts().size());

	}

	@Test
	public void testForNullSortInIndexedSorts()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(1, target.getSorts().size());

	}

	@Test
	public void testForNullPageableData()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		request.setCurrentSort(sort);
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(2, target.getSorts().size());

	}

	@Test
	public void testForBlankSortInPageableData()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		final IndexedTypeSort sort = new IndexedTypeSort();
		final SolrSortModel sortModel = new SolrSortModel();
		sortModel.setVisible(Boolean.FALSE);
		sort.setSort(sortModel);
		sort.setCode("TEST_SORT_CODE");
		sort.setName("TEST_SORT_NAME");
		request.setCurrentSort(sort);
		final IndexedTypeSort sort1 = new IndexedTypeSort();
		final SolrSortModel sortModel1 = new SolrSortModel();
		sortModel1.setVisible(Boolean.TRUE);
		sort1.setSort(sortModel1);
		sort1.setCode("TEST_SORT_CODE_1");
		sort1.setName("TEST_SORT_NAME_1");
		indexedType.setSorts(Stream.of(sort, sort1).collect(Collectors.toList()));
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final PageableData pageableData = new PageableData();
		pageableData.setSort(StringUtils.EMPTY);
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		final SearchPageData target = new SearchPageData();
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS))
				.thenReturn("TEST_PAGEABLE_SORT_CODE");
		travelSearchResponseSortsPopulator.populate(solrSearchResponse, target);
		Assert.assertEquals(2, target.getSorts().size());

	}


}
