/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;
import de.hybris.platform.travelservices.search.facetdata.FilteredFacetSearchPageData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate the facets on which the results have been filtered.
 */
public class TravelSearchResponseFilteredFacetsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>>
{
	private ConfigurationService configurationService;
	private static final String PROPERTY_DELIMITER = ",";

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final FacetSearchPageData<SolrSearchQueryData, ITEM> target)
	{
		final AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM> accommodationOfferingSearchPageData = (AccommodationOfferingSearchPageData<SolrSearchQueryData, ITEM>) target;

		accommodationOfferingSearchPageData.setFilteredFacets(buildFilteredFacets(target.getCurrentQuery(),
				source.getRequest().getIndexedPropertyValues(), source.getRequest().getSearchQuery()));

	}

	protected List<FilteredFacetSearchPageData<SolrSearchQueryData>> buildFilteredFacets(
			final SolrSearchQueryData currentSearchQuery,
			final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues, final SearchQuery searchQuery)
	{
		if (CollectionUtils.isEmpty(indexedPropertyValues))
		{
			return Collections.emptyList();
		}

		final List<FilteredFacetSearchPageData<SolrSearchQueryData>> selectedFacets = new ArrayList<>(1);

		final List<String> requiredFacetCodes = getRequiredFacetCodes();
		final List<IndexedPropertyValueData<IndexedProperty>> filteredIndexedPropertyValues = indexedPropertyValues.stream().filter(
				indexedPropertyValue -> !isValidProperty(indexedPropertyValue.getIndexedProperty().getName(), requiredFacetCodes))
				.collect(Collectors.toList());

		for (final IndexedPropertyValueData<IndexedProperty> indexedPropertyValue : filteredIndexedPropertyValues)
		{
			final IndexedProperty indexedProperty = indexedPropertyValue.getIndexedProperty();
			final String facetValueCode = indexedPropertyValue.getValue();
			String facetValueDisplayName = facetValueCode;
			final FacetValueDisplayNameProvider facetDisplayNameProvider = getFacetDisplayNameProvider(indexedProperty);
			if (facetDisplayNameProvider != null)
			{
				facetValueDisplayName = facetDisplayNameProvider.getDisplayName(searchQuery, indexedProperty, facetValueCode);
			}
			final String facetCode = indexedProperty.getName();
			final String facetDisplayName = StringUtils.isEmpty(indexedProperty.getDisplayName()) ? facetCode
					: indexedProperty.getDisplayName();
			final FacetValueData<SolrSearchQueryData> facetValueData = createFacetValueData();
			facetValueData.setCode(facetValueCode);
			facetValueData.setName(facetValueDisplayName);
			facetValueData.setQuery(refineQueryRemoveFacet(currentSearchQuery, facetCode, facetValueCode));
			final List<FilteredFacetSearchPageData<SolrSearchQueryData>> selectedFacet = selectedFacets.stream()
					.filter(sf -> StringUtils.equals(sf.getCode(), indexedProperty.getName())).collect(Collectors.toList());
			FilteredFacetSearchPageData<SolrSearchQueryData> facetData;
			if (CollectionUtils.isNotEmpty(selectedFacet))
			{
				facetData = selectedFacet.get(0);
				facetData.getValues().add(facetValueData);
			}
			else
			{
				facetData = createFacetData();
				facetData.setCode(indexedProperty.getName());
				facetData.setName(facetDisplayName);
				final List<FacetValueData<SolrSearchQueryData>> facetValues = new ArrayList<>(1);
				facetValues.add(facetValueData);
				facetData.setValues(facetValues);
				facetData.setClearFacetQuery(createClearFacetQueryData(currentSearchQuery, indexedProperty.getName()));
				selectedFacets.add(facetData);
			}
		}

		return selectedFacets;
	}

	/**
	 * Creates SolrSearchQueryData to clear all the selected facet values for the specific facet from the filter.
	 *
	 * @param searchQueryData
	 *           the instance to clone
	 *
	 * @param facetCode
	 *
	 * @return filteredFacetSearchPageData instance of FilteredFacetSearchPageData
	 */
	protected SolrSearchQueryData createClearFacetQueryData(final SolrSearchQueryData searchQueryData, final String facetCode)
	{
		final SolrSearchQueryData clearSelectedFacetQuery = cloneSearchQueryData(searchQueryData);
		final List<SolrSearchQueryTermData> filterTerms = new ArrayList<>(1);
		searchQueryData.getFilterTerms().stream()
				.filter(filterTerm -> !StringUtils.equalsIgnoreCase(filterTerm.getKey(), facetCode))
				.forEach(filterTerm -> filterTerms.add(filterTerm));
		clearSelectedFacetQuery.setFilterTerms(filterTerms);
		return clearSelectedFacetQuery;
	}

	/**
	 * Refines the query for the facet. The resulting query can be used to filter the records with all previous selected
	 * facets without the selected.
	 *
	 * @param searchQueryData
	 *           the instance to clone
	 * @param facetCode
	 * @param facetValue
	 * @return solrSearchQueryData
	 */
	protected SolrSearchQueryData refineQueryRemoveFacet(final SolrSearchQueryData searchQueryData, final String facetCode,
			final String facetValue)
	{
		final List<SolrSearchQueryTermData> filteredTerms = new ArrayList<>(1);
		searchQueryData.getFilterTerms().stream()
				.filter(filterTerm -> !(StringUtils.equalsIgnoreCase(filterTerm.getKey(), facetCode)
						&& StringUtils.equalsIgnoreCase(filterTerm.getValue(), facetValue)))
				.forEach(filterTerm -> filteredTerms.add(filterTerm));

		final SolrSearchQueryData solrSearchQueryData = cloneSearchQueryData(searchQueryData);
		solrSearchQueryData.setFilterTerms(filteredTerms);
		return solrSearchQueryData;
	}

	/**
	 * Shallow clone of the source SearchQueryData
	 *
	 * @param source
	 *           the instance to clone
	 * @return the shallow clone
	 */
	protected SolrSearchQueryData cloneSearchQueryData(final SolrSearchQueryData source)
	{
		final SolrSearchQueryData target = createSearchQueryData();
		target.setFreeTextSearch(source.getFreeTextSearch());
		target.setCategoryCode(source.getCategoryCode());
		target.setSort(source.getSort());
		target.setFilterTerms(source.getFilterTerms());
		return target;
	}

	/*
	 * Returns an instance of FacetDisplayNameProvider defined for Indexed Property
	 *
	 * @param property instance of IndexedProperty
	 *
	 * @return facetDisplayNameProvider
	 */
	protected FacetValueDisplayNameProvider getFacetDisplayNameProvider(final IndexedProperty property)
	{
		return StringUtils.isEmpty(property.getFacetDisplayNameProvider()) ? null
				: Registry.getApplicationContext().getBean(property.getFacetDisplayNameProvider(),
						FacetValueDisplayNameProvider.class);
	}

	/*
	 * Validate if the indexedPropertyCode is one of required properties for the search.
	 *
	 * @param indexedPropertyCode
	 *
	 * @return
	 */
	protected boolean isValidProperty(final String indexedPropertyCode, final List<String> requiredFacetCodes)
	{
		return requiredFacetCodes.contains(indexedPropertyCode);
	}

	/*
	 * Returns an instance of SolrSearchQueryData
	 */
	protected SolrSearchQueryData createSearchQueryData()
	{
		return new SolrSearchQueryData();
	}

	/*
	 * Returns an instance of FilteredFacetSearchPageData
	 */
	protected FilteredFacetSearchPageData<SolrSearchQueryData> createFacetData()
	{
		return new FilteredFacetSearchPageData<SolrSearchQueryData>();
	}

	/*
	 * Returns an instance of FacetValueData
	 */
	protected FacetValueData<SolrSearchQueryData> createFacetValueData()
	{
		return new FacetValueData<SolrSearchQueryData>();
	}

	/**
	 * This method will return a list of Strings taken from the
	 * {@link TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES} property, where the delimiter between the codes
	 * is a comma ","
	 *
	 * @return list of String
	 */
	protected List<String> getRequiredFacetCodes()
	{
		final String requiredFacetCodes = getConfigurationService().getConfiguration()
				.getString(TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES);

		if (StringUtils.isEmpty(requiredFacetCodes))
		{
			return Collections.emptyList();
		}

		return Arrays.asList(requiredFacetCodes.split(PROPERTY_DELIMITER));
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
