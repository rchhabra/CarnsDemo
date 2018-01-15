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

package de.hybris.platform.solrfacetsearch.config.factories.impl;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Map;


public class DefaultTravelFlexibleSearchQuerySpecFactory extends DefaultFlexibleSearchQuerySpecFactory
{
	protected static final String ACTIVE = "active";

	@Override
	protected void populateRuntimeParameters(final IndexedTypeFlexibleSearchQuery indexTypeFlexibleSearchQueryData,
			final IndexedType indexedType, final FacetSearchConfig facetSearchConfig) throws SolrServiceException
	{
		super.populateRuntimeParameters(indexTypeFlexibleSearchQueryData, indexedType, facetSearchConfig);
		final Map<String, Object> parameters = indexTypeFlexibleSearchQueryData.getParameters();
		if (indexTypeFlexibleSearchQueryData.isActive())
		{
			parameters.put(ACTIVE, Boolean.TRUE);
		}

	}
}
