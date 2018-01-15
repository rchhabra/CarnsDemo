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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.accommodation.data.ConfiguredAccommodationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.impl.DefaultConfiguredAccommodationFacade;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfiguredAccommodationFacadeTest
{
	@Mock
	private Converter<ConfiguredAccommodationModel, ConfiguredAccommodationData> configuredAccommodationConverter;

	@Mock
	private ConfiguredAccommodationModel accommodation;

	@Mock
	private AccommodationMapService accommodationMapService;

	@InjectMocks
	private final DefaultConfiguredAccommodationFacade defaultConfiguredAccommodationFacade = new DefaultConfiguredAccommodationFacade();

	@Test
	public void getAccommodationForAccommodationNoTest()
	{
		final String accommodationNo = "testAccommodationNo";
		Mockito.when(accommodationMapService.getAccommodation(accommodationNo)).thenReturn(accommodation);
		final ConfiguredAccommodationData configuredAccommodationData = new ConfiguredAccommodationData();
		Mockito.when(configuredAccommodationConverter.convert(accommodation)).thenReturn(configuredAccommodationData);
		final ConfiguredAccommodationData returnedConfiguredAccommodationData = defaultConfiguredAccommodationFacade
				.getAccommodation(accommodationNo);
		Assert.assertEquals(configuredAccommodationData, returnedConfiguredAccommodationData);
	}

	@Test
	public void getAccommodationForInvalidAccommodation()
	{
		final String accommodationNo = "testAccommodationNo";
		final ConfiguredAccommodationData returnedConfiguredAccommodationData = defaultConfiguredAccommodationFacade
				.getAccommodation(accommodationNo);
		Assert.assertNull(returnedConfiguredAccommodationData);
	}

	@Test
	public void setConfiguredAccommodationConverter()
	{
		defaultConfiguredAccommodationFacade.setConfiguredAccommodationConverter(configuredAccommodationConverter);
		Assert.assertEquals(configuredAccommodationConverter,
				defaultConfiguredAccommodationFacade.getConfiguredAccommodationConverter());
	}

	@Test
	public void setAccommodationMapService()
	{
		defaultConfiguredAccommodationFacade.setAccommodationMapService(accommodationMapService);
		Assert.assertEquals(accommodationMapService, defaultConfiguredAccommodationFacade.getAccommodationMapService());
	}

}
