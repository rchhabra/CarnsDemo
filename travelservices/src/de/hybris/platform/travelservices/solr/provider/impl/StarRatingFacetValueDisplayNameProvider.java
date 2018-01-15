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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;


/**
 * Display Name provider for the starRating facet.
 */
public class StarRatingFacetValueDisplayNameProvider implements FacetValueDisplayNameProvider, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_RATING = "0";
	private static final String MAX_RATING = "5+";

	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty indexedProperty, final String facetValue)
	{
		if (StringUtils.isEmpty(facetValue))
		{
			return "accommodation.offering." + DEFAULT_RATING + ".star";
		}

		final Integer star = Integer.parseInt(facetValue);

		if (star > 0 && star <= 5)
		{
			return "accommodation.offering." + facetValue + ".star";
		}

		if (star > 5)
		{
			return "accommodation.offering." + MAX_RATING + ".star";
		}

		return "accommodation.offering." + DEFAULT_RATING + ".star";
	}


}
