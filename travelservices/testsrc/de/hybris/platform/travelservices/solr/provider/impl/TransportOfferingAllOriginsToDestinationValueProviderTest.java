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
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

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
public class TransportOfferingAllOriginsToDestinationValueProviderTest
{
	@InjectMocks
	TransportOfferingAllOriginsToDestinationValueProvider transportOfferingAllOriginsToDestinationValueProvider;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Test
	public void testGetFieldValues() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final TransportOfferingModel model = new TransportOfferingModel();

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		transportFacilityModel.setCode("CDG");
		travelSectorModel.setDestination(transportFacilityModel);

		final Collection<TravelRouteModel> travelRouteModels=new ArrayList<>(1);
		final TransportFacilityModel transportFacilityModelDest = new TransportFacilityModel();
		transportFacilityModelDest.setCode("CDG");
		final TravelRouteModel travelRouteModel=new TravelRouteModel();
		travelRouteModel.setDestination(transportFacilityModelDest);

		final TransportFacilityModel transportFacilityModelOrig = new TransportFacilityModel();
		final LocationModel location = new LocationModel();
		location.setCode("London");
		transportFacilityModelOrig.setLocation(location);
		transportFacilityModelOrig.setCode("LTN");
		travelRouteModel.setOrigin(transportFacilityModelOrig);

		travelRouteModels.add(travelRouteModel);
		travelSectorModel.setTravelRoute(travelRouteModels);

		//---->
		model.setTravelSector(travelSectorModel);

		final Collection<String> fieldNames = new HashSet<String>();
		fieldNames.add("autosuggest");
		fieldNames.add("spellcheck");

		Mockito.when(
				transportOfferingAllOriginsToDestinationValueProvider.getFieldNameProvider().getFieldNames(indexedProperty, null))
				.thenReturn(fieldNames);

		final Collection<FieldValue> collectionFieldValues = transportOfferingAllOriginsToDestinationValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);

	}

	@Test
	public void testGetFieldValuesModelIsNotTransportOfferingModel() throws FieldValueProviderException
	{
		final IndexConfig indexConfig = null;
		final IndexedProperty indexedProperty = null;
		final Object model = new Object();
		final Collection<FieldValue> collectionFieldValues = transportOfferingAllOriginsToDestinationValueProvider
				.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(collectionFieldValues);
		Assert.assertTrue(collectionFieldValues.isEmpty());

	}

}
