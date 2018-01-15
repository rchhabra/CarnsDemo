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

import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the sorts of the target element with the visible sorts taken from solr and with the
 * custom sorts taken from a property in project.properties
 *
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_TYPE>
 * @param <INDEXED_PROPERTY_TYPE>
 * @param <SEARCH_RESULT_TYPE>
 * @param <ITEM>
 */
public class TravelSearchResponseSortsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE,
		SEARCH_RESULT_TYPE, ITEM>
		extends
		SearchResponseSortsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_RESULT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
				IndexedTypeSort, SEARCH_RESULT_TYPE>, SearchPageData<ITEM>>
{

	private static final String CUSTOM_SORT_DELIMITER = ",";
	private ConfigurationService configurationService;

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
					IndexedTypeSort, SEARCH_RESULT_TYPE> source,
			final SearchPageData<ITEM> target)
	{
		final List<SortData> solrSorts = this.buildSorts(source);
		final List<SortData> customSorts = buildCustomSorts(source);

		solrSorts.addAll(customSorts);
		target.setSorts(solrSorts);
	}

	/**
	 * This method returns a list of the sorts taken from solr, filtering out the not visible ones
	 */
	@Override
	protected List<SortData> buildSorts(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
					IndexedTypeSort, SEARCH_RESULT_TYPE> source)
	{
		final List<SortData> result = new ArrayList<>();

		final IndexedType indexedType = source.getRequest().getSearchQuery().getIndexedType();

		if (indexedType != null)
		{
			final IndexedTypeSort currentSort = source.getRequest().getCurrentSort();
			final String currentSortCode = currentSort != null ? currentSort.getCode() : null;

			final List<IndexedTypeSort> sorts = indexedType.getSorts();
			if (CollectionUtils.isNotEmpty(sorts))
			{
				sorts.stream().filter(sort -> BooleanUtils.isNotFalse(sort.getSort().getVisible()))
						.forEach(sort -> addSortData(result, currentSortCode, sort));
			}
		}

		return result;
	}

	/**
	 * This method returns the list of SortData corresponding to the custom sorts. The SortData has the string taken from
	 * the property as code, while the name will be empty
	 *
	 * @param source
	 * @return the list of SortData corresponding to the custom sorts
	 */
	protected List<SortData> buildCustomSorts(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
					IndexedTypeSort, SEARCH_RESULT_TYPE> source)
	{

		final List<SortData> customSorts = new ArrayList<>();

		final List<String> customSortCodes = getCustomSorts();
		for (final String sortCode : customSortCodes)
		{
			final SortData sortData = createSortData();
			sortData.setCode(sortCode);

			if (source.getRequest().getPageableData() != null && StringUtils
					.isNotBlank(source.getRequest().getPageableData().getSort()) && sortCode
					.equals(source.getRequest().getPageableData().getSort()))
			{
				sortData.setSelected(true);
			}

			customSorts.add(sortData);
		}

		return customSorts;
	}

	/**
	 * This method will return a list of Strings taken from the
	 * {@link TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS} property, where the delimiter between the
	 * codes is a comma ","
	 *
	 * @return list of String
	 */
	protected List<String> getCustomSorts()
	{
		final String customSortCodeList = getConfigurationService().getConfiguration()
				.getString(TravelservicesConstants.ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS);

		if (StringUtils.isBlank(customSortCodeList))
		{
			return Collections.emptyList();
		}

		return Arrays.asList(customSortCodeList.split(CUSTOM_SORT_DELIMITER));
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
