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
package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.jalo.warehouse.TransportOffering;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;
import de.hybris.platform.travelservices.search.strategies.SolrDateRangeStrategy;
import de.hybris.platform.travelservices.services.SolrIndexSortService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelSearchSolrQueryPopulatorTest
{

	@InjectMocks
	private final TravelSearchSolrQueryPopulator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FacetSearchConfig, IndexedType, ?, SearchQuery, ?>> populator = new TravelSearchSolrQueryPopulator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FacetSearchConfig, IndexedType, ?, SearchQuery, ?>>();

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private FacetSearchService facetSearchService;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;

	@Mock
	private SolrDateRangeStrategy solrDateRangeStrategy;

	@Mock
	private SolrIndexSortService solrIndexSortService;

	@Before
	public void setup() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		given(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig())
				.willReturn(new SolrFacetSearchConfigModel());

		given(facetSearchConfigService.getConfiguration(Mockito.anyString())).willReturn(TestData.createFacetSearchConfig(false));

		given(solrIndexSortService.getDefaultSortOrderMapping(Mockito.anyString()))
				.willReturn(TestData.createSolrIndexedTypeDefaultSortOrderMappingModel());

		given(facetSearchService.createFreeTextSearchQuery(Mockito.any(FacetSearchConfig.class), Mockito.any(IndexedType.class),
				Mockito.anyString())).willReturn(new SearchQuery(new FacetSearchConfig(), new IndexedType()));

		given(commonI18NService.getCurrentCurrency()).willReturn(Mockito.mock(CurrencyModel.class));
		given(commonI18NService.getCurrentCurrency().getIsocode()).willReturn("GBP");

		given(commonI18NService.getCurrentLanguage()).willReturn(Mockito.mock(LanguageModel.class));
		given(commonI18NService.getCurrentLanguage().getIsocode()).willReturn("EN");
	}

	@Test
	public void testActivitySearch()
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = TestData
				.createSearchQueryPageableData(TravelservicesConstants.ACTIVITY_SEARCH);
		final SolrSearchRequest target = new SolrSearchRequest<>();

		populator.populate(source, target);

		Assert.assertNotNull(target.getSearchQuery());

		final SearchQuery searchQuery = (SearchQuery) target.getSearchQuery();

		Assert.assertEquals(1, searchQuery.getGroupCommands().size());
		Assert.assertEquals(TravelservicesConstants.SEARCH_KEY_DESTINATION_LOCATION_CITY,
				searchQuery.getGroupCommands().get(0).getField());
		Assert.assertFalse(searchQuery.isGroupFacets());
	}

	@Test
	public void testSuggestionsOriginSearchInLegacyMode() throws FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = TestData
				.createSearchQueryPageableData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN);
		final SolrSearchRequest target = new SolrSearchRequest<>();
		given(facetSearchConfigService.getConfiguration(Mockito.anyString())).willReturn(TestData.createFacetSearchConfig(true));
		populator.populate(source, target);

		Assert.assertNotNull(target.getSearchQuery());

		final SearchQuery searchQuery = (SearchQuery) target.getSearchQuery();

		Assert.assertEquals(1, searchQuery.getGroupCommands().size());
		Assert.assertEquals(TravelservicesConstants.SOLR_GROUP_KEY_ORIGIN_LOCATION_HIERARCHY,
				searchQuery.getGroupCommands().get(0).getField());
		Assert.assertFalse(searchQuery.isGroupFacets());
		Assert.assertEquals(Operator.OR, searchQuery.getDefaultOperator());
		Assert.assertEquals("LTN", searchQuery.getUserQuery());
	}

	@Test
	public void testSuggestionsDestinationSearch()
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = TestData
				.createSearchQueryPageableData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION);
		final SolrSearchRequest target = new SolrSearchRequest<>();

		populator.populate(source, target);

		Assert.assertNotNull(target.getSearchQuery());

		final SearchQuery searchQuery = (SearchQuery) target.getSearchQuery();

		Assert.assertEquals(1, searchQuery.getGroupCommands().size());
		Assert.assertEquals(TravelservicesConstants.SOLR_GROUP_KEY_DESTINATION_LOCATION_HIERARCHY,
				searchQuery.getGroupCommands().get(0).getField());
		Assert.assertFalse(searchQuery.isGroupFacets());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ConversionException.class)
	public void testNoValidSolrConfigException() throws NoValidSolrConfigException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = TestData
				.createSearchQueryPageableData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION);
		final SolrSearchRequest target = new SolrSearchRequest<>();

		given(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).willThrow(NoValidSolrConfigException.class);

		populator.populate(source, target);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ConversionException.class)
	public void testFacetConfigServiceException() throws FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = TestData
				.createSearchQueryPageableData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION);
		final SolrSearchRequest target = new SolrSearchRequest<>();

		given(facetSearchConfigService.getConfiguration(Mockito.anyString())).willThrow(FacetConfigServiceException.class);

		populator.populate(source, target);
	}

	private static class TestData
	{

		public static SearchQueryPageableData<SolrSearchQueryData> createSearchQueryPageableData(final String searchType)
		{
			final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageable = new SearchQueryPageableData<>();
			searchQueryPageable.setSearchQueryData(createSolrSearchQuery(searchType));
			searchQueryPageable.setPageableData(new PageableData());
			return searchQueryPageable;
		}


		public static SolrIndexedTypeDefaultSortOrderMappingModel createSolrIndexedTypeDefaultSortOrderMappingModel()
		{
			final SolrIndexedTypeDefaultSortOrderMappingModel solrIndexedTypeDefaultSortOrderMappingModel = new SolrIndexedTypeDefaultSortOrderMappingModel();
			solrIndexedTypeDefaultSortOrderMappingModel.setDefaultSortOrder(Stream.of("code").collect(Collectors.toList()));
			return solrIndexedTypeDefaultSortOrderMappingModel;
		}

		public static FacetSearchConfig createFacetSearchConfig(final boolean legacyMode)
		{
			final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
			facetSearchConfig.setIndexConfig(createIndexConfig());
			facetSearchConfig.setSearchConfig(createSearchConfig(legacyMode));
			return facetSearchConfig;
		}

		private static SolrSearchQueryData createSolrSearchQuery(final String searchType)
		{
			final SolrSearchQueryData solrSearchQuery = new SolrSearchQueryData();
			solrSearchQuery.setFreeTextSearch("LTN");
			solrSearchQuery.setSearchType(searchType);
			return solrSearchQuery;
		}

		private static IndexConfig createIndexConfig()
		{
			final IndexConfig indexConfig = new IndexConfig();
			indexConfig.setIndexedTypes(createIndexedType());
			return indexConfig;
		}

		private static Map<String, IndexedType> createIndexedType()
		{
			final Map<String, IndexedType> indexedTypes = new HashMap<>();
			final IndexedType indexedType = new IndexedType();
			indexedType.setCode(TransportOffering.class.getSimpleName());
			indexedTypes.put("index", indexedType);
			return indexedTypes;
		}

		private static SearchConfig createSearchConfig(final boolean legacyMode)
		{
			final SearchConfig searchConfig = new SearchConfig();
			searchConfig.setLegacyMode(legacyMode);
			if(legacyMode){
				searchConfig.setDefaultSortOrder(Stream.of("code").collect(Collectors.toList()));
			}
			return searchConfig;
		}
	}
}
