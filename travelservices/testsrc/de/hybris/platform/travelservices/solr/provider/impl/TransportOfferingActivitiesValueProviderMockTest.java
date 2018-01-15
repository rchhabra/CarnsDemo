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
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.travelservices.model.travel.ActivityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * 
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingActivitiesValueProviderMockTest
{

	@InjectMocks
	private TransportOfferingActivitiesValueProvider valueProvider;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock

	private TransportOfferingModel transportOffering;
	@Mock
	private TravelSectorModel sector;

	@Mock
	private TransportFacilityModel destination;

	@Mock
	private LocationModel location;

	@Before
	public void setup()
	{

		Mockito.when(transportOffering.getTravelSector()).thenReturn(sector);
		Mockito.when(sector.getDestination()).thenReturn(destination);
		Mockito.when(destination.getLocation()).thenReturn(location);

		final Collection<String> indexedProperty = new ArrayList<>();
		indexedProperty.add("activity_string_mv");

		Mockito.when(fieldNameProvider.getFieldNames(Mockito.any(IndexedProperty.class), Mockito.anyString()))
				.thenReturn(indexedProperty);
	}

	@Test
	public void notInstanceOfTransportOfferingModelTest() throws FieldValueProviderException
	{

		final ActivityModel model = new ActivityModel();
		final Collection<FieldValue> fieldValues = valueProvider.getFieldValues(null, null, model);

		Assert.assertNotNull(fieldValues);
		Assert.assertTrue(fieldValues.isEmpty());
	}

	@Test
	public void buildFieldValuesFromLocationActivitesTest() throws FieldValueProviderException
	{

		final Collection<ActivityModel> activities = getActivities();

		Mockito.when(location.getActivity()).thenReturn(activities);

		final Collection<FieldValue> fieldValues = valueProvider.getFieldValues(null, null, transportOffering);

		Assert.assertNotNull(fieldValues);
		Assert.assertTrue(!fieldValues.isEmpty());
		Assert.assertEquals(activities.size(), fieldValues.size());
	}

	@Test
	public void buildFieldValuesFromTransportFacilityActivitesTest() throws FieldValueProviderException
	{

		final Collection<ActivityModel> activities = getActivities();

		Mockito.when(destination.getActivity()).thenReturn(activities);

		final Collection<FieldValue> fieldValues = valueProvider.getFieldValues(null, null, transportOffering);

		Assert.assertNotNull(fieldValues);
		Assert.assertTrue(!fieldValues.isEmpty());
		Assert.assertEquals(activities.size(), fieldValues.size());
	}

	@Test
	public void getListOfFieldValuesTestWithoutDuplicates() throws FieldValueProviderException
	{

		final ActivityModel newActivity = new ActivityModel();
		newActivity.setCode("bigBen");
		newActivity.setName("Big Ben", Locale.ENGLISH);

		final Collection<ActivityModel> transportFacilityActivities = getActivities();
		transportFacilityActivities.add(newActivity);

		final Collection<ActivityModel> locationActivities = getActivities();

		Mockito.when(location.getActivity()).thenReturn(locationActivities);

		Mockito.when(destination.getActivity()).thenReturn(transportFacilityActivities);

		final Collection<FieldValue> fieldValues = valueProvider.getFieldValues(null, null, transportOffering);

		Assert.assertNotNull(fieldValues);
		Assert.assertTrue(!fieldValues.isEmpty());
		Assert.assertEquals(4, fieldValues.size());
	}

	private Collection<ActivityModel> getActivities()
	{

		final Collection<ActivityModel> activities = new ArrayList<>();

		final ActivityModel activityA = new ActivityModel();
		activityA.setCode("britishMuseum");
		activityA.setName("British Museum", Locale.ENGLISH);

		final ActivityModel activityB = new ActivityModel();
		activityB.setCode("nationalGallery");
		activityB.setName("National Gallery", Locale.ENGLISH);

		final ActivityModel activityC = new ActivityModel();
		activityC.setCode("towerOfLondon");
		activityC.setName("Tower of London", Locale.ENGLISH);

		activities.add(activityA);
		activities.add(activityB);
		activities.add(activityC);

		return activities;
	}
}
