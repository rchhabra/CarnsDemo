package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.jalo.accommodation.MarketingRatePlanInfo;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;
import de.hybris.platform.travelservices.search.solrfacetsearch.AccommodationFacetSearchService;
import de.hybris.platform.travelservices.services.SolrIndexSortService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class AccommodationSearchSolrQueryPopulator<INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FacetSearchConfig, IndexedType,
				INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE>>
{
	private CommonI18NService commonI18NService;
	private BaseSiteService baseSiteService;
	private CatalogVersionService catalogVersionService;
	private AccommodationFacetSearchService accommodationFacetSearchService;
	private FacetSearchConfigService facetSearchConfigService;
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
	private SolrIndexSortService solrIndexSortService;

	@Override
	public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FacetSearchConfig, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE>
					target)
	{
		// Setup the SolrSearchRequest
		target.setSearchQueryData(source.getSearchQueryData());
		target.setPageableData(source.getPageableData());

		final Collection<CatalogVersionModel> catalogVersions = getSessionProductCatalogVersions();
		if (catalogVersions == null || catalogVersions.isEmpty())
		{
			throw new ConversionException("Missing solr facet search indexed catalog versions");
		}

		target.setCatalogVersions(new ArrayList<CatalogVersionModel>(catalogVersions));
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

		final String searchType = source.getSearchQueryData().getSearchType();

		// Create the solr search query for the config and type (this sets-up the default page size and sort order)
		final SearchQuery searchQuery = createSearchQuery(target.getFacetSearchConfig(), target.getIndexedType(),
				source.getSearchQueryData().getFreeTextSearch(), searchType);
		searchQuery.setCatalogVersions(target.getCatalogVersions());
		searchQuery.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode());
		searchQuery.setLanguage(getCommonI18NService().getCurrentLanguage().getIsocode());

		// enable spell checker
		searchQuery.setEnableSpellcheck(false);

		if (TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION.equalsIgnoreCase(searchType))
		{
			searchQuery.addGroupCommand(TravelservicesConstants.SEARCH_KEY_LOCATION_CODES);
			searchQuery.setGroupFacets(false);
		}
		else if (TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY.equalsIgnoreCase(searchType))
		{
			searchQuery.addGroupCommand(TravelservicesConstants.SEARCH_KEY_PROPERTY_CODE);
			searchQuery.setGroupFacets(false);
		}
		else if (TravelservicesConstants.SOLR_SEARCH_TYPE_SPATIAL.equalsIgnoreCase(searchType))
		{
			final Optional<SolrSearchQueryTermData> positionOptional = source.getSearchQueryData().getFilterTerms().stream().filter(
					filterTerm -> StringUtils.equalsIgnoreCase(filterTerm.getKey(), TravelservicesConstants.SOLR_FIELD_POSITION))
					.findFirst();

			final Optional<SolrSearchQueryTermData> radiusOptional = source.getSearchQueryData().getFilterTerms().stream()
					.filter(filterTerm -> StringUtils.equalsIgnoreCase(filterTerm.getKey(), TravelservicesConstants
							.SOLR_FIELD_RADIUS))
					.findFirst();

			if (positionOptional.isPresent() && radiusOptional.isPresent())
			{
				searchQuery.addFilterQuery("{!geofilt sfield=" + TravelservicesConstants.SEARCH_KEY_LATLON + "}");
				searchQuery.addRawParam("pt", positionOptional.get().getValue());
				searchQuery.addRawParam("d", radiusOptional.get().getValue());
			}
		}

		target.setSearchQuery(searchQuery);
	}


	/**
	 * Resolves suitable {@link FacetSearchConfig} for the query based on the configured strategy bean.<br>
	 *
	 * @return {@link FacetSearchConfig} that is converted from {@link SolrFacetSearchConfigModel}
	 * @throws NoValidSolrConfigException , FacetConfigServiceException
	 */
	protected FacetSearchConfig getFacetSearchConfig() throws NoValidSolrConfigException, FacetConfigServiceException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = getSolrFacetSearchConfigSelectionStrategy()
				.getCurrentSolrFacetSearchConfig();

		final FacetSearchConfig configuration = getFacetSearchConfigService()
				.getConfiguration(solrFacetSearchConfigModel.getName());

		final SolrIndexedTypeDefaultSortOrderMappingModel defaultSortOrderMapping = getSolrIndexSortService()
				.getDefaultSortOrderMapping(MarketingRatePlanInfo.class.getSimpleName());

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
				if (StringUtils.equalsIgnoreCase(indexedType.getCode(), MarketingRatePlanInfo.class.getSimpleName()))
				{
					return indexedType;
				}
			}
		}

		// No indexed types
		return null;
	}

	protected SearchQuery createSearchQuery(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final String freeTextSearch, final String searchType)
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
			searchQuery = getAccommodationFacetSearchService()
					.createFreeTextSearchQuery(facetSearchConfig, indexedType, freeTextSearch, searchType);
		}

		return searchQuery;
	}

	/**
	 * Get all the session catalog versions that belong to product catalogs of the current site.
	 *
	 * @return the list of session catalog versions
	 */
	protected Collection<CatalogVersionModel> getSessionProductCatalogVersions()
	{
		final BaseSiteModel currentSite = getBaseSiteService().getCurrentBaseSite();
		final List<CatalogModel> productCatalogs = getBaseSiteService().getProductCatalogs(currentSite);

		final Collection<CatalogVersionModel> sessionCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();

		final Collection<CatalogVersionModel> result = new ArrayList<CatalogVersionModel>();
		for (final CatalogVersionModel sessionCatalogVersion : sessionCatalogVersions)
		{
			if (productCatalogs.contains(sessionCatalogVersion.getCatalog()))
			{
				result.add(sessionCatalogVersion);
			}
		}
		return result;
	}

	/**
	 * @return CommonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
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
	 * @return AccommodationFacetSearchService
	 */
	protected AccommodationFacetSearchService getAccommodationFacetSearchService()
	{
		return accommodationFacetSearchService;
	}

	/**
	 * @param accommodationFacetSearchService
	 */
	@Required
	public void setAccommodationFacetSearchService(final AccommodationFacetSearchService accommodationFacetSearchService)
	{
		this.accommodationFacetSearchService = accommodationFacetSearchService;
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


	/**
	 * @return baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}


	/**
	 * @param baseSiteService
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}


	/**
	 * @return catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}


	/**
	 * @param catalogVersionService
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
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

