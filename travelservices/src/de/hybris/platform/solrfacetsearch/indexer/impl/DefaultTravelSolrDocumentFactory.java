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

package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.TravelSolrDocumentFactory;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.ValueResolver;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.solr.provider.DateBasedValueResolver;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;


/**
 * Default implementation of TravelSolrDocumentFactory
 */
public class DefaultTravelSolrDocumentFactory extends DefaultSolrDocumentFactory implements TravelSolrDocumentFactory
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelSolrDocumentFactory.class);

	@Override
	public SolrInputDocument createInputDocument(final ItemModel model, final IndexConfig indexConfig,
			final IndexedType indexedType, final Date documentDate) throws FieldValueProviderException
	{
		validateCommonRequiredParameters(model, indexConfig, indexedType);

		final IndexerBatchContext batchContext = getIndexerBatchContextFactory().getContext();
		final SolrInputDocument doc = new SolrInputDocument();
		final DefaultSolrInputDocument wrappedDoc = createWrappedDocument(batchContext, doc);

		wrappedDoc.startDocument();


		doc.addField("indexOperationId", batchContext.getIndexOperationId());

		addCommonFields(doc, batchContext, model, documentDate);
		addIndexedPropertyFields(wrappedDoc, batchContext, model, documentDate);
		addIndexedTypeFields(wrappedDoc, batchContext, model);

		wrappedDoc.endDocument();

		return doc;
	}

	@Override
	public SolrInputDocument createInputDocument(final ItemModel model, final IndexConfig indexConfig,
			final IndexedType indexedType, final Collection<IndexedProperty> indexedProperties, final Date documentDate)
			throws FieldValueProviderException
	{
		validateCommonRequiredParameters(model, indexConfig, indexedType);

		final IndexerBatchContext batchContext = getIndexerBatchContextFactory().getContext();
		final Set<String> indexedFields = getIndexedFields(batchContext);
		final SolrInputDocument doc = new SolrInputDocument();
		final DefaultSolrInputDocument wrappedDoc = createWrappedDocumentForPartialUpdates(batchContext, doc, indexedFields);

		wrappedDoc.startDocument();

		addCommonFields(doc, batchContext, model, documentDate);
		addIndexedPropertyFields(wrappedDoc, batchContext, model, documentDate);

		wrappedDoc.endDocument();

		return doc;
	}

	/**
	 * Add common fields.
	 *
	 * @param document
	 * 		the document
	 * @param batchContext
	 * 		the batch context
	 * @param model
	 * 		the model
	 * @param documentDate
	 * 		the document date
	 */
	protected void addCommonFields(final SolrInputDocument document, final IndexerBatchContext batchContext, final ItemModel model,
			final Date documentDate)
	{
		final FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();
		final IndexedType indexedType = batchContext.getIndexedType();

		final IdentityProvider<ItemModel> identityProvider = getIdentityProvider(indexedType);
		String id = identityProvider.getIdentifier(facetSearchConfig.getIndexConfig(), model);

		final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		id += "/" + dateFormat.format(documentDate);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Using SolrInputDocument id [" + id + "]");
		}

		document.addField("id", id);
		document.addField("pk", Long.valueOf(model.getPk().getLongValue()));

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForClass(model.getClass());
		if (Objects.equals(Boolean.TRUE, composedType.getCatalogItemType()))
		{
			final AttributeDescriptorModel catalogAttDesc = composedType.getCatalogVersionAttribute();
			final CatalogVersionModel catalogVersion = (CatalogVersionModel) getModelService()
					.getAttributeValue(model, catalogAttDesc.getQualifier());
			document.addField("catalogId", catalogVersion.getCatalog().getId());
			document.addField("catalogVersion", catalogVersion.getVersion());
		}
	}

	/**
	 * Add indexed property fields.
	 *
	 * @param document
	 * 		the document
	 * @param batchContext
	 * 		the batch context
	 * @param model
	 * 		the model
	 * @param documentDate
	 * 		the document date
	 * @throws FieldValueProviderException
	 * 		the field value provider exception
	 */
	protected void addIndexedPropertyFields(final InputDocument document, final IndexerBatchContext batchContext, final ItemModel model,
			final Date documentDate) throws FieldValueProviderException
	{
		final Map<String, Collection<IndexedProperty>> valueProviders = resolveValueProviders(batchContext);

		for (final Map.Entry<String, Collection<IndexedProperty>> entry : valueProviders.entrySet())
		{
			final String valueProviderId = entry.getKey();
			final Collection<IndexedProperty> indexedProperties = entry.getValue();

			final Object valueProvider = getValueProviderSelectionStrategy().getValueProvider(valueProviderId);

			if (valueProvider instanceof FieldValueProvider)
			{
				addIndexedPropertyFieldsForOldApi(document, batchContext, model, indexedProperties, valueProviderId,
						(FieldValueProvider) valueProvider);
			}
			else if (valueProvider instanceof ValueResolver)
			{
				addIndexedPropertyFieldsForNewApi(document, batchContext, model, indexedProperties, valueProviderId,
						(ValueResolver) valueProvider);
			}
			else if (valueProvider instanceof DateBasedValueResolver)
			{
				addIndexedPropertyFieldsForNewDateBasedApi(document, batchContext, model, indexedProperties, valueProviderId,
						(DateBasedValueResolver) valueProvider, documentDate);
			}
			else
			{
				throw new FieldValueProviderException("Value provider is not of an expected type: " + valueProviderId);
			}
		}
	}

	/**
	 * Add indexed property fields for new date based api.
	 *
	 * @param document
	 * 		the document
	 * @param batchContext
	 * 		the batch context
	 * @param model
	 * 		the model
	 * @param indexedProperties
	 * 		the indexed properties
	 * @param valueProviderId
	 * 		the value provider id
	 * @param valueProvider
	 * 		the value provider
	 * @param documentDate
	 * 		the document date
	 * @throws FieldValueProviderException
	 * 		the field value provider exception
	 */
	protected void addIndexedPropertyFieldsForNewDateBasedApi(final InputDocument document, final IndexerBatchContext
			batchContext,
			final ItemModel model, final Collection<IndexedProperty> indexedProperties, final String valueProviderId,
			final DateBasedValueResolver<ItemModel> valueProvider, final Date documentDate) throws FieldValueProviderException
	{
		final FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();

		try
		{
			valueProvider.resolve(document, batchContext, indexedProperties, model, documentDate);
		}
		catch (FieldValueProviderException | RuntimeException e)
		{
			final List<String> indexedPropertiesNames = indexedProperties.stream().map(IndexedProperty::getName)
					.collect(Collectors.toList());

			final String message = "Failed to resolve values for item with PK: " + model.getPk() + ", by resolver: " +
					valueProviderId + ", for properties: " + indexedPropertiesNames + " for date: " + documentDate + ", reason: " + e
					.getMessage();
			handleError(facetSearchConfig.getIndexConfig(), message, e);
		}
	}
}
