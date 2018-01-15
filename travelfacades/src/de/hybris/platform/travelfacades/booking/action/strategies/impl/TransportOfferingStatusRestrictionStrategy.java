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
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>, based on the transport offering status.
 */
public class TransportOfferingStatusRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.leg.check.in.alternative.message";
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

		for (final BookingActionData bookingActionData : enabledBookingActions)
		{
			final int originDestinationRefNumber = bookingActionData.getOriginDestinationRefNumber();
			final List<TransportOfferingData> transportOfferings = reservationData.getReservationItems()
					.get(originDestinationRefNumber).getReservationItinerary().getOriginDestinationOptions().stream()
					.flatMap(odOption -> odOption.getTransportOfferings().stream()).collect(Collectors.toList());

			final boolean enabled = transportOfferings.stream().noneMatch(to -> notAllowedStatuses.stream().anyMatch(
					transportOfferingStatus -> StringUtils.equalsIgnoreCase(transportOfferingStatus.getCode(), to.getStatus())));

			if (!enabled)
			{
				bookingActionData.setEnabled(enabled);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			}
		}
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
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}
}
