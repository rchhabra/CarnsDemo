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

package de.hybris.platform.solrfacetsearch.indexer;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

import java.util.Collection;
import java.util.Date;

import org.apache.solr.common.SolrInputDocument;


/**
 * Extension of SolrDocumentFactory which supports date based indexing of items
 */
public interface TravelSolrDocumentFactory
{
	/**
	 * Extension of createInputDocument method which supports date based full index of items
	 *
	 * @param model
	 * 		the model
	 * @param indexConfig
	 * 		the index config
	 * @param indexedType
	 * 		the indexed type
	 * @param documentDate
	 * 		the document date
	 * @return solr input document
	 * @throws FieldValueProviderException
	 * 		the field value provider exception
	 */
	SolrInputDocument createInputDocument(ItemModel model, IndexConfig indexConfig, IndexedType indexedType,
			Date documentDate) throws FieldValueProviderException;

	/**
	 * Extension of createInputDocument method which supports date based update index of items
	 *
	 * @param model
	 * 		the model
	 * @param indexConfig
	 * 		the index config
	 * @param indexedType
	 * 		the indexed type
	 * @param indexedProperties
	 * 		the indexed properties
	 * @param documentDate
	 * 		the document date
	 * @return solr input document
	 * @throws FieldValueProviderException
	 * 		the field value provider exception
	 */
	SolrInputDocument createInputDocument(ItemModel model, IndexConfig indexConfig, IndexedType indexedType,
			Collection<IndexedProperty> indexedProperties, Date documentDate) throws FieldValueProviderException;
}
