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
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.location.Location;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelGoogleMapToolsTest
{
	@InjectMocks
	private TravelGoogleMapTools travelGoogleMapTools;

	@Mock
	private TimeZoneResponseGoogleResponseParser timeZoneResponseGoogleResponseParser;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TimeService timeService;

	final Location address = Mockito.mock(Location.class);
	final GPS gps = Mockito.mock(GPS.class);

	@Before
	public void setUp()
	{
		travelGoogleMapTools.setBaseUrl("https://maps.googleapis.com/maps/api/timezone/");
		BDDMockito.given(address.getGPS()).willReturn(gps);
		BDDMockito.given(timeService.getCurrentTime().getTime()).willReturn(new Date().getTime());

	}

	@Test
	public void testTimeZoneOffset() throws IOException
	{
		final TimeZoneResponseData timeZoneResponseData=new TimeZoneResponseData();
		timeZoneResponseData.setDstOffset(0d);
		timeZoneResponseData.setRawOffset(0d);
		BDDMockito.given(timeZoneResponseGoogleResponseParser.extractData(Matchers.any(ClientHttpResponse.class)))
				.willReturn(timeZoneResponseData);

		final TimeZoneResponseData timeZoneResponseDataAct = travelGoogleMapTools.timeZoneOffset(address);
		Assert.assertEquals(timeZoneResponseData, timeZoneResponseDataAct);

	}

	@Test(expected = GeoServiceWrapperException.class)
	public void testTimeZoneOffsetWithException() throws IOException
	{
		final TimeZoneResponseData timeZoneResponseData = new TimeZoneResponseData();
		timeZoneResponseData.setDstOffset(0d);
		BDDMockito.given(timeZoneResponseGoogleResponseParser.extractData(Matchers.any(ClientHttpResponse.class)))
				.willReturn(timeZoneResponseData);
		travelGoogleMapTools.timeZoneOffset(address);
	}

	@Test(expected = GeoServiceWrapperException.class)
	public void testTimeZoneOffsetWithException1() throws IOException
	{
		BDDMockito.given(timeZoneResponseGoogleResponseParser.extractData(Matchers.any(ClientHttpResponse.class)))
				.willThrow(new GeoLocatorException(""));
		travelGoogleMapTools.timeZoneOffset(address);
	}

	@Test(expected = GeoServiceWrapperException.class)
	public void testTimeZoneOffsetWithException2() throws IOException
	{
		BDDMockito.given(timeZoneResponseGoogleResponseParser.extractData(Matchers.any(ClientHttpResponse.class)))
				.willThrow(new ResourceAccessException(""));
		travelGoogleMapTools.timeZoneOffset(address);
	}
}
