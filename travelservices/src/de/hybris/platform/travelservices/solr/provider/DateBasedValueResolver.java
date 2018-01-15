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

package de.hybris.platform.travelservices.solr.provider;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

import java.util.Collection;
import java.util.Date;


/**
 * Value Resolver which can resolve values based on date provided
 *
 * @param <T>
 * 		the type parameter
 */
public interface DateBasedValueResolver<T extends ItemModel>
{
	/**
	 * Resolves the indxed properties based on the date provided
	 *
	 * @param document
	 * 		the document
	 * @param batchContext
	 * 		the batch context
	 * @param indexedProperties
	 * 		the indexed properties
	 * @param model
	 * 		the model
	 * @param documentDate
	 * 		the document date
	 * @throws FieldValueProviderException
	 * 		the field value provider exception
	 */
	void resolve(InputDocument document, IndexerBatchContext batchContext, Collection<IndexedProperty> indexedProperties,
			T model, Date documentDate) throws FieldValueProviderException;

}
