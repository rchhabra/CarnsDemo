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
 */

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.travelservices.dao.SolrIndexSortDao;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;
import de.hybris.platform.travelservices.services.SolrIndexSortService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SolrIndexSortService}
 */
public class DefaultSolrIndexSortService implements SolrIndexSortService
{
	private SolrIndexSortDao solrIndexSortDao;

	@Override
	public SolrIndexedTypeDefaultSortOrderMappingModel getDefaultSortOrderMapping(final String indexedType)
	{
		return getSolrIndexSortDao().findIndexOrderSortMapping(indexedType);
	}

	/**
	 * Gets solr index sort dao.
	 *
	 * @return the solr index sort dao
	 */
	protected SolrIndexSortDao getSolrIndexSortDao()
	{
		return solrIndexSortDao;
	}

	/**
	 * Sets solr index sort dao.
	 *
	 * @param solrIndexSortDao
	 * 		the solr index sort dao
	 */
	@Required
	public void setSolrIndexSortDao(final SolrIndexSortDao solrIndexSortDao)
	{
		this.solrIndexSortDao = solrIndexSortDao;
	}
}
