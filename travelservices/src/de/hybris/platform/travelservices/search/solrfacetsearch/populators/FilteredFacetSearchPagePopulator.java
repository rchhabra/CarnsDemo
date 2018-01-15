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

import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.search.facetdata.FilteredFacetSearchPageData;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class FilteredFacetSearchPagePopulator<QUERY, STATE>
		implements Populator<FilteredFacetSearchPageData<QUERY>, FilteredFacetSearchPageData<STATE>>
{
	private Converter<QUERY, STATE> searchQueryConverter;

	private Converter<FacetValueData<QUERY>, FacetValueData<STATE>> facetValueConverter;

	@Override
	public void populate(final FilteredFacetSearchPageData<QUERY> source, final FilteredFacetSearchPageData<STATE> target)
			throws ConversionException
	{
		target.setCode(source.getCode());
		target.setClearFacetQuery(getSearchQueryConverter().convert(source.getClearFacetQuery()));
		if (CollectionUtils.isNotEmpty(source.getValues()))
		{
			target.setValues(Converters.convertAll(source.getValues(), getFacetValueConverter()));
		}
	}

	/**
	 * @return the searchQueryConverter
	 */
	protected Converter<QUERY, STATE> getSearchQueryConverter()
	{
		return searchQueryConverter;
	}

	/**
	 * @param searchQueryConverter
	 *           the searchQueryConverter to set
	 */
	@Required
	public void setSearchQueryConverter(final Converter<QUERY, STATE> searchQueryConverter)
	{
		this.searchQueryConverter = searchQueryConverter;
	}

	/**
	 * @return the facetValueConverter
	 */
	protected Converter<FacetValueData<QUERY>, FacetValueData<STATE>> getFacetValueConverter()
	{
		return facetValueConverter;
	}

	/**
	 * @param facetValueConverter
	 *           the facetValueConverter to set
	 */
	@Required
	public void setFacetValueConverter(final Converter<FacetValueData<QUERY>, FacetValueData<STATE>> facetValueConverter)
	{
		this.facetValueConverter = facetValueConverter;
	}
}
