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

import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData> for a complete cancelled booking.
 */
public class CancelCompleteBookingRestrictionStrategy implements GlobalBookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.cancel.complete.booking.alternative.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final GlobalTravelReservationData globalReservationData,
			final BookingActionResponseData bookingActionResponse)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream().filter(BookingActionData::isEnabled)
				.collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		boolean enabled = false;

		Optional<BookingActionData> cancelBookingOptional = Optional.empty();
		if (CollectionUtils.isNotEmpty(bookingActionResponse.getBookingActions()))
		{
			cancelBookingOptional = bookingActionResponse.getBookingActions().stream()
					.filter(bookingAction -> ActionTypeOption.CANCEL_TRANSPORT_BOOKING.equals(bookingAction.getActionType()))
					.findFirst();
		}

		Optional<AccommodationBookingActionData> cancelAccommodationBookingOptional = Optional.empty();
		if (CollectionUtils.isNotEmpty(bookingActionResponse.getAccommodationBookingActions()))
		{
			cancelAccommodationBookingOptional = bookingActionResponse.getAccommodationBookingActions().stream()
					.filter(bookingAction -> ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING.equals(bookingAction.getActionType()))
					.findFirst();
		}

		if (cancelBookingOptional.isPresent() && cancelAccommodationBookingOptional.isPresent())
		{
			final String reservationDataBookingStatusCode = globalReservationData.getReservationData().getBookingStatusCode();
			final String accommodationReservationDataBookingStatusCode = globalReservationData.getAccommodationReservationData()
					.getBookingStatusCode();
			if (checkIfNotCancelledBookingStatus(reservationDataBookingStatusCode)
					&& checkIfNotCancelledBookingStatus(accommodationReservationDataBookingStatusCode))
			{
				enabled = cancelBookingOptional.get().isEnabled() && cancelAccommodationBookingOptional.get().isEnabled();
			}
			else if (checkIfNotCancelledBookingStatus(reservationDataBookingStatusCode)
					|| checkIfNotCancelledBookingStatus(accommodationReservationDataBookingStatusCode))
			{
				enabled = cancelBookingOptional.get().isEnabled() || cancelAccommodationBookingOptional.get().isEnabled();
				cancelBookingOptional.get().setEnabled(Boolean.FALSE);
				cancelAccommodationBookingOptional.get().setEnabled(Boolean.FALSE);
			}
		}

		if (cancelBookingOptional.isPresent() && !cancelAccommodationBookingOptional.isPresent())
		{
			enabled = cancelBookingOptional.get().isEnabled();
		}
		if (!cancelBookingOptional.isPresent() && cancelAccommodationBookingOptional.isPresent())
		{
			enabled = cancelAccommodationBookingOptional.get().isEnabled();
		}

		if (!enabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}

	}

	protected boolean checkIfNotCancelledBookingStatus(final String bookingStatusCode)
	{
		return !StringUtils.equalsIgnoreCase(OrderStatus.CANCELLED.getCode(), bookingStatusCode)
				&& !StringUtils.equalsIgnoreCase(OrderStatus.CANCELLING.getCode(), bookingStatusCode);
	}

}
