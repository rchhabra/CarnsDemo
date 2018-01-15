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
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. For each leg, if the departureDate is
 * before the current time, meaning that the leg is in the past, the enabled property is set to false, true otherwise.
 */
public class PastLegRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.past.leg.alternative.message";

	private TimeService timeService;

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

			final TransportOfferingData firstTransportOffering = transportOfferings.stream().sorted(Comparator
					.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
					.findFirst().get();

			final boolean enabled = TravelDateUtils.isAfter(firstTransportOffering.getDepartureTime(),
					firstTransportOffering.getDepartureTimeZoneId(), getTimeService().getCurrentTime(), ZoneId.systemDefault());

			if (!enabled)
			{
				enabledBookingActions.stream().filter(bookingActionData -> bookingActionData
						.getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber())
						.forEach(bookingActionData -> {
							bookingActionData.setEnabled(enabled);
							bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
						});
			}
		}
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
