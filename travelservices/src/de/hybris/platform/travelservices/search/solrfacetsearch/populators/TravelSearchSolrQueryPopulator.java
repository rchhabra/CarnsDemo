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

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
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

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * @param <INDEXED_PROPERTY_TYPE>
 * @param <INDEXED_TYPE_SORT_TYPE>
 */
public class TravelSearchSolrQueryPopulator<INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FacetSearchConfig, IndexedType,
				INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE>>
{
	private CommonI18NService commonI18NService;
	private FacetSearchService facetSearchService;
	private FacetSearchConfigService facetSearchConfigService;
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
	private SolrDateRangeStrategy solrDateRangeStrategy;
	private SolrIndexSortService solrIndexSortService;

	@Override
	public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FacetSearchConfig, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE>
					target)
	{
		// Setup the SolrSearchRequest
		target.setSearchQueryData(source.getSearchQueryData());
		target.setPageableData(source.getPageableData());
		try
		{
			target.setFacetSearchConfig(getFacetSearchConfig());
		}
		catch (final NoValidSolrConfigException e)
		{
			throw new ConversionException("No valid solrFacetSearchConfig found for the current context", e);
		}
		catch (final FacetConfigServiceException e)
		{
			throw new ConversionException(e.getMessage(), e);
		}

		// We can only search one core so select the indexed type
		target.setIndexedType(getIndexedType(target.getFacetSearchConfig()));

		// Create the solr search query for the config and type (this sets-up the default page size and sort order)
		final SearchQuery searchQuery = createSearchQuery(target.getFacetSearchConfig(), target.getIndexedType(),
				source.getSearchQueryData().getFreeTextSearch());
		searchQuery.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode());
		searchQuery.setLanguage(getCommonI18NService().getCurrentLanguage().getIsocode());

		// enable spell checker
		searchQuery.setEnableSpellcheck(true);

		final String searchType = source.getSearchQueryData().getSearchType();

		switch (searchType)
		{
			case TravelservicesConstants.ACTIVITY_SEARCH:
				searchQuery.addGroupCommand(TravelservicesConstants.SEARCH_KEY_DESTINATION_LOCATION_CITY);
				searchQuery.setGroupFacets(false);
				break;
			case TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN:
				searchQuery.addGroupCommand(TravelservicesConstants.SOLR_GROUP_KEY_ORIGIN_LOCATION_HIERARCHY);
				searchQuery.setGroupFacets(false);
				break;
			case TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION:
				searchQuery.addGroupCommand(TravelservicesConstants.SOLR_GROUP_KEY_DESTINATION_LOCATION_HIERARCHY);
				searchQuery.setGroupFacets(false);
				break;
			case TravelservicesConstants.SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE:
				searchQuery.addGroupCommand(TravelservicesConstants.SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE);
				searchQuery.setGroupFacets(false);
				break;
			default:
				// do nothing
				break;
		}

		target.setSearchQuery(searchQuery);
	}


	/**
	 * Resolves suitable {@link FacetSearchConfig} for the query based on the configured strategy bean.<br>
	 *
	 * @return {@link FacetSearchConfig} that is converted from {@link SolrFacetSearchConfigModel}
	 * @throws NoValidSolrConfigException, FacetConfigServiceException
	 */
	protected FacetSearchConfig getFacetSearchConfig() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = getSolrFacetSearchConfigSelectionStrategy()
				.getCurrentSolrFacetSearchConfig();

		final FacetSearchConfig configuration = getFacetSearchConfigService()
				.getConfiguration(solrFacetSearchConfigModel.getName());

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping = getSolrIndexSortService()
				.getDefaultSortOrderMapping(TransportOffering.class.getSimpleName());

		if (defaultSortOrderMapping != null)
		{
			configuration.getSearchConfig().setDefaultSortOrder(defaultSortOrderMapping.getDefaultSortOrder());
		}

		return configuration;
	}

	protected IndexedType getIndexedType(final FacetSearchConfig config)
	{
		final IndexConfig indexConfig = config.getIndexConfig();

		// Strategy for working out which of the available indexed types to use
		final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();
		if (CollectionUtils.isNotEmpty(indexedTypes))
		{
			for (final IndexedType indexedType : indexedTypes)
			{
				if (StringUtils.equalsIgnoreCase(indexedType.getCode(), TransportOffering.class.getSimpleName()))
				{
					return indexedType;
				}
			}
		}

		// No indexed types
		return null;
	}

	protected SearchQuery createSearchQuery(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final String freeTextSearch)
	{
		SearchQuery searchQuery;

		if (facetSearchConfig.getSearchConfig().isLegacyMode())
		{
			searchQuery = new SearchQuery(facetSearchConfig, indexedType);
			searchQuery.setDefaultOperator(Operator.OR);
			searchQuery.setUserQuery(freeTextSearch);
		}
		else
		{
			searchQuery = getFacetSearchService().createFreeTextSearchQuery(facetSearchConfig, indexedType, freeTextSearch);
		}

		return searchQuery;
	}

	/**
	 * @return CommonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @return SolrDateRangeStrategy
	 */
	protected SolrDateRangeStrategy getSolrDateRangeStrategy()
	{
		return solrDateRangeStrategy;
	}

	/**
	 * @param solrDateRangeStrategy
	 */
	@Required
	public void setSolrDateRangeStrategy(final SolrDateRangeStrategy solrDateRangeStrategy)
	{
		this.solrDateRangeStrategy = solrDateRangeStrategy;
	}

	/**
	 * @param commonI18NService
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return FacetSearchService
	 */
	protected FacetSearchService getFacetSearchService()
	{
		return facetSearchService;
	}

	/**
	 * @param facetSearchService
	 */
	@Required
	public void setFacetSearchService(final FacetSearchService facetSearchService)
	{
		this.facetSearchService = facetSearchService;
	}

	/**
	 * @return FacetSearchConfigService
	 */
	protected FacetSearchConfigService getFacetSearchConfigService()
	{
		return facetSearchConfigService;
	}

	/**
	 * @param facetSearchConfigService
	 */
	@Required
	public void setFacetSearchConfigService(final FacetSearchConfigService facetSearchConfigService)
	{
		this.facetSearchConfigService = facetSearchConfigService;
	}

	/**
	 * @return SolrFacetSearchConfigSelectionStrategy
	 */
	protected SolrFacetSearchConfigSelectionStrategy getSolrFacetSearchConfigSelectionStrategy()
	{
		return solrFacetSearchConfigSelectionStrategy;
	}

	/**
	 * @param solrFacetSearchConfigSelectionStrategy
	 */
	@Required
	public void setSolrFacetSearchConfigSelectionStrategy(
			final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy)
	{
		this.solrFacetSearchConfigSelectionStrategy = solrFacetSearchConfigSelectionStrategy;
	}

	public SolrIndexSortService getSolrIndexSortService()
	{
		return solrIndexSortService;
	}

	@Required
	public void setSolrIndexSortService(final SolrIndexSortService solrIndexSortService)
	{
		this.solrIndexSortService = solrIndexSortService;
	}
}
