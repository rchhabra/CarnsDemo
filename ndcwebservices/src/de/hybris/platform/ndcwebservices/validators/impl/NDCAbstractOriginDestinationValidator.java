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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to validate originDestination, contains the mathod to check if two airports belong to the same airport group
 */
public abstract class NDCAbstractOriginDestinationValidator<N> implements NDCRequestValidator<N>
{
	private TransportFacilityService transportFacilityService;
	private TravelRouteFacade travelRouteFacade;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private ConfigurationService configurationService;

	/**
	 * @param departure
	 * @param arrival
	 * @return
	 */
	protected boolean isSameCity(final String departure, final String arrival)
	{
		if (departure.equals(arrival))
		{
			return true;
		}

		final TransportFacilityModel departureTransportFacility = getTransportFacilityService().getTransportFacility(departure);

		if (Objects.nonNull(departureTransportFacility) && StringUtils
				.equalsIgnoreCase(departureTransportFacility.getLocation().getCode(), arrival))
		{
			return true;
		}

		final TransportFacilityModel arrivalTransportFacility = getTransportFacilityService().getTransportFacility(arrival);

		if (Objects.nonNull(arrivalTransportFacility) && StringUtils
				.equalsIgnoreCase(arrivalTransportFacility.getLocation().getCode(), departure))
		{
			return true;
		}

		if (Objects.nonNull(departureTransportFacility) && Objects.nonNull(arrivalTransportFacility) && StringUtils
				.equalsIgnoreCase(departureTransportFacility
						.getLocation().getCode(), arrivalTransportFacility.getLocation().getCode()))
		{
			return true;
		}

		return false;
	}

	/**
	 * Checks, in case of a return flight, if the departure of the outbound is in the same city of the arrival inbound and if
	 * the arrival of the outbound is in the same city of the departure of the inbound
	 *
	 * @param offers
	 * @param errorsType
	 * @return
	 */
	protected boolean validateSameAirports(final List<String> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		if (offers.size() == NdcservicesConstants.ONE_WAY_FLIGHT)
		{
			return true;
		}

		final TravelRouteData outboundRoute = getTravelRouteFacade().getTravelRoute(
				getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(offers.get(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER)).getRouteCode());

		final TravelRouteData inboundRoute = getTravelRouteFacade().getTravelRoute(
				getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(offers.get(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER)).getRouteCode());

		if (!isSameCity(outboundRoute.getOrigin().getCode(), inboundRoute.getDestination().getCode()) || !isSameCity(
				outboundRoute.getDestination().getCode(), inboundRoute.getOrigin().getCode()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_AIRPORTS_COMBINATION));
			return false;
		}

		return true;
	}

	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	@Required
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

	protected TravelRouteFacade getTravelRouteFacade()
	{
		return travelRouteFacade;
	}

	@Required
	public void setTravelRouteFacade(final TravelRouteFacade travelRouteFacade)
	{
		this.travelRouteFacade = travelRouteFacade;
	}

	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
