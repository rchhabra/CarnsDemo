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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.Qualifier;
import de.hybris.platform.solrfacetsearch.provider.QualifierProvider;
import de.hybris.platform.solrfacetsearch.provider.QualifierProviderAware;
import de.hybris.platform.solrfacetsearch.provider.ValueFilter;
import de.hybris.platform.travelservices.solr.provider.DateBasedValueResolver;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract implementation of DateBasedValueResolver
 *
 * @param <T>
 * @param <MDATA>
 * @param <QDATA>
 */
public abstract class AbstractDateBasedValueResolver<T extends ItemModel, MDATA, QDATA>
		implements DateBasedValueResolver<T>, QualifierProviderAware
{
	private SessionService sessionService;
	private QualifierProvider qualifierProvider;
	private Collection<ValueFilter> valueFilters;

	@Override
	public void resolve(final InputDocument document, final IndexerBatchContext batchContext,
			final Collection<IndexedProperty> indexedProperties, final T model, final Date documentDate)
			throws FieldValueProviderException
	{
		ServicesUtil.validateParameterNotNull("model", "model instance is null");

		try
		{
			createLocalSessionContext();
			final Object data = loadData(batchContext, indexedProperties, model);
			final ValueResolverContext resolverContext = new ValueResolverContext();
			resolverContext.setData(data);

			for (final IndexedProperty indexedProperty : indexedProperties)
			{
				if (!getQualifierProvider().canApply(indexedProperty))
				{
					addFieldValues(document, batchContext, indexedProperty, model, resolverContext, documentDate);
				}
			}

			final FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();
			final IndexedType indexedType = batchContext.getIndexedType();
			final Collection<Qualifier> qualifiers = getQualifierProvider().getAvailableQualifiers(facetSearchConfig, indexedType);

			for (final Qualifier qualifier : qualifiers)
			{
				getQualifierProvider().applyQualifier(qualifier);
				final String fieldQualifier = qualifier.toFieldQualifier();
				final QDATA qualifierData = loadQualifierData(batchContext, indexedProperties, model, qualifier, documentDate);
				resolverContext.setQualifier(qualifier);
				resolverContext.setFieldQualifier(fieldQualifier);
				resolverContext.setQualifierData(qualifierData);

				for (final IndexedProperty indexedProperty : indexedProperties)
				{
					if (getQualifierProvider().canApply(indexedProperty))
					{
						addFieldValues(document, batchContext, indexedProperty, model, resolverContext, documentDate);
					}
				}
			}
		}
		finally
		{
			removeLocalSessionContext();
		}
	}

	protected MDATA loadData(final IndexerBatchContext batchContext, final Collection<IndexedProperty> indexedProperties,
			final T model) throws FieldValueProviderException
	{
		return null;
	}

	protected QDATA loadQualifierData(final IndexerBatchContext batchContext, final Collection<IndexedProperty> indexedProperties,
			final T model, final Qualifier qualifier, final Date documentDate) throws FieldValueProviderException
	{
		return null;
	}


	protected abstract void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final T model, final ValueResolverContext<MDATA, QDATA> valueResolverContext,
			final Date documentDate) throws FieldValueProviderException;

	protected Object filterFieldValue(final IndexerBatchContext batchContext, final IndexedProperty indexedProperty, final Object value)
	{
		Object resultValue = value;
		if ((this.valueFilters != null) && (!this.valueFilters.isEmpty()))
		{
			for (final ValueFilter valueFilter : this.valueFilters)
			{
				resultValue = valueFilter.doFilter(batchContext, indexedProperty, resultValue);
			}
		}

		return resultValue;
	}

	protected boolean addFieldValue(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final Object value, final String qualifier) throws FieldValueProviderException
	{
		final boolean isString = value instanceof String;
		if ((isString) && (StringUtils.isBlank((String) value)))
		{
			return false;
		}

		document.addField(indexedProperty, value, qualifier);

		return true;
	}

	protected boolean filterAndAddFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final Object value, final String qualifier) throws FieldValueProviderException
	{
		Object resolvedValue = value;
		boolean hasValue = false;

		if (resolvedValue != null)
		{
			resolvedValue = filterFieldValue(batchContext, indexedProperty, resolvedValue);

			if ((resolvedValue instanceof Collection))
			{
				final Collection<Object> values = (Collection) resolvedValue;

				for (final Object singleValue : values)
				{
					hasValue |= addFieldValue(document, batchContext, indexedProperty, singleValue, qualifier);
				}
			}
			else
			{
				hasValue = addFieldValue(document, batchContext, indexedProperty, resolvedValue, qualifier);
			}
		}

		return hasValue;
	}

	protected void createLocalSessionContext()
	{
		final Session session = this.sessionService.getCurrentSession();
		final JaloSession jaloSession = (JaloSession) this.sessionService.getRawSession(session);
		jaloSession.createLocalSessionContext();
	}

	protected void removeLocalSessionContext()
	{
		final Session session = this.sessionService.getCurrentSession();
		final JaloSession jaloSession = (JaloSession) this.sessionService.getRawSession(session);
		jaloSession.removeLocalSessionContext();
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public QualifierProvider getQualifierProvider()
	{
		return qualifierProvider;
	}

	@Required
	@Override
	public void setQualifierProvider(final QualifierProvider qualifierProvider)
	{
		this.qualifierProvider = qualifierProvider;
	}

	public Collection<ValueFilter> getValueFilters()
	{
		return valueFilters;
	}

	public void setValueFilters(final Collection<ValueFilter> valueFilters)
	{
		this.valueFilters = valueFilters;
	}

	protected static final class ValueResolverContext<T, U>
	{
		private T data;
		private U qualifierData;
		private Qualifier qualifier;
		private String fieldQualifier;

		public T getData()
		{
			return (T) this.data;
		}

		public void setData(final T data)
		{
			this.data = data;
		}

		public U getQualifierData()
		{
			return (U) this.qualifierData;
		}

		public void setQualifierData(final U qualifierData)
		{
			this.qualifierData = qualifierData;
		}

		public Qualifier getQualifier()
		{
			return this.qualifier;
		}

		public void setQualifier(final Qualifier qualifier)
		{
			this.qualifier = qualifier;
		}

		public String getFieldQualifier()
		{
			return this.fieldQualifier;
		}

		public void setFieldQualifier(final String fieldQualifier)
		{
			this.fieldQualifier = fieldQualifier;
		}
	}
}
