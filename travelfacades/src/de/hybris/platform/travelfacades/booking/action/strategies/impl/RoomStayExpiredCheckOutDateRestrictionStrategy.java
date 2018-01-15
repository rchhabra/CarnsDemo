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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>, based on the check out date for the
 * accommodation.
 */
public class RoomStayExpiredCheckOutDateRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.checkin.date.not.expired";

	private List<OrderStatus> statusesToIgnore;

	@Override
	public void applyStrategy(final List<AccommodationBookingActionData> bookingActionDataList,
			final AccommodationReservationData accommodationReservationData)
	{
		final List<AccommodationBookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(AccommodationBookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final ZoneId currentZone = ZoneId.systemDefault();
		final Date today = Date.from(LocalDateTime.now().atZone(currentZone).toInstant());
		final Optional<ReservedRoomStayData> roomStay = accommodationReservationData.getRoomStays().stream().findFirst();
		if (!roomStay.isPresent())
		{
			return;
		}
		final Date checkOutDate = roomStay.get().getCheckOutDate();
		if (TravelDateUtils.isAfter(checkOutDate, currentZone, today, currentZone)
				&& !getStatusesToIgnore().stream().map(OrderStatus::getCode).collect(Collectors.toList())
						.contains(accommodationReservationData.getBookingStatusCode()))
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}
	}

	/**
	 *
	 * @return statusesToIgnore
	 */
	protected List<OrderStatus> getStatusesToIgnore()
	{
		return statusesToIgnore;
	}

	/**
	 *
	 * @param statusesToIgnore
	 *           the statusesToIgnore to set
	 */
	@Required
	public void setStatusesToIgnore(final List<OrderStatus> statusesToIgnore)
	{
		this.statusesToIgnore = statusesToIgnore;
	}



}
