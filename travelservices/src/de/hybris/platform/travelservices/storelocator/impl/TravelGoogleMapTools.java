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

package de.hybris.platform.travelservices.storelocator.impl;

import de.hybris.platform.commercefacades.storelocator.data.TimeZoneResponseData;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.impl.GoogleMapTools;
import de.hybris.platform.storelocator.location.Location;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class TravelGoogleMapTools extends GoogleMapTools
{

	private static final String SEPARATOR = ",";
	private String baseUrl;
	private TimeZoneResponseGoogleResponseParser timeZoneResponseGoogleResponseParser;
	private TimeService timeService;

	public TimeZoneResponseData timeZoneOffset(final Location address) throws GeoServiceWrapperException
	{
		Preconditions.checkNotNull(address, "Geocoding failed! Address cannot be null");
		Preconditions.checkNotNull(address.getGPS(), "Geocoding failed! GPS cannot be null");

		return timeZoneOffset(address.getGPS());
	}

	public TimeZoneResponseData timeZoneOffset(final GPS gps) throws GeoServiceWrapperException
	{
		try
		{
			final RestTemplate restTemplate = new RestTemplate();
			final String urlAddress = baseUrl + "xml?location=" + getGoogleQuery(gps) + "&timestamp="
					+ (getTimeService().getCurrentTime().getTime() / 1000);
			final TimeZoneResponseData timeZoneResponseData = restTemplate.execute(urlAddress, HttpMethod.GET, null,
					timeZoneResponseGoogleResponseParser, Collections.emptyMap());
			final Double dstOffset = timeZoneResponseData.getDstOffset();
			final Double rawOffset = timeZoneResponseData.getRawOffset();
			if (dstOffset != null && rawOffset != null)
			{
				return timeZoneResponseData;
			}
			else
			{
				throw new GeoServiceWrapperException(GeoServiceWrapperException.errorMessages.get(timeZoneResponseData.getStatus()));
			}
		}
		catch (final GeoLocatorException e)
		{
			throw new GeoServiceWrapperException(e);
		}
		catch (final ResourceAccessException e)
		{
			throw new GeoServiceWrapperException(e);
		}

	}

	@Override
	public void setBaseUrl(final String baseUrl)
	{
		super.setBaseUrl(baseUrl);
		this.baseUrl = baseUrl;
	}

	public String getGoogleQuery(final GPS gps)
	{
		final List<Double> collection = Lists.newArrayList(gps.getDecimalLatitude(), gps.getDecimalLongitude());

		return Joiner.on(SEPARATOR).join(Iterables.filter(collection, Predicates.notNull()));
	}

	/**
	 * Gets time zone response google response parser.
	 *
	 * @return the time zone response google response parser
	 */
	protected TimeZoneResponseGoogleResponseParser getTimeZoneResponseGoogleResponseParser()
	{
		return timeZoneResponseGoogleResponseParser;
	}


	/**
	 * Sets time zone response google response parser.
	 *
	 * @param timeZoneResponseGoogleResponseParser
	 * 		the time zone response google response parser
	 */
	public void setTimeZoneResponseGoogleResponseParser(
			final TimeZoneResponseGoogleResponseParser timeZoneResponseGoogleResponseParser)
	{
		this.timeZoneResponseGoogleResponseParser = timeZoneResponseGoogleResponseParser;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
