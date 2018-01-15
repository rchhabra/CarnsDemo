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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;


/**
 * This interface provides functionality related to {@link SolrIndexedTypeDefaultSortOrderMappingModel}
 */
public interface SolrIndexSortDao extends Dao
{
	/**
	 * Finds default order sort mapping for given indexedType
	 *
	 * @param indexedType
	 * 		the indexed type
	 * @return solr indexed type default sort order mapping model
	 */
	SolrIndexedTypeDefaultSortOrderMappingModel findIndexOrderSortMapping(String indexedType);
}
