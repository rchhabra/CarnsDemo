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

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelImageFacade;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.services.TravelLocationService;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TravelImageFacade}
 */
public class DefaultTravelImageFacade implements TravelImageFacade
{
	private AccommodationOfferingService accommodationOfferingService;
	private Converter<MediaModel, ImageData> imageConverter;
	private TravelLocationService travelLocationService;
	private TransportFacilityService transportFacilityService;
	private TravelCartService travelCartService;
	private AccommodationCommerceCartService accommodationCommerceCartService;

	@Override
	public ImageData getImageForDestinationLocation(final String destinationLocation)
	{
		final Optional<String> optionalCityLocation = Arrays.stream(destinationLocation.split(Pattern.quote("|")))
				.filter(locationCode -> locationCode.length() == 3).findFirst();
		if (optionalCityLocation.isPresent())
		{
			final LocationModel location = getTravelLocationService().getLocation(optionalCityLocation.get());
			if (location != null && location.getPicture() != null)
			{
				return getImageConverter().convert(location.getPicture());
			}
		}

		return null;
	}

	@Override
	public ImageData getImageForAccommodationOfferingLocation(final String accommodationOfferingCode)
	{
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);
		if (accommodationOfferingModel != null)
		{
			final LocationModel cityLocation = getCityLocation(accommodationOfferingModel.getLocation());
			if (cityLocation != null && cityLocation.getPicture() != null)
			{
				return getImageConverter().convert(cityLocation.getPicture());
			}
		}
		return null;
	}

	@Override
	public ImageData getImageFromCart()
	{
		final String accommodationOfferingCode = getAccommodationCommerceCartService().getCurrentAccommodationOffering();

		if (StringUtils.isNotEmpty(accommodationOfferingCode))
		{
			return getImageForAccommodationOfferingLocation(accommodationOfferingCode);
		}

		final String destinationTransportFacilityCode = getTravelCartService().getCurrentDestination();

		if (StringUtils.isNotEmpty(destinationTransportFacilityCode))
		{
			return getImageForArrivalTransportFacility(destinationTransportFacilityCode);
		}

		return null;
	}

	@Override
	public ImageData getImageForArrivalTransportFacility(final String arrivalTransportFacility)
	{
		final TransportFacilityModel transportFacility = getTransportFacilityService()
				.getTransportFacility(arrivalTransportFacility);
		if (transportFacility != null)
		{
			final LocationModel cityLocation = getCityLocation(transportFacility.getLocation());
			if (cityLocation != null && cityLocation.getPicture() != null)
			{
				return getImageConverter().convert(cityLocation.getPicture());
			}
		}
		return null;
	}

	/**
	 * Gets city location.
	 *
	 * @param locationModel
	 * 		the location model
	 * @return the city location
	 */
	protected LocationModel getCityLocation(final LocationModel locationModel)
	{
		LocationModel location = locationModel;

		while (location != null)
		{
			if (LocationType.CITY.equals(location.getLocationType()))
			{
				return location;
			}
			location = CollectionUtils.isNotEmpty(location.getSuperlocations()) ? location.getSuperlocations().get(0) : null;
		}

		return null;
	}

	/**
	 * Gets accommodation offering service.
	 *
	 * @return the accommodation offering service
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * Sets accommodation offering service.
	 *
	 * @param accommodationOfferingService
	 * 		the accommodation offering service
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	/**
	 * Gets image converter.
	 *
	 * @return the image converter
	 */
	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	/**
	 * Sets image converter.
	 *
	 * @param imageConverter
	 * 		the image converter
	 */
	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
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
	 * Gets transport facility service.
	 *
	 * @return the transport facility service
	 */
	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	/**
	 * Sets transport facility service.
	 *
	 * @param transportFacilityService
	 * 		the transport facility service
	 */
	@Required
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

	/**
	 * Gets travel cart service.
	 *
	 * @return the travel cart service
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * Sets travel cart service.
	 *
	 * @param travelCartService
	 * 		the travel cart service
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * Gets accommodation commerce cart service.
	 *
	 * @return the accommodation commerce cart service
	 */
	protected AccommodationCommerceCartService getAccommodationCommerceCartService()
	{
		return accommodationCommerceCartService;
	}

	/**
	 * Sets accommodation commerce cart service.
	 *
	 * @param accommodationCommerceCartService
	 * 		the accommodation commerce cart service
	 */
	@Required
	public void setAccommodationCommerceCartService(final AccommodationCommerceCartService accommodationCommerceCartService)
	{
		this.accommodationCommerceCartService = accommodationCommerceCartService;
	}
}
