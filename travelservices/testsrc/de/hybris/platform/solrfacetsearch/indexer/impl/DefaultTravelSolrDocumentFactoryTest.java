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

package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContextFactory;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.ValueProviderSelectionStrategy;
import de.hybris.platform.solrfacetsearch.provider.ValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ItemIdentityProvider;
import de.hybris.platform.travelservices.solr.provider.impl.AddressValueResolver;
import de.hybris.platform.travelservices.solr.provider.impl.DateOfStayValueResolver;
import de.hybris.platform.travelservices.solr.provider.impl.TransportOfferingLocationCountryValueProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test cases for {@link DefaultTravelSolrDocumentFactory}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelSolrDocumentFactoryTest
{

	@Mock
	private IndexerBatchContextFactory indexerBatchContextFactory;

	@Mock
	private ModelService modelService;

	@Mock
	private ValueProviderSelectionStrategy valueProviderSelectionStrategy;

	@Mock
	DefaultSolrPartialUpdateInputDocument defaultSolrPartialUpdateInputDocument;
	@Mock
	DateOfStayValueResolver dateOfStayValueResolver;

	@Mock
	DefaultSolrInputDocument defaultSolrInputDocument;

	@Mock
	TransportOfferingLocationCountryValueProvider fieldValueProvider;

	@Mock
	AddressValueResolver valueResolver;

	private DefaultTravelSolrDocumentFactory defaultTravelSolrDocumentFactory;

	final PK pkDemo = PK.fromLong(0001l);
	final ItemModel itemModel = new ItemModel()
	{
		@Override
		public PK getPk()
		{
			return pkDemo;
		}
	};

	@Before
	public void setUp() throws FieldValueProviderException
	{
		final ItemIdentityProvider itemIdentityProvider = new ItemIdentityProvider()
		{
			@Override
			public String getIdentifier(final IndexConfig arg0, final ItemModel arg1)
			{
				return "TEST_ID";
			}
		};

		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		final CatalogModel catalog = new CatalogModel();
		catalog.setId("TEST_CATALOG_ID");
		catalogVersionModel.setCatalog(catalog);
		catalogVersionModel.setVersion("TEST_CATALOG_VERSION");
		Mockito.when(modelService.getAttributeValue(Matchers.any(), Matchers.any())).thenReturn(catalogVersionModel);
		final AttributeDescriptorModel catalogAttDesc = new AttributeDescriptorModel();
		catalogAttDesc.setAttributeType(new TypeModel());

		final ComposedTypeModel composedType = new ComposedTypeModel();
		composedType.setCatalogItemType(Boolean.TRUE);
		composedType.setCatalogVersionAttribute(catalogAttDesc);
		final IndexerBatchContext batchContext = new DefaultIndexerBatchContext()
		{
			@Override
			public FacetSearchConfig getFacetSearchConfig()
			{
				return new FacetSearchConfig();
			}

			@Override
			public IndexedType getIndexedType()
			{
				return new IndexedType();
			}
		};


		defaultTravelSolrDocumentFactory = new DefaultTravelSolrDocumentFactory()
		{
			@Override
			public void validateCommonRequiredParameters(final ItemModel item, final IndexConfig indexConfig,
					final IndexedType indexedType)
			{
				return;
			}

			@Override
			public DefaultSolrInputDocument createWrappedDocument(final IndexerBatchContext batchContext,
					final SolrInputDocument delegate)
			{
				return defaultSolrPartialUpdateInputDocument;
			}

			@Override
			public IdentityProvider<ItemModel> getIdentityProvider(final IndexedType indexedType)
			{
				return itemIdentityProvider;
			}

			@Override
			public TypeService getTypeService()
			{
				return new DefaultTypeService()
				{
					@Override
					public ComposedTypeModel getComposedTypeForClass(final Class arg0)
					{
						return composedType;
					}
				};
			}

			@Override
			public Map<String, Collection<IndexedProperty>> resolveValueProviders(final IndexerBatchContext batchContext)
			{
				final IndexedProperty indexedProperty = new IndexedProperty();
				indexedProperty.setName("TEST_PROPERTY");
				final Map<String, Collection<IndexedProperty>> valueProviders = new HashMap<>();
				valueProviders.put("TEST_VALUE_PROVIDER_KEY", Collections.singletonList(indexedProperty));

				return valueProviders;
			}

			@Override
			public void handleError(final IndexConfig indexConfig, final String message, final Exception error)
					throws FieldValueProviderException
			{
				return;
			}

			@Override
			public ModelService getModelService()
			{
				return modelService;
			}

			@Override
			public DefaultSolrInputDocument createWrappedDocumentForPartialUpdates(final IndexerBatchContext batchContext,
					final SolrInputDocument delegate, final Set<String> indexedPropertiesFields)
			{
				return defaultSolrInputDocument;
			}

			@Override
			public Set<String> getIndexedFields(final IndexerBatchContext batchContext) throws FieldValueProviderException
			{
				return Collections.emptySet();
			}

			@Override
			public void addIndexedPropertyFieldsForOldApi(final InputDocument document, final IndexerBatchContext batchContext,
					final ItemModel model, final Collection<IndexedProperty> indexedProperties, final String valueProviderId,
					final FieldValueProvider valueProvider) throws FieldValueProviderException
			{
				return;
			}

			@Override
			public void addIndexedPropertyFieldsForNewApi(final InputDocument document, final IndexerBatchContext batchContext,
					final ItemModel model, final Collection<IndexedProperty> indexedProperties, final String valueProviderId,
					final ValueResolver<ItemModel> valueProvider) throws FieldValueProviderException
			{
				return;
			}
		};
		Mockito.when(indexerBatchContextFactory.getContext()).thenReturn(batchContext);
		defaultTravelSolrDocumentFactory.setValueProviderSelectionStrategy(valueProviderSelectionStrategy);
		defaultTravelSolrDocumentFactory.setIndexerBatchContextFactory(indexerBatchContextFactory);
	}

	@Test
	public void testForFieldValueProvider() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(fieldValueProvider);
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(), new Date());
	}

	@Test
	public void testForValueResolver() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(valueResolver);
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(), new Date());

	}

	@Test
	public void testForDateBasedValueResolver() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(dateOfStayValueResolver);
		Mockito.doNothing().when(dateOfStayValueResolver).resolve(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any());
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(), new Date());
	}

	@Test
	public void testForDateBasedValueResolverAndFieldValueProviderException() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(dateOfStayValueResolver);
		Mockito.doThrow(new FieldValueProviderException("Exception")).when(dateOfStayValueResolver).resolve(Matchers.any(),
				Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any());
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(), new Date());
	}

	@Test(expected = FieldValueProviderException.class)
	public void testForFieldValueProviderException() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(new Object());
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(), new Date());
	}

	@Test
	public void testForIndexedProperties() throws FieldValueProviderException
	{
		Mockito.when(valueProviderSelectionStrategy.getValueProvider(Matchers.anyString())).thenReturn(dateOfStayValueResolver);
		Mockito.doNothing().when(dateOfStayValueResolver).resolve(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any());
		defaultTravelSolrDocumentFactory.createInputDocument(itemModel, new IndexConfig(), new IndexedType(),
				Collections.emptyList(), new Date());
	}

}
