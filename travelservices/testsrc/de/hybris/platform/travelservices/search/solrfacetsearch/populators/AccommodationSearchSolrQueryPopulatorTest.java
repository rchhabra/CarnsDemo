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

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;
import de.hybris.platform.travelservices.search.solrfacetsearch.AccommodationFacetSearchService;
import de.hybris.platform.travelservices.services.SolrIndexSortService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationSearchSolrQueryPopulatorTest
{
	@InjectMocks
	AccommodationSearchSolrQueryPopulator accommodationSearchSolrQueryPopulator;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private AccommodationFacetSearchService accommodationFacetSearchService;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;

	@Mock
	private SolrIndexSortService solrIndexSortService;

	@Test(expected = ConversionException.class)
	public void testPopulateForFacetConfigServiceException() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = new SearchQueryPageableData<SolrSearchQueryData>();
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSearchType("TEST_SEARCH_TYPE");
		final SolrSearchQueryTermData solrSearchQueryTermData = new SolrSearchQueryTermData();
		solrSearchQueryTermData.setKey("position");
		solrSearchQueryTermData.setValue("TEST_FILTER_POSITION_VALUE");
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("radius");
		solrSearchQueryTermData1.setValue("TEST_FILTER_RADIUS_VALUE");
		searchQueryData.setFilterTerms(Arrays.asList(solrSearchQueryTermData, solrSearchQueryTermData1));
		source.setSearchQueryData(searchQueryData);


		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		source.setPageableData(pageableData);
		final BaseSiteModel currentSite = new BaseSiteModel();
		when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);

		final CatalogModel productCatalog = new CatalogModel();
		when(baseSiteService.getProductCatalogs(currentSite)).thenReturn(Arrays.asList(productCatalog));
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setCatalog(productCatalog);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Arrays.asList(catalogVersionModel));

		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig())
				.thenReturn(solrFacetSearchConfigModel);

		final FacetSearchConfig configuration = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setLegacyMode(Boolean.TRUE);
		configuration.setSearchConfig(searchConfig);
		final IndexConfig indexConfig = new IndexConfig();
		final Map<String, IndexedType> indexedTypes = new HashMap<>();
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode("MarketingRatePlanInfo");
		indexedTypes.put("TEST_INDEXED_TYPE", indexedType);
		indexConfig.setIndexedTypes(indexedTypes);
		configuration.setIndexConfig(indexConfig);
		when(facetSearchConfigService.getConfiguration(Matchers.anyString()))
				.thenThrow(new FacetConfigServiceException("Exception"));

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping = new SolrIndexedTypeDefaultSortOrderMappingModel();
		defaultSortOrderMapping
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		when(solrIndexSortService.getDefaultSortOrderMapping(Matchers.anyString())).thenReturn(defaultSortOrderMapping);

		final LanguageModel language = new LanguageModel();
		language.setIsocode("TEST_CURRENT_LANGUAGE_ISO_CODE");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("TEST_CURRENT_CURRENCY_ISO_CODE");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final SolrSearchRequest target = new SolrSearchRequest();
		accommodationSearchSolrQueryPopulator.populate(source, target);
		Assert.assertNull(target.getSearchQuery());
	}

	@Test(expected = ConversionException.class)
	public void testPopulateForNoValidSolrConfigException() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = new SearchQueryPageableData<SolrSearchQueryData>();
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSearchType("TEST_SEARCH_TYPE");
		final SolrSearchQueryTermData solrSearchQueryTermData = new SolrSearchQueryTermData();
		solrSearchQueryTermData.setKey("position");
		solrSearchQueryTermData.setValue("TEST_FILTER_POSITION_VALUE");
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("radius");
		solrSearchQueryTermData1.setValue("TEST_FILTER_RADIUS_VALUE");
		searchQueryData.setFilterTerms(Arrays.asList(solrSearchQueryTermData, solrSearchQueryTermData1));
		source.setSearchQueryData(searchQueryData);


		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		source.setPageableData(pageableData);
		final BaseSiteModel currentSite = new BaseSiteModel();
		when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);

		final CatalogModel productCatalog = new CatalogModel();
		when(baseSiteService.getProductCatalogs(currentSite)).thenReturn(Arrays.asList(productCatalog));
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setCatalog(productCatalog);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Arrays.asList(catalogVersionModel));

		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig())
				.thenThrow(new NoValidSolrConfigException("Exception"));

		final FacetSearchConfig configuration = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setLegacyMode(Boolean.TRUE);
		configuration.setSearchConfig(searchConfig);
		final IndexConfig indexConfig = new IndexConfig();
		final Map<String, IndexedType> indexedTypes = new HashMap<>();
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode("MarketingRatePlanInfo");
		indexedTypes.put("TEST_INDEXED_TYPE", indexedType);
		indexConfig.setIndexedTypes(indexedTypes);
		configuration.setIndexConfig(indexConfig);
		when(facetSearchConfigService.getConfiguration(Matchers.anyString())).thenReturn(configuration);

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping = new SolrIndexedTypeDefaultSortOrderMappingModel();
		defaultSortOrderMapping
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		when(solrIndexSortService.getDefaultSortOrderMapping(Matchers.anyString())).thenReturn(defaultSortOrderMapping);

		final LanguageModel language = new LanguageModel();
		language.setIsocode("TEST_CURRENT_LANGUAGE_ISO_CODE");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("TEST_CURRENT_CURRENCY_ISO_CODE");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final SolrSearchRequest target = new SolrSearchRequest();
		accommodationSearchSolrQueryPopulator.populate(source, target);
		Assert.assertNull(target.getSearchQuery());
	}

	@Test(expected = ConversionException.class)
	public void testPopulateForConversionException() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = new SearchQueryPageableData<SolrSearchQueryData>();
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSearchType("TEST_SEARCH_TYPE");
		final SolrSearchQueryTermData solrSearchQueryTermData = new SolrSearchQueryTermData();
		solrSearchQueryTermData.setKey("position");
		solrSearchQueryTermData.setValue("TEST_FILTER_POSITION_VALUE");
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("radius");
		solrSearchQueryTermData1.setValue("TEST_FILTER_RADIUS_VALUE");
		searchQueryData.setFilterTerms(Arrays.asList(solrSearchQueryTermData, solrSearchQueryTermData1));
		source.setSearchQueryData(searchQueryData);


		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		source.setPageableData(pageableData);
		final BaseSiteModel currentSite = new BaseSiteModel();
		when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);

		final CatalogModel productCatalog = new CatalogModel();
		when(baseSiteService.getProductCatalogs(currentSite)).thenReturn(Arrays.asList(productCatalog));
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setCatalog(productCatalog);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.emptyList());

		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).thenReturn(solrFacetSearchConfigModel);

		final FacetSearchConfig configuration = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setLegacyMode(Boolean.TRUE);
		configuration.setSearchConfig(searchConfig);
		final IndexConfig indexConfig = new IndexConfig();
		final Map<String, IndexedType> indexedTypes = new HashMap<>();
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode("MarketingRatePlanInfo");
		indexedTypes.put("TEST_INDEXED_TYPE", indexedType);
		indexConfig.setIndexedTypes(indexedTypes);
		configuration.setIndexConfig(indexConfig);
		when(facetSearchConfigService.getConfiguration(Matchers.anyString())).thenReturn(configuration);

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping = new SolrIndexedTypeDefaultSortOrderMappingModel();
		defaultSortOrderMapping
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		when(solrIndexSortService.getDefaultSortOrderMapping(Matchers.anyString())).thenReturn(defaultSortOrderMapping);

		final LanguageModel language = new LanguageModel();
		language.setIsocode("TEST_CURRENT_LANGUAGE_ISO_CODE");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("TEST_CURRENT_CURRENCY_ISO_CODE");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final SolrSearchRequest target = new SolrSearchRequest();
		accommodationSearchSolrQueryPopulator.populate(source, target);
		Assert.assertNull(target.getSearchQuery());
	}

	@Test
	public void testPopulate() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = new SearchQueryPageableData<SolrSearchQueryData>();
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_SPATIAL);
		final SolrSearchQueryTermData solrSearchQueryTermData = new SolrSearchQueryTermData();
		solrSearchQueryTermData.setKey("position");
		solrSearchQueryTermData.setValue("TEST_FILTER_POSITION_VALUE");
		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey("radius");
		solrSearchQueryTermData1.setValue("TEST_FILTER_RADIUS_VALUE");
		searchQueryData.setFilterTerms(Arrays.asList(solrSearchQueryTermData, solrSearchQueryTermData1));
		source.setSearchQueryData(searchQueryData);


		final PageableData pageableData = new PageableData();
		pageableData.setSort("TEST_PAGEABLE_SORT_CODE");
		source.setPageableData(pageableData);
		 final BaseSiteModel currentSite =new BaseSiteModel();
		when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);

		final CatalogModel productCatalog=new CatalogModel();
		when(baseSiteService.getProductCatalogs(currentSite)).thenReturn(Arrays.asList(productCatalog));
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setCatalog(productCatalog);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Arrays.asList(catalogVersionModel));

		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).thenReturn(solrFacetSearchConfigModel);

		final FacetSearchConfig configuration = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setLegacyMode(Boolean.TRUE);
		configuration.setSearchConfig(searchConfig);
		final IndexConfig indexConfig = new IndexConfig();
		final Map<String, IndexedType> indexedTypes = new HashMap<>();
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode("MarketingRatePlanInfo");
		indexedTypes.put("TEST_INDEXED_TYPE", indexedType);
		indexConfig.setIndexedTypes(indexedTypes);
		configuration.setIndexConfig(indexConfig);
		when(facetSearchConfigService.getConfiguration(Matchers.anyString())).thenReturn(configuration);

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping=new SolrIndexedTypeDefaultSortOrderMappingModel();
		defaultSortOrderMapping
				.setDefaultSortOrder(Stream.of("TEST_SOLR_FIELD_MODEL_A", "TEST_SOLR_FIELD_MODEL_B").collect(Collectors.toList()));
		when(solrIndexSortService.getDefaultSortOrderMapping(Matchers.anyString())).thenReturn(defaultSortOrderMapping);

		final LanguageModel language=new LanguageModel();
		language.setIsocode("TEST_CURRENT_LANGUAGE_ISO_CODE");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("TEST_CURRENT_CURRENCY_ISO_CODE");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final SolrSearchRequest target = new SolrSearchRequest();
		accommodationSearchSolrQueryPopulator.populate(source, target);
		Assert.assertNotNull(target.getSearchQuery());
	}

}
