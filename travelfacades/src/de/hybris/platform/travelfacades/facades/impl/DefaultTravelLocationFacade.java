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
 */

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelLocationFacade;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.services.TravelLocationService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TravelLocationFacade}
 */
public class DefaultTravelLocationFacade implements TravelLocationFacade
{
	private TravelLocationService travelLocationService;
	private Converter<LocationModel, LocationData> locationConverter;

	@Override
	public LocationData getLocation(final String locationCode)
	{
		final LocationModel locationModel = getTravelLocationService().getLocation(locationCode);

		return locationModel != null ? getLocationConverter().convert(locationModel) : null;
	}

	/**
	 * Gets travel location service.
	 *
	 * @return the travel location service
	 */
	protected TravelLocationService getTravelLocationService()
	{
		return travelLocationService;
	}

	/**
	 * Sets travel location service.
	 *
	 * @param travelLocationService
	 * 		the travel location service
	 */
	@Required
	public void setTravelLocationService(final TravelLocationService travelLocationService)
	{
		this.travelLocationService = travelLocationService;
	}

	/**
	 * Gets location converter.
	 *
	 * @return the location converter
	 */
	protected Converter<LocationModel, LocationData> getLocationConverter()
	{
		return locationConverter;
	}

	/**
	 * Sets location converter.
	 *
	 * @param locationConverter
	 * 		the location converter
	 */
	@Required
	public void setLocationConverter(final Converter<LocationModel, LocationData> locationConverter)
	{
		this.locationConverter = locationConverter;
	}
}
