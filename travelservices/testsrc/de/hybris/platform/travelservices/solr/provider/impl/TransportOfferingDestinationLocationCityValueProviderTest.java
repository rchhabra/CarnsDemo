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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.ActivityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.impl.DefaultTransportFacilityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Junit test suite{link TransportOfferingDestinationLocationCityValueProvider}
 */
@SuppressWarnings("deprecation")
@UnitTest
public class TransportOfferingDestinationLocationCityValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private final TransportOfferingLocationCityValueProvider transportOfferingLocationCityValueProvider = new TransportOfferingLocationCityValueProvider();

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private TransportOfferingModel transportOffering;

	@Mock
	private LocationModel location;

	@Mock
	private TravelSectorModel sector;

	@Mock
	private TransportFacilityModel destination;

	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private LocationModel secondLevelLocation;

	private static final String TEST_DESTINATIONLOCATION_CITY_PROP = "destinationLocationCity";

	private static final String TEST_DESTINATION_LOCATION_CITY_EN = "destinationLocationCity_en_string";

	private static final String TEST_DESTINATION_LOCATION_CITY_DE = "destinationLocationCity_de_string";

	private static final String TEST_LOCATION_OPTION = "DESTINATION";


	@Override
	protected String getPropertyName()
	{
		return TEST_DESTINATIONLOCATION_CITY_PROP;
	}

	@Override
	@Before
	public void configure()
	{
		transportOfferingLocationCityValueProvider.setTransportFacilityService(new DefaultTransportFacilityService());
		setPropertyFieldValueProvider(transportOfferingLocationCityValueProvider);
		configureBase();

		((TransportOfferingLocationCityValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		((TransportOfferingLocationCityValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((TransportOfferingLocationCityValueProvider) getPropertyFieldValueProvider()).setLocationOption(TEST_LOCATION_OPTION);
	}

	/**
	 * @throws FieldValueProviderException
	 */
	@Test
	public void testReturnValueWhenTransportOfferingDestinationLocationHasLocationCity() throws FieldValueProviderException
	{
		Mockito.when(transportOffering.getTravelSector()).thenReturn(sector);
		Mockito.when(sector.getDestination()).thenReturn(destination);
		Mockito.when(destination.getLocation()).thenReturn(location);
		Mockito.when(location.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(location.getName(Matchers.any(Locale.class))).thenReturn("LONDON");
		Mockito.when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.TRUE);
		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_EN_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_EN));
		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_DE_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_DE));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, transportOffering);

		assertNotNull(result);
		assertEquals(2, result.size());

		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase("LONDON"));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_DESTINATION_LOCATION_CITY_EN)
					|| val.getFieldName().equalsIgnoreCase(TEST_DESTINATION_LOCATION_CITY_DE));
		}
	}

	/**
	 * @throws FieldValueProviderException
	 */

	@Test
	public void testShouldReturnEmptyValueWhenTransportOfferingDestinationLocationIsNotCity() throws FieldValueProviderException
	{

		Mockito.when(transportOffering.getTravelSector()).thenReturn(sector);
		Mockito.when(sector.getDestination()).thenReturn(destination);
		Mockito.when(destination.getLocation()).thenReturn(location);

		Mockito.when(location.getLocationType()).thenReturn(LocationType.COUNTRY);
		Mockito.when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.TRUE);

		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_EN_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_EN));
		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_DE_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_DE));
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, transportOffering);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	/**
	 * @throws FieldValueProviderException
	 */
	@Test
	public void testShouldReturnValueWhenCityIsNotAtFirstLevelInDestinationTransportFacility() throws FieldValueProviderException
	{

		Mockito.when(transportOffering.getTravelSector()).thenReturn(sector);
		Mockito.when(sector.getDestination()).thenReturn(destination);
		Mockito.when(destination.getLocation()).thenReturn(location);

		Mockito.when(location.getLocationType()).thenReturn(LocationType.COUNTRY);
		Mockito.when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.TRUE);

		final List<LocationModel> superLocations = new ArrayList<LocationModel>();
		superLocations.add(secondLevelLocation);

		Mockito.when(location.getSuperlocations()).thenReturn(superLocations);
		Mockito.when(secondLevelLocation.getLocationType()).thenReturn(LocationType.CITY);
		Mockito.when(secondLevelLocation.getName(Matchers.any(Locale.class))).thenReturn("PARIS");

		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_EN_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_EN));
		Mockito.when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Mockito.eq(TEST_DE_LANG_CODE)))
				.thenReturn(singletonList(TEST_DESTINATION_LOCATION_CITY_DE));
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, transportOffering);

		assertNotNull(result);
		assertEquals(2, result.size());

		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase("PARIS"));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_DESTINATION_LOCATION_CITY_EN)
					|| val.getFieldName().equalsIgnoreCase(TEST_DESTINATION_LOCATION_CITY_DE));
		}
		;
	}


	/**
	 * @throws FieldValueProviderException
	 */
	@Test
	public void notInstanceOfTransportOfferingModelTest() throws FieldValueProviderException
	{

		final ActivityModel model = new ActivityModel();
		final Collection<FieldValue> fieldValues = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(null, null,
				model);

		Assert.assertNotNull(fieldValues);
		Assert.assertTrue(fieldValues.isEmpty());
	}
}
