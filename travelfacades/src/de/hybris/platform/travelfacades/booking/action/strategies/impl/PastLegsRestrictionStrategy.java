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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;



/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. If all legs of the booking are in the past,
 * the enabled property is set to false, true otherwise
 */
public class PastLegsRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.past.legs.alternative.message";

	private TimeService timeService;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		boolean enabled = false;
		for (final ReservationItemData reservationItem : reservationData.getReservationItems())
		{
			final List<TransportOfferingData> transportOfferings = reservationItem.getReservationItinerary()
					.getOriginDestinationOptions().stream().flatMap(odOption -> odOption.getTransportOfferings().stream())
					.collect(Collectors.toList());

			final Optional<TransportOfferingData> firstTransportOffering = transportOfferings.stream().sorted(Comparator
					.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
					.findFirst();

			if (!firstTransportOffering.isPresent())
			{
				continue;
			}
			enabled |= TravelDateUtils.isAfter(firstTransportOffering.get().getDepartureTime(),
					firstTransportOffering.get().getDepartureTimeZoneId(), getTimeService().getCurrentTime(), ZoneId.systemDefault());
		}

		if (!enabled)
		{
			for (final BookingActionData bookingActionData : enabledBookingActions)
			{
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
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
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
