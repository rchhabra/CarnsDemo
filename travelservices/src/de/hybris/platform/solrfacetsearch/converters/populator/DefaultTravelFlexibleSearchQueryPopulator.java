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

package de.hybris.platform.solrfacetsearch.converters.populator;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;


public class DefaultTravelFlexibleSearchQueryPopulator extends DefaultIndexedTypeFlexibleSearchQueryPopulator
{
	@Override
	public void populate(final SolrIndexerQueryModel source, final IndexedTypeFlexibleSearchQuery target)
			throws ConversionException
	{
		super.populate(source, target);
		target.setActive(source.isActive());
		target.setParameters(initializeFSQParameters(source.getSolrIndexerQueryParameters()));

	}
}
