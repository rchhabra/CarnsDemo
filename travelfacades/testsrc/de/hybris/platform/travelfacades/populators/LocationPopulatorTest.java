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

package de.hybris.platform.travelfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for {@link LocationPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LocationPopulatorTest
{

	@InjectMocks
	private LocationPopulator locationPopulator;
	@Mock
	private Converter<MediaModel, ImageData> imageConverter;
	@Mock
	private LocaleProvider locProvider;

	@Test
	public void testLocationPopulate()
	{
		final LocationModel locationModel = new LocationModel()
		{

			@Override
			public String getCode()
			{
				return "CDG";
			}

			@Override
			public String getName()
			{
				return "Paris";
			}

		};
		final MediaModel mediaModel = new MediaModel();

		locationModel.setPicture(mediaModel);

		final ImageData imageData = new ImageData();
		given(imageConverter.convert(Matchers.any(MediaModel.class))).willReturn(imageData);
		final LocationData locationData = new LocationData();
		locationPopulator.populate(locationModel, locationData);
		Assert.assertEquals("Paris", locationData.getName());

	}

	@Test
	public void testNullLocationModel()
	{

		final LocationModel locationModel = new LocationModel()
		{

			@Override
			public String getCode()
			{
				return null;
			}

			@Override
			public String getName()
			{
				return null;
			}

		};
		final LocationData locationData = new LocationData();
		locationPopulator.populate(locationModel, locationData);
		Assert.assertEquals(null, locationData.getCode());
		Assert.assertEquals(null, locationData.getName());
	}
}
