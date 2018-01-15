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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>, setting it to false if the departureDate of
 * the first transportOffering is not included between the minCheckInTime and maxCheckInTime. Values of minCheckInTime
 * and maxCheckInTime can be set in the properties file.
 */
public class CheckInWindowRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private ConfigurationService configurationService;
	private TimeService timeService;

	private static final String ALTERNATIVE_MESSAGE_BEFORE = "booking.action.check.in.window.alternative.message.before";
	private static final String ALTERNATIVE_MESSAGE_AFTER = "booking.action.check.in.window.alternative.message.after";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(bookingActionData -> bookingActionData.isEnabled()).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		for (final ReservationItemData reservationItem : reservationData.getReservationItems())
		{
			final List<TransportOfferingData> transportOfferings = reservationItem.getReservationItinerary()
					.getOriginDestinationOptions().stream().flatMap(odOption -> odOption.getTransportOfferings().stream())
					.collect(Collectors.toList());

			final Optional<TransportOfferingData> transportOfferingData = transportOfferings.stream()
					.sorted(Comparator
							.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
					.findFirst();

			if (!transportOfferingData.isPresent())
			{
				continue;
			}
			final TransportOfferingData firstTransportOffering = transportOfferingData.get();

			final ZonedDateTime departureUtcTime = TravelDateUtils.getUtcZonedDateTime(firstTransportOffering.getDepartureTime(),
					firstTransportOffering.getDepartureTimeZoneId());
			final ZonedDateTime minCheckInTime = departureUtcTime
					.minusHours(getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MIN_CHECKIN_TIME_PROPERTY));
			final ZonedDateTime maxCheckInTime = departureUtcTime
					.minusHours(getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MAX_CHECKIN_TIME_PROPERTY));

			final ZonedDateTime currentUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
					ZoneId.systemDefault());
			if (currentUtcTime.isBefore(minCheckInTime))
			{
				enabledBookingActions.stream().filter(bookingActionData -> bookingActionData
						.getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber())
						.forEach(bookingActionData -> {
							bookingActionData.setEnabled(false);
							bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE_BEFORE);
						});
			}

			if (currentUtcTime.isAfter(maxCheckInTime))
			{
				enabledBookingActions.stream().filter(bookingActionData -> bookingActionData
						.getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber())
						.forEach(bookingActionData -> {
							bookingActionData.setEnabled(false);
							bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE_AFTER);
						});
			}

		}
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
