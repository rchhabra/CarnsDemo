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

package de.hybris.platform.travelservices.storelocator.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.TimeZoneResponseData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.storelocator.location.Location;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelGoogleMapsServiceWrapperTest
{
	@InjectMocks
	private TravelGoogleMapsServiceWrapper travelGoogleMapsServiceWrapper;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;

	@Test
	public void testTimeZoneOffset()
	{
		final Location address=Mockito.mock(Location.class);
		final TravelGoogleMapTools mapTools = Mockito.mock(TravelGoogleMapTools.class);
		travelGoogleMapsServiceWrapper.setGoogleMapTools(mapTools);
		final TimeZoneResponseData timeZoneResponseDataExp = new TimeZoneResponseData();
		BDDMockito.given(configurationService.getConfiguration().getString("google.api.timezone.url"))
				.willReturn("https://maps.googleapis.com/maps/api/timezone/");
		BDDMockito.given(mapTools.timeZoneOffset(address)).willReturn(timeZoneResponseDataExp);

		final TimeZoneResponseData timeZoneResponseData = travelGoogleMapsServiceWrapper.timeZoneOffset(address);
		Assert.assertEquals(timeZoneResponseDataExp, timeZoneResponseData);

		travelGoogleMapsServiceWrapper.setGoogleMapTools(mapTools);
	}

}
