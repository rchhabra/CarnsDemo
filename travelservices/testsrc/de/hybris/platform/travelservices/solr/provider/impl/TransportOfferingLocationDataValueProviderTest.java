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
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingLocationDataValueProviderTest
{
	@InjectMocks
	TransportOfferingLocationDataValueProvider transportOfferingLocationDataValueProvider;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Test
	public void testGetFieldValuesModelIsNotTransportOfferingModel() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final Object model = new Object();
		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);
		Assert.assertTrue(collectionFieldValues.isEmpty());

	}

	@Test
	public void testGetFieldValuesWithTravelSectorNull() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();
		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);
		Assert.assertTrue(collectionFieldValues.isEmpty());

	}

	@Test
	public void testGetFieldValuesWithLocationOptionORIGIN() throws FieldValueProviderException
	{
		transportOfferingLocationDataValueProvider.setLocationOption("ORIGIN");
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final Collection<TravelRouteModel> travelRouteModels = new ArrayList<>(1);
		final TravelRouteModel travelRouteModel = new TravelRouteModel();

		final TransportFacilityModel transportFacilityModelDest = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelDest.setCode("CDG");

		final TransportFacilityModel transportFacilityModelOrig = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelOrig.setCode("LTN");
		final LocationModel locationCityOrig = Mockito.mock(LocationModel.class);
		Mockito.when(locationCityOrig.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(locationCityOrig.getCode()).thenReturn("LONDON");
		Mockito.when(locationCityOrig.getName()).thenReturn("London");
		Mockito.when(transportFacilityModelOrig.getLocation()).thenReturn(locationCityOrig);
		Mockito.when(transportFacilityModelOrig.getName()).thenReturn("Luton Airport");
		travelRouteModel.setOrigin(transportFacilityModelOrig);

		travelSectorModel.setDestination(transportFacilityModelDest);
		travelSectorModel.setOrigin(transportFacilityModelOrig);

		travelRouteModels.add(travelRouteModel);
		travelSectorModel.setTravelRoute(travelRouteModels);

		//---->
		model.setTravelSector(travelSectorModel);


		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(transportOfferingLocationDataValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesWithLocationOptionDESTINATION() throws FieldValueProviderException
	{
		transportOfferingLocationDataValueProvider.setLocationOption("DESTINATION");
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final Collection<TravelRouteModel> travelRouteModels = new ArrayList<>(1);
		final TravelRouteModel travelRouteModel = new TravelRouteModel();

		final TransportFacilityModel transportFacilityModelDest = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelDest.setCode("CDG");
		final LocationModel locationCityDest = Mockito.mock(LocationModel.class);
		Mockito.when(locationCityDest.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(locationCityDest.getCode()).thenReturn("PARIS");
		Mockito.when(locationCityDest.getName()).thenReturn("Paris");
		Mockito.when(transportFacilityModelDest.getLocation()).thenReturn(locationCityDest);
		Mockito.when(transportFacilityModelDest.getName()).thenReturn(" Paris Charles De Gaulle Airport");
		travelRouteModel.setDestination(transportFacilityModelDest);

		final TransportFacilityModel transportFacilityModelOrig = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelOrig.setCode("LTN");

		travelSectorModel.setDestination(transportFacilityModelDest);
		travelSectorModel.setOrigin(transportFacilityModelOrig);

		travelRouteModels.add(travelRouteModel);
		travelSectorModel.setTravelRoute(travelRouteModels);

		//---->
		model.setTravelSector(travelSectorModel);


		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(transportOfferingLocationDataValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesWithLocationTypeIsNotCity() throws FieldValueProviderException
	{
		transportOfferingLocationDataValueProvider.setLocationOption("ORIGIN");
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final Collection<TravelRouteModel> travelRouteModels = new ArrayList<>(1);
		final TravelRouteModel travelRouteModel = new TravelRouteModel();

		final TransportFacilityModel transportFacilityModelDest = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelDest.setCode("CDG");

		final TransportFacilityModel transportFacilityModelOrig = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelOrig.setCode("LTN");
		final LocationModel locationAirportOrig = Mockito.mock(LocationModel.class);
		Mockito.when(locationAirportOrig.getLocationType()).thenReturn(LocationType.AIRPORTGROUP);
		final List<LocationModel> superLocationModelList = new ArrayList<LocationModel>(1);

		final LocationModel locationCityOrig = Mockito.mock(LocationModel.class);
		superLocationModelList.add(locationCityOrig);

		Mockito.when(locationAirportOrig.getSuperlocations()).thenReturn(superLocationModelList);
		Mockito.when(locationCityOrig.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(locationCityOrig.getCode()).thenReturn("LONDON");
		Mockito.when(locationCityOrig.getName()).thenReturn("London");
		Mockito.when(transportFacilityModelOrig.getLocation()).thenReturn(locationAirportOrig);
		Mockito.when(transportFacilityModelOrig.getName()).thenReturn("Luton Airport");
		travelRouteModel.setOrigin(transportFacilityModelOrig);

		travelSectorModel.setDestination(transportFacilityModelDest);
		travelSectorModel.setOrigin(transportFacilityModelOrig);

		travelRouteModels.add(travelRouteModel);
		travelSectorModel.setTravelRoute(travelRouteModels);

		//---->
		model.setTravelSector(travelSectorModel);


		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(transportOfferingLocationDataValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesWithLocationTypeIsCity() throws FieldValueProviderException
	{
		transportOfferingLocationDataValueProvider.setLocationOption("ORIGIN");
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final Collection<TravelRouteModel> travelRouteModels = new ArrayList<>(1);
		final TravelRouteModel travelRouteModel = new TravelRouteModel();

		final TransportFacilityModel transportFacilityModelDest = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelDest.setCode("CDG");

		final TransportFacilityModel transportFacilityModelOrig = Mockito.mock(TransportFacilityModel.class);
		transportFacilityModelOrig.setCode("LTN");
		final LocationModel locationCityOrig = Mockito.mock(LocationModel.class);
		Mockito.when(locationCityOrig.getLocationType()).thenReturn(LocationType.CITY);

		final List<LocationModel> superLocationModelList = new ArrayList<LocationModel>(1);
		final LocationModel locationCountryOrig = Mockito.mock(LocationModel.class);
		superLocationModelList.add(locationCountryOrig);
		Mockito.when(locationCountryOrig.getLocationType()).thenReturn(LocationType.COUNTRY);
		Mockito.when(locationCountryOrig.getCode()).thenReturn("UK");
		Mockito.when(locationCountryOrig.getName()).thenReturn("United Kingdom");

		Mockito.when(locationCityOrig.getSuperlocations()).thenReturn(superLocationModelList);
		Mockito.when(locationCityOrig.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(locationCityOrig.getCode()).thenReturn("LONDON");
		Mockito.when(locationCityOrig.getName()).thenReturn("London");
		Mockito.when(transportFacilityModelOrig.getLocation()).thenReturn(locationCityOrig);
		Mockito.when(transportFacilityModelOrig.getName()).thenReturn("Luton Airport");
		travelRouteModel.setOrigin(transportFacilityModelOrig);

		travelSectorModel.setDestination(transportFacilityModelDest);
		travelSectorModel.setOrigin(transportFacilityModelOrig);

		travelRouteModels.add(travelRouteModel);
		travelSectorModel.setTravelRoute(travelRouteModels);

		//---->
		model.setTravelSector(travelSectorModel);


		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(transportOfferingLocationDataValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingLocationDataValueProvider.getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}
}
