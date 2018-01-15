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
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;

import java.util.ArrayList;
import java.util.Collection;
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
public class TransportOfferingLocationCountryValueProviderTest
{
	@InjectMocks
	TransportOfferingLocationCountryValueProvider transportOfferingLocationCountryValueProvider;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	I18NService i18nService;

	@Mock
	private TransportFacilityService transportFacilityService;

	@Test
	public void testGetFieldValuesWithOriginLocationOption() throws FieldValueProviderException
	{
		final String locationOption = "ORIGIN";
		transportOfferingLocationCountryValueProvider.setLocationOption(locationOption);

		final IndexConfig indexConfig = new IndexConfig();
		final Collection<LanguageModel> languages = new ArrayList<LanguageModel>();
		languages.add(new LanguageModel());
		indexConfig.setLanguages(languages);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setLocalized(true);

		final TransportOfferingModel model = new TransportOfferingModel();
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		travelSectorModel.setOrigin(transportFacilityModel);
		model.setTravelSector(travelSectorModel);

		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		Mockito.when(locationModel.getName(i18nService.getCurrentLocale())).thenReturn("en");

		Mockito.when(transportOfferingLocationCountryValueProvider.getTransportFacilityService().getCountry(transportFacilityModel))
				.thenReturn(locationModel);

		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");
		Mockito.when(transportOfferingLocationCountryValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationCountryValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesWithDestinationLocationOption() throws FieldValueProviderException
	{
		final String locationOption = "DESTINATION";
		transportOfferingLocationCountryValueProvider.setLocationOption(locationOption);

		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		travelSectorModel.setOrigin(transportFacilityModel);
		model.setTravelSector(travelSectorModel);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationCountryValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesWithNullLocationModel() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationCountryValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesModelIsNotTransportOfferingModel() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final Object model = new Object();
		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationCountryValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);
		Assert.assertTrue(collectionFieldValues.isEmpty());

	}



}
