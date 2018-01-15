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
package de.hybris.platform.ndcservices.services.impl;

import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCTransportOfferingService}
 */
public class DefaultNDCTransportOfferingService implements NDCTransportOfferingService
{
	private TimeService timeService;
	private TransportOfferingService transportOfferingService;
	private ConfigurationService configurationService;

	@Override
	public List<TransportOfferingModel> getTransportOfferings(final List<String> transportOfferingCodes) throws NDCOrderException
	{
		final List<TransportOfferingModel> transportOfferingsList = new LinkedList<>();
		for (final String transportOfferingCode : transportOfferingCodes)
		{
			final TransportOfferingModel transportOffering = getTransportOffering(transportOfferingCode);
			if (!isValidDate(transportOffering))
			{
				throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcservicesConstants.PAST_DATE));
			}
			transportOfferingsList.add(transportOffering);
		}
		return transportOfferingsList;
	}

	@Override
	public TransportOfferingModel getTransportOffering(final String transportOfferingCode)
	{
		return getTransportOfferingService().getTransportOffering(transportOfferingCode);
	}

	@Override
	public boolean isValidReturnDate(final Map<String, List<TransportOfferingModel>> transportOfferings)
	{
		if (transportOfferings.size() < NdcservicesConstants.RETURN_FLIGHT_LEG_NUMBER)
		{
			return true;
		}

		ZonedDateTime arrivalUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
				ZoneId.systemDefault());

		for (final TransportOfferingModel transportOffering : transportOfferings
				.get(String.valueOf(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER)))
		{
			final ZonedDateTime offeringDepartureUtc = getArrivalZonedDateTimeFromTransportOffering(transportOffering);
			if (arrivalUtcTime.isBefore(offeringDepartureUtc))
			{
				arrivalUtcTime = offeringDepartureUtc;
			}
		}

		arrivalUtcTime = arrivalUtcTime.plus(NdcservicesConstants.MIN_BOOKING_ADVANCE_TIME, ChronoUnit.HOURS);

		for (final TransportOfferingModel transportOffering : transportOfferings
				.get(String.valueOf(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER)))
		{
			final ZonedDateTime offeringDepartureUtc = getDepartureZonedDateTimeFromTransportOffering(transportOffering);
			if (arrivalUtcTime.isAfter(offeringDepartureUtc))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isValidDate(final TransportOfferingModel transportOffering)
	{
		final ZonedDateTime firstOfferingDepartureUtc = getDepartureZonedDateTimeFromTransportOffering(transportOffering);

		final ZonedDateTime currentUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
				ZoneId.systemDefault());
		if (currentUtcTime.isAfter(firstOfferingDepartureUtc))
		{
			return false;
		}

		final int bookingWindow = NdcservicesConstants.MIN_BOOKING_ADVANCE_TIME;
		final ZonedDateTime minBookingUtcTime = currentUtcTime.plusHours(bookingWindow);

		return !minBookingUtcTime.isAfter(firstOfferingDepartureUtc);
	}

	@Override
	public ZonedDateTime getDepartureZonedDateTimeFromTransportOffering(final TransportOfferingModel transportOffering)
	{
		final List<PointOfServiceModel> originPointOfServices = transportOffering.getTravelSector().getOrigin().getPointOfService();

		final ZoneId departureTimeZoneId;
		if (CollectionUtils.isNotEmpty(originPointOfServices) && Objects.nonNull(originPointOfServices.get(0).getTimeZoneId()))
		{
			departureTimeZoneId = ZoneId.of(originPointOfServices.get(0).getTimeZoneId());
		}
		else
		{
			departureTimeZoneId = ZoneId.from(ZoneOffset.UTC);
		}

		return TravelDateUtils.getUtcZonedDateTime(transportOffering.getDepartureTime(), departureTimeZoneId);
	}

	@Override
	public ZonedDateTime getArrivalZonedDateTimeFromTransportOffering(final TransportOfferingModel transportOffering)
	{
		final List<PointOfServiceModel> originPointOfServices = transportOffering.getTravelSector().getDestination()
				.getPointOfService();

		final ZoneId departureTimeZoneId;
		if (CollectionUtils.isNotEmpty(originPointOfServices) && Objects.nonNull(originPointOfServices.get(0).getTimeZoneId()))
		{
			departureTimeZoneId = ZoneId.of(originPointOfServices.get(0).getTimeZoneId());
		}
		else
		{
			departureTimeZoneId = ZoneId.from(ZoneOffset.UTC);
		}

		return TravelDateUtils.getUtcZonedDateTime(transportOffering.getArrivalTime(), departureTimeZoneId);
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
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transport offering service
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transport offering service
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
