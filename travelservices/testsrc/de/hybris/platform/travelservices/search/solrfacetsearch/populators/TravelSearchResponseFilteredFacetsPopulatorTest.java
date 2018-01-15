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
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSearchResponseFilteredFacetsPopulatorTest
{
	@InjectMocks
	TravelSearchResponseFilteredFacetsPopulator travelSearchResponseFilteredFacetsPopulator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testPopulate()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		indexedType.setSorts(Collections.emptyList());
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final IndexedPropertyValueData<IndexedProperty> indexedPropertyValueDataA = new IndexedPropertyValueData<>();
		indexedPropertyValueDataA.setValue("TEST_FACET_VALUE_CODE_A");
		final IndexedProperty indexedPropertyA = new IndexedProperty();
		indexedPropertyA.setName("TEST_FACET_CODE_A");
		indexedPropertyA.setDisplayName("TEST_FACET_DISPAY_NAME_A");
		indexedPropertyValueDataA.setIndexedProperty(indexedPropertyA);

		final IndexedPropertyValueData<IndexedProperty> indexedPropertyValueDataB = new IndexedPropertyValueData<>();
		indexedPropertyValueDataB.setValue("TEST_FACET_VALUE_CODE_B");
		final IndexedProperty indexedPropertyB = new IndexedProperty();
		indexedPropertyB.setName("TEST_FACET_CODE_B");
		indexedPropertyB.setDisplayName("TEST_FACET_DISPAY_NAME_B");
		indexedPropertyValueDataB.setIndexedProperty(indexedPropertyB);

		final IndexedPropertyValueData<IndexedProperty> indexedPropertyValueDataC = new IndexedPropertyValueData<>();
		indexedPropertyValueDataC.setValue("TEST_FACET_VALUE_CODE_C");
		final IndexedProperty indexedPropertyC = new IndexedProperty();
		indexedPropertyC.setName("TEST_FACET_CODE_B");
		indexedPropertyValueDataC.setIndexedProperty(indexedPropertyC);


		final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>();
		indexedPropertyValues.add(indexedPropertyValueDataA);
		indexedPropertyValues.add(indexedPropertyValueDataB);
		indexedPropertyValues.add(indexedPropertyValueDataC);

		request.setIndexedPropertyValues(indexedPropertyValues);
		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES))
				.thenReturn("TEST_FACET_CODE_A");

		final AccommodationOfferingSearchPageData target = new AccommodationOfferingSearchPageData<>();
		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData1.setValue("TEST_FACET_VALUE_CODE_B");
		final SolrSearchQueryTermData solrSearchQueryTermData2 = new SolrSearchQueryTermData();
		solrSearchQueryTermData2.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData2.setValue("TEST_FACET_VALUE_CODE_A");
		solrSearchQueryData
				.setFilterTerms(Stream.of(solrSearchQueryTermData1, solrSearchQueryTermData2).collect(Collectors.toList()));
		target.setCurrentQuery(solrSearchQueryData);
		travelSearchResponseFilteredFacetsPopulator.populate(solrSearchResponse, target);
		Assert.assertTrue(CollectionUtils.isNotEmpty(target.getFilteredFacets()));
	}

	@Test
	public void testPopulateForEmptyRequiredFacets()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();
		indexedType.setSorts(Collections.emptyList());
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);

		final IndexedPropertyValueData<IndexedProperty> indexedPropertyValueDataA = new IndexedPropertyValueData<>();
		indexedPropertyValueDataA.setValue("TEST_FACET_VALUE_CODE_A");
		final IndexedProperty indexedPropertyA = new IndexedProperty();
		indexedPropertyA.setName("TEST_FACET_CODE_A");
		indexedPropertyA.setDisplayName("TEST_FACET_DISPAY_NAME_A");
		indexedPropertyValueDataA.setIndexedProperty(indexedPropertyA);

		final IndexedPropertyValueData<IndexedProperty> indexedPropertyValueDataB = new IndexedPropertyValueData<>();
		indexedPropertyValueDataB.setValue("TEST_FACET_VALUE_CODE_B");
		final IndexedProperty indexedPropertyB = new IndexedProperty();
		indexedPropertyB.setName("TEST_FACET_CODE_B");
		indexedPropertyB.setDisplayName("TEST_FACET_DISPAY_NAME_B");
		indexedPropertyValueDataB.setIndexedProperty(indexedPropertyB);


		final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>();
		indexedPropertyValues.add(indexedPropertyValueDataA);
		indexedPropertyValues.add(indexedPropertyValueDataB);

		request.setIndexedPropertyValues(indexedPropertyValues);
		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES))
				.thenReturn("");

		final AccommodationOfferingSearchPageData target = new AccommodationOfferingSearchPageData<>();
		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData1.setValue("TEST_FACET_VALUE_CODE_B");
		final SolrSearchQueryTermData solrSearchQueryTermData2 = new SolrSearchQueryTermData();
		solrSearchQueryTermData2.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData2.setValue("TEST_FACET_VALUE_CODE_A");
		solrSearchQueryData
				.setFilterTerms(Stream.of(solrSearchQueryTermData1, solrSearchQueryTermData2).collect(Collectors.toList()));
		target.setCurrentQuery(solrSearchQueryData);
		travelSearchResponseFilteredFacetsPopulator.populate(solrSearchResponse, target);
		Assert.assertTrue(CollectionUtils.isNotEmpty(target.getFilteredFacets()));
	}

	@Test
	public void testPopulateForEmptyIndexedProperties()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final SolrSearchRequest request = new SolrSearchRequest<>();

		final IndexedType indexedType = new IndexedType();

		indexedType.setSorts(Collections.emptyList());
		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);
		request.setSearchQuery(searchQuery);


		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		request.setPageableData(pageableData);
		solrSearchResponse.setRequest(request);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES))
				.thenReturn("TEST_FACET_CODE_B");

		final AccommodationOfferingSearchPageData target = new AccommodationOfferingSearchPageData<>();
		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData1.setValue("TEST_FACET_VALUE_CODE_B");
		final SolrSearchQueryTermData solrSearchQueryTermData2 = new SolrSearchQueryTermData();
		solrSearchQueryTermData2.setKey("TEST_FACET_CODE_B");
		solrSearchQueryTermData2.setValue("TEST_FACET_VALUE_CODE_A");
		solrSearchQueryData
				.setFilterTerms(Stream.of(solrSearchQueryTermData1, solrSearchQueryTermData2).collect(Collectors.toList()));
		target.setCurrentQuery(solrSearchQueryData);
		travelSearchResponseFilteredFacetsPopulator.populate(solrSearchResponse, target);
		Assert.assertTrue(CollectionUtils.isEmpty(target.getFilteredFacets()));
	}

}
