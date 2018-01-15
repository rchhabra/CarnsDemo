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

import de.hybris.platform.commerceservices.model.solrsearch.config.SolrSortFieldModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSortPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Populator responsible for populating the currentSort in the target element and the orderSort in the searchQuery. If
 * no sort order is specified, the default ones are taken from the facetSearchConfig. If no default ones are specified,
 * the last sort used in the searchQueryData is used, and as a final fall-back the first available sort is used.
 *
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_TYPE>
 * @param <INDEXED_PROPERTY_TYPE>
 */
public class TravelSearchSortPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE>
		extends SearchSortPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort>>
{

	@Override
	public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort> target)
	{
		final IndexedType commerceIndexedType = target.getIndexedType();
		final FacetSearchConfig facetSearchConfig = (FacetSearchConfig) target.getFacetSearchConfig();
		final Collection<String> defaultSortOrders = facetSearchConfig.getSearchConfig().getDefaultSortOrder();
		final List<IndexedTypeSort> sorts = getFilteredSorts(target.getIndexedType());

		// Try to get the sort from the pageableData
		if (target.getPageableData() != null && StringUtils.isNotEmpty(target.getPageableData().getSort()))
		{
			final IndexedTypeSort currentSortApplied = commerceIndexedType.getSortsByCode().get(target.getPageableData().getSort());
			target.setCurrentSort(currentSortApplied);
			addSortsInSearchQuery(target.getSearchQuery(), currentSortApplied);
		}

		// Fall-back to the last sort used in the searchQueryData
		if (target.getCurrentSort() == null && StringUtils.isNotEmpty(target.getSearchQueryData().getSort()))
		{
			final IndexedTypeSort currentSortApplied = commerceIndexedType.getSortsByCode()
					.get(target.getSearchQueryData().getSort());
			if (Objects.nonNull(currentSortApplied))
			{
				target.setCurrentSort(currentSortApplied);
				addSortsInSearchQuery(target.getSearchQuery(), currentSortApplied);
				// adding order fields in search query from default sort orders
				if (CollectionUtils.isNotEmpty(defaultSortOrders))
				{
					for (final String defaultSort : defaultSortOrders)
					{
						final IndexedTypeSort indexedTypeSort = getIndexedTypeSort(defaultSort, sorts);
						if (!StringUtils.equals(currentSortApplied.getCode(), indexedTypeSort.getCode()))
						{
							addSortsInSearchQuery(target.getSearchQuery(), indexedTypeSort);
						}
					}
				}
			}
		}

		// Fall-back to the first defaultOrderSorts
		if (target.getCurrentSort() == null)
		{
			if (CollectionUtils.isNotEmpty(defaultSortOrders))
			{
				for (final String defaultSort : defaultSortOrders)
				{
					if (target.getCurrentSort() == null)
					{
						final IndexedTypeSort indexedTypeSort = getIndexedTypeSort(defaultSort, sorts);
						if (indexedTypeSort != null)
						{
							target.setCurrentSort(indexedTypeSort);
							addSortsInSearchQuery(target.getSearchQuery(), indexedTypeSort);
						}
					}
					else
					{
						final IndexedTypeSort indexedTypeSort = getIndexedTypeSort(defaultSort, sorts);
						addSortsInSearchQuery(target.getSearchQuery(), indexedTypeSort);
					}
				}
			}

		}

		// Fall-back to first available sort
		if (target.getCurrentSort() == null)
		{
			if (sorts != null && !sorts.isEmpty())
			{
				final IndexedTypeSort currentSortApplied = sorts.get(0);
				target.setCurrentSort(currentSortApplied);
				addSortsInSearchQuery(target.getSearchQuery(), currentSortApplied);
			}
		}
	}

	/**
	 * This method will return the IndexedTypeSort corresponding to the specified fieldName
	 *
	 * @param fieldName
	 *           the fieldName to be used to get the corresponding IndexedTypeSort
	 * @param sorts
	 *           the list of IndexedTypeSort
	 *
	 * @return the indexedTypeSort corresponding to the specified fieldName, null if no correspondence is found
	 */
	protected IndexedTypeSort getIndexedTypeSort(final String fieldName, final List<IndexedTypeSort> sorts)
	{
		for (final IndexedTypeSort indexedTypeSort : sorts)
		{
			if (indexedTypeSort.getSort().getFields().stream()
					.filter(field -> StringUtils.equalsIgnoreCase(fieldName, field.getFieldName())).findAny().isPresent())
			{
				return indexedTypeSort;
			}
		}

		return null;
	}

	/**
	 * Adds the sorts in search query.
	 *
	 * @param searchQuery
	 *           the search query
	 * @param indexedTypeSort
	 *           the indexed type sort
	 */
	protected void addSortsInSearchQuery(final SearchQuery searchQuery, final IndexedTypeSort indexedTypeSort)
	{
		if (Objects.isNull(indexedTypeSort) || Objects.isNull(indexedTypeSort.getSort())
				|| CollectionUtils.isEmpty(indexedTypeSort.getSort().getFields()))
		{
			return;
		}

		for (final SolrSortFieldModel sortFieldModel : indexedTypeSort.getSort().getFields())
		{
			if (sortFieldModel.isAscending())
			{
				searchQuery.addSort(sortFieldModel.getFieldName(), SortOrder.ASCENDING);
			}
			else
			{
				searchQuery.addSort(sortFieldModel.getFieldName(), SortOrder.DESCENDING);
			}
		}
	}
}
