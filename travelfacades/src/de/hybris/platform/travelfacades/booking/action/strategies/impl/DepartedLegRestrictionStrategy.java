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
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. For each leg, if the TransportOfferingStatus is
 * DEPARTED, the enabled property is set to false, true otherwise.
 */
public class DepartedLegRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.departed.leg.alternative.message";

	private TimeService timeService;

	private List<TransportOfferingStatus> notAllowedStatuses;

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

			final boolean enabled = transportOfferings.stream().noneMatch(to -> getNotAllowedStatuses().stream().anyMatch
					(transportOfferingStatus -> StringUtils.equalsIgnoreCase(transportOfferingStatus.getCode(), to.getStatus())));

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
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the notAllowedStatuses
	 */
	protected List<TransportOfferingStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	/**
	 * @param notAllowedStatuses
	 *           the notAllowedStatuses to set
	 */
	@Required
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}
}
