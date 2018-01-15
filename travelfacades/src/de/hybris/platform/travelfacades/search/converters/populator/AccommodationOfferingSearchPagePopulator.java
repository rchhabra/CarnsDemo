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

package de.hybris.platform.travelfacades.search.converters.populator;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;
import de.hybris.platform.travelservices.search.facetdata.FilteredFacetSearchPageData;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation offering search page populator.
 *
 * @param <QUERY>
 * 		the type parameter
 * @param <STATE>
 * 		the type parameter
 * @param <RESULT>
 * 		the type parameter
 * @param <ITEM>
 * 		the type parameter
 */
public class AccommodationOfferingSearchPagePopulator<QUERY, STATE, RESULT, ITEM extends AccommodationOfferingDayRateData>
		implements Populator<AccommodationOfferingSearchPageData<QUERY, RESULT>, AccommodationOfferingSearchPageData<STATE, ITEM>>
{
	private Converter<RESULT, ITEM> searchResultAccommodationOfferingConverter;
	private Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter;
	private Converter<FilteredFacetSearchPageData<QUERY>, FilteredFacetSearchPageData<STATE>> filteredFacetSearchPageConverter;
	private Converter<QUERY, STATE> searchStateConverter;

	@Override
	public void populate(final AccommodationOfferingSearchPageData<QUERY, RESULT> source,
			final AccommodationOfferingSearchPageData<STATE, ITEM> target)
	{
		target.setPagination(source.getPagination());
		target.setSorts(source.getSorts());
		target.setKeywordRedirectUrl(source.getKeywordRedirectUrl());

		target.setCurrentQuery(getSearchStateConverter().convert(source.getCurrentQuery()));

		if (CollectionUtils.isNotEmpty(source.getFacets()))
		{
			target.setFacets(Converters.convertAll(source.getFacets(), getFacetConverter()));
		}
		if (CollectionUtils.isNotEmpty(source.getFilteredFacets()))
		{
			target.setFilteredFacets(Converters.convertAll(source.getFilteredFacets(), getFilteredFacetSearchPageConverter()));
		}

		if (source.getResults() != null)
		{
			target.setResults(Converters.convertAll(source.getResults(), getSearchResultAccommodationOfferingConverter()));
		}
	}

	/**
	 * Gets search result accommodation offering converter.
	 *
	 * @return the search result accommodation offering converter
	 */
	protected Converter<RESULT, ITEM> getSearchResultAccommodationOfferingConverter()
	{
		return searchResultAccommodationOfferingConverter;
	}

	/**
	 * Sets search result accommodation offering converter.
	 *
	 * @param searchResultAccommodationOfferingConverter
	 * 		the search result accommodation offering converter
	 */
	@Required
	public void setSearchResultAccommodationOfferingConverter(
			final Converter<RESULT, ITEM> searchResultAccommodationOfferingConverter)
	{
		this.searchResultAccommodationOfferingConverter = searchResultAccommodationOfferingConverter;
	}

	/**
	 * Gets facet converter.
	 *
	 * @return the facetConverter
	 */
	protected Converter<FacetData<QUERY>, FacetData<STATE>> getFacetConverter()
	{
		return facetConverter;
	}

	/**
	 * Sets facet converter.
	 *
	 * @param facetConverter
	 * 		the facetConverter to set
	 */
	@Required
	public void setFacetConverter(final Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter)
	{
		this.facetConverter = facetConverter;
	}

	/**
	 * Gets filtered facet search page converter.
	 *
	 * @return the filteredFacetSearchPageConverter
	 */
	protected Converter<FilteredFacetSearchPageData<QUERY>, FilteredFacetSearchPageData<STATE>> getFilteredFacetSearchPageConverter()
	{
		return filteredFacetSearchPageConverter;
	}

	/**
	 * Sets filtered facet search page converter.
	 *
	 * @param filteredFacetSearchPageConverter
	 * 		the filteredFacetSearchPageConverter to set
	 */
	@Required
	public void setFilteredFacetSearchPageConverter(final Converter<FilteredFacetSearchPageData<QUERY>, FilteredFacetSearchPageData<STATE>> filteredFacetSearchPageConverter)
	{
		this.filteredFacetSearchPageConverter = filteredFacetSearchPageConverter;
	}

	/**
	 * Gets search state converter.
	 *
	 * @return the search state converter
	 */
	protected Converter<QUERY, STATE> getSearchStateConverter()
	{
		return searchStateConverter;
	}

	/**
	 * Sets search state converter.
	 *
	 * @param searchStateConverter
	 * 		the search state converter
	 */
	public void setSearchStateConverter(final Converter<QUERY, STATE> searchStateConverter)
	{
		this.searchStateConverter = searchStateConverter;
	}
}
