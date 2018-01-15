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

package de.hybris.platform.travelfacades.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.impl.DefaultTravelImageFacade;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.services.TravelLocationService;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTravelImageFacade implementation
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelImageFacadeTest
{
	@InjectMocks
	DefaultTravelImageFacade defaultTravelImageFacade;
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private Converter<MediaModel, ImageData> imageConverter;
	@Mock
	private TravelLocationService travelLocationService;
	@Mock
	private TransportFacilityService transportFacilityService;
	@Mock
	private TravelCartService travelCartService;
	@Mock
	private AccommodationCommerceCartService accommodationCommerceCartService;

	private final String TEST_DESTINATION_LOCATION = "TEST_DESTINATION_LOCATION|TEST_DESTINATION_LOCATION|TEST_DESTINATION_LOCATION_NAME|TCA";
	@Test
	public void testGetImageForDestinationLocationForNull()
	{
		Assert.assertNull(defaultTravelImageFacade.getImageForDestinationLocation(StringUtils.EMPTY));
	}

	@Test
	public void testGetImageForDestinationLocationForInvalidLocationCode()
	{
		when(travelLocationService.getLocation("TCA")).thenReturn(null);
		Assert.assertNull(defaultTravelImageFacade.getImageForDestinationLocation(TEST_DESTINATION_LOCATION));

	}

	@Test
	public void testGetImageForDestinationLocationForLocationWithoutPicture()
	{
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		when(locationModel.getPicture()).thenReturn(null);
		when(travelLocationService.getLocation("TCA")).thenReturn(locationModel);
		Assert.assertNull(defaultTravelImageFacade.getImageForDestinationLocation(TEST_DESTINATION_LOCATION));
	}

	@Test
	public void testGetImageForDestinationLocationForLocationWithPicture()
	{
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		final MediaModel imageModel = Mockito.mock(MediaModel.class);
		when(locationModel.getPicture()).thenReturn(imageModel);
		when(imageConverter.convert(imageModel)).thenReturn(Mockito.mock(ImageData.class));
		when(travelLocationService.getLocation("TCA")).thenReturn(locationModel);
		Assert.assertNotNull(defaultTravelImageFacade.getImageForDestinationLocation(TEST_DESTINATION_LOCATION));
	}

	@Test
	public void testGetImageForAccommodationOfferingLocationWithInvalidCode()
	{
		when(accommodationOfferingService.getAccommodationOffering("TCA")).thenReturn(null);
		Assert.assertNull(defaultTravelImageFacade.getImageForAccommodationOfferingLocation(TEST_DESTINATION_LOCATION));

	}

	@Test
	public void testGetImageForAccommodationOfferingLocationWithoutPicture()
	{
		final AccommodationOfferingModel accommodationOfferingModel = Mockito.mock(AccommodationOfferingModel.class);
		when(accommodationOfferingService.getAccommodationOffering("TCA")).thenReturn(accommodationOfferingModel);
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		when(accommodationOfferingModel.getLocation()).thenReturn(locationModel);
		Assert.assertNull(defaultTravelImageFacade.getImageForAccommodationOfferingLocation(TEST_DESTINATION_LOCATION));

	}

	@Test
	public void testGetImageForAccommodationOfferingLocationWithPicture()
	{
		final AccommodationOfferingModel accommodationOfferingModel = Mockito.mock(AccommodationOfferingModel.class);
		when(accommodationOfferingService.getAccommodationOffering(TEST_DESTINATION_LOCATION))
				.thenReturn(accommodationOfferingModel);
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		when(locationModel.getLocationType()).thenReturn(LocationType.CITY);
		when(accommodationOfferingModel.getLocation()).thenReturn(locationModel);
		final MediaModel imageModel = Mockito.mock(MediaModel.class);
		when(locationModel.getPicture()).thenReturn(imageModel);
		when(imageConverter.convert(imageModel)).thenReturn(Mockito.mock(ImageData.class));
		Assert.assertNotNull(defaultTravelImageFacade.getImageForAccommodationOfferingLocation(TEST_DESTINATION_LOCATION));
	}

	@Test
	public void testGetImagesFromCartWithAccommodationOfferingCode()
	{
		when(accommodationCommerceCartService.getCurrentAccommodationOffering()).thenReturn("ao1");
		final AccommodationOfferingModel accommodationOfferingModel = Mockito.mock(AccommodationOfferingModel.class);
		when(accommodationOfferingService.getAccommodationOffering("ao1"))
				.thenReturn(accommodationOfferingModel);
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		when(locationModel.getLocationType()).thenReturn(LocationType.CITY);
		when(accommodationOfferingModel.getLocation()).thenReturn(locationModel);
		final MediaModel imageModel = Mockito.mock(MediaModel.class);
		when(locationModel.getPicture()).thenReturn(imageModel);
		when(imageConverter.convert(imageModel)).thenReturn(Mockito.mock(ImageData.class));
		Assert.assertNotNull(defaultTravelImageFacade.getImageFromCart());
	}

	@Test
	public void testGetImagesFromCartWithoutAccommodationOfferingCode()
	{
		when(accommodationCommerceCartService.getCurrentAccommodationOffering())
				.thenReturn(org.apache.commons.lang3.StringUtils.EMPTY);
		when(travelCartService.getCurrentDestination()).thenReturn("airport1");
		final TransportFacilityModel transportFacilityModel = Mockito.mock(TransportFacilityModel.class);
		when(transportFacilityService.getTransportFacility("airport1")).thenReturn(transportFacilityModel);
		final LocationModel locationModel = Mockito.mock(LocationModel.class);
		when(transportFacilityModel.getLocation()).thenReturn(locationModel);
		when(locationModel.getLocationType()).thenReturn(LocationType.CITY);
		final MediaModel imageModel = Mockito.mock(MediaModel.class);
		when(locationModel.getPicture()).thenReturn(imageModel);
		when(imageConverter.convert(imageModel)).thenReturn(Mockito.mock(ImageData.class));
		Assert.assertNotNull(defaultTravelImageFacade.getImageFromCart());
	}

}
