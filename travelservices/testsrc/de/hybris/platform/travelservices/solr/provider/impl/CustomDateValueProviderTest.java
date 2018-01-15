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
*/

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomDateValueProviderTest
{
	@InjectMocks
	CustomDateValueProvider customDateValueProvider;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Test
	public void testGetFieldValues() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();
		model.setDepartureTime(new Date());

		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(customDateValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null)).thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = customDateValueProvider.getFieldValues(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesModelIsNotTransportOfferingModel() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final Object model = new Object();
		final Collection<FieldValue> collectionFieldValues = customDateValueProvider.getFieldValues(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(collectionFieldValues);
		Assert.assertTrue(collectionFieldValues.isEmpty());

	}

}
