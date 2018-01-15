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
package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.strategies.CheckInEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of CheckInEvaluatorStrategy
 */
public class DefaultCheckInEvaluatorStrategy implements CheckInEvaluatorStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultCheckInEvaluatorStrategy.class);

	private ConfigurationService configurationService;
	private TimeService timeService;
	private List<TransportOfferingStatus> notAllowedStatuses;

	@Override
	public boolean isCheckInPossible(final ReservationData reservation, final int originDestinationRefNumber)
	{
		if (reservation == null)
		{
			return false;
		}

		if (!checkReservationAndStatus(reservation))
		{
			LOG.error("Reservation not valid or in cancelling/cancelled status. Booking reference: " + reservation.getCode());
			return false;
		}

		final List<ReservationItemData> reservationItems = reservation.getReservationItems();

		if (reservationItems == null || CollectionUtils.isEmpty(reservationItems))
		{
			LOG.error("Reservation Item not valid. Booking reference: " + reservation.getCode() + " OriginDestination Ref Number"
					+ originDestinationRefNumber);
			return false;
		}

		final ReservationItemData reservationItem = reservationItems.get(originDestinationRefNumber);

		for (final OriginDestinationOptionData originDestinationOption : reservationItem.getReservationItinerary()
				.getOriginDestinationOptions())
		{
			if (CollectionUtils.isEmpty(originDestinationOption.getTransportOfferings()))
			{
				LOG.error("No Transport Offerings available for the current leg. Booking reference: " + reservation.getCode()
						+ "OriginDestination Ref Number" + originDestinationRefNumber);
				return false;
			}

			//check if any transportOffering is with notAllowedStatus
			final Optional<TransportOfferingData> transportOfferingWithNotAllowedStatus = originDestinationOption
					.getTransportOfferings().stream()
					.filter(to -> getNotAllowedStatuses().stream()
							.anyMatch(notAllowedStatus -> StringUtils.equalsIgnoreCase(notAllowedStatus.getCode(), to.getStatus())))
					.findAny();
			if (transportOfferingWithNotAllowedStatus.isPresent())
			{
				LOG.error("Transport Offering with code " + transportOfferingWithNotAllowedStatus.get().getCode()
						+ " already in BOARDED, DEPARTED or CANCELLED status. Booking " + "reference: " + reservation.getCode()
						+ "OriginDestination Ref Number" + originDestinationRefNumber);
				return false;
			}

			// Check for the first transport offering to validate the check-in
			final Optional<TransportOfferingData> transportOffering = originDestinationOption.getTransportOfferings().stream()
					.sorted(Comparator
							.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
					.findFirst();
			if (!transportOffering.isPresent())
			{
				LOG.error("Transport Offering not valid in Booking " + "reference: " + reservation.getCode()
						+ "OriginDestination Ref Number" + originDestinationRefNumber);
				return false;
			}
			final ZonedDateTime departureUtcTime = TravelDateUtils.getUtcZonedDateTime(transportOffering.get().getDepartureTime(),
					transportOffering.get().getDepartureTimeZoneId());
			final ZonedDateTime minCheckInTime = departureUtcTime
					.minusHours(getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MIN_CHECKIN_TIME_PROPERTY));
			final ZonedDateTime maxCheckInTime = departureUtcTime
					.minusHours(getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MAX_CHECKIN_TIME_PROPERTY));

			final ZonedDateTime currentUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
					ZoneId.systemDefault());
			if (currentUtcTime.isBefore(minCheckInTime) || currentUtcTime.isAfter(maxCheckInTime))
			{
				LOG.error(
						"Transport Offering with code " + transportOffering.get().getCode() + " not in valid Check In window. Booking "
								+ "reference: " + reservation.getCode() + "OriginDestination Ref Number" + originDestinationRefNumber);
				return false;
			}
		}
		return true;
	}

	/**
	 * Check reservation and status boolean.
	 *
	 * @param reservation
	 *           the reservation
	 * @return the boolean
	 */
	protected Boolean checkReservationAndStatus(final ReservationData reservation)
	{
		return !(StringUtils.equalsIgnoreCase(OrderStatus.CANCELLED.getCode(), reservation.getBookingStatusCode())
				|| StringUtils.equalsIgnoreCase(OrderStatus.CANCELLING.getCode(), reservation.getBookingStatusCode()));
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets time service.
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Gets not allowed statuses.
	 *
	 * @return the notAllowedStatuses
	 */
	protected List<TransportOfferingStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	/**
	 * Sets not allowed statuses.
	 *
	 * @param notAllowedStatuses
	 *           the notAllowedStatuses to set
	 */
	@Required
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}

}
