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
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.impl.GoogleMapTools;
import de.hybris.platform.storelocator.impl.GoogleMapsServiceWrapper;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.travelservices.storelocator.TravelGeoWebServiceWrapper;

import org.springframework.beans.factory.annotation.Required;


public class TravelGoogleMapsServiceWrapper extends GoogleMapsServiceWrapper implements TravelGeoWebServiceWrapper
{

	private ConfigurationService configurationService;

	public static final String GOOGLE_API_TIMEZONE_URL = "google.api.timezone.url";

	private GoogleMapTools googleMapTools;

	@Override
	public TimeZoneResponseData timeZoneOffset(final Location address) throws GeoServiceWrapperException
	{
		final TravelGoogleMapTools mapTools = (TravelGoogleMapTools) getMapTools(
				getConfigurationService().getConfiguration().getString(GOOGLE_API_TIMEZONE_URL, null));
		return mapTools.timeZoneOffset(address);
	}

	@Override
	protected GoogleMapTools getMapTools(final String url)
	{
		googleMapTools.setBaseUrl(url);
		return googleMapTools;
	}

	/**
	 * Gets google map tools.
	 *
	 * @return the google map tools
	 */
	protected GoogleMapTools getGoogleMapTools()
	{
		return googleMapTools;
	}

	@Override
	public void setGoogleMapTools(final GoogleMapTools googleMapTools)
	{
		super.setGoogleMapTools(googleMapTools);
		this.googleMapTools = googleMapTools;
	}

	/**
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
