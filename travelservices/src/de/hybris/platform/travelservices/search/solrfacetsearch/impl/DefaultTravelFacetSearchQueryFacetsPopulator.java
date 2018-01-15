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

package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryFacetsPopulator;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Required;


/*
 * The class populates SolrQuery with Facet Query.
 */
public class DefaultTravelFacetSearchQueryFacetsPopulator extends FacetSearchQueryFacetsPopulator
{
	private Integer defaultFacetValuesMaxLimit;
	private Integer defaultFacetValuesMinCount;

	@Override
	public void populate(final SearchQueryConverterData source, final SolrQuery target) throws ConversionException
	{
		super.populate(source, target);
		target.setFacetMinCount(getDefaultFacetValuesMinCount());
		target.setFacetLimit(getDefaultFacetValuesMaxLimit());
	}

	/**
	 * @return the defaultFacetValuesMaxLimit
	 */
	protected Integer getDefaultFacetValuesMaxLimit()
	{
		return defaultFacetValuesMaxLimit;
	}

	/**
	 * @param defaultFacetValuesMaxLimit
	 *           the defaultFacetValuesMaxLimit to set
	 */
	@Required
	public void setDefaultFacetValuesMaxLimit(final Integer defaultFacetValuesMaxLimit)
	{
		this.defaultFacetValuesMaxLimit = defaultFacetValuesMaxLimit;
	}

	/**
	 * @return the defaultFacetValuesMinCount
	 */
	protected Integer getDefaultFacetValuesMinCount()
	{
		return defaultFacetValuesMinCount;
	}

	/**
	 * @param defaultFacetValuesMinCount
	 *           the defaultFacetValuesMinCount to set
	 */
	@Required
	public void setDefaultFacetValuesMinCount(final Integer defaultFacetValuesMinCount)
	{
		this.defaultFacetValuesMinCount = defaultFacetValuesMinCount;
	}
}
