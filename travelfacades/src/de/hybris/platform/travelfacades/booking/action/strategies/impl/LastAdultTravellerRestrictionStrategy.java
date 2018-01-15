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
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. For each leg if there is only one 'adult'
 * traveller left, the enabled property of the related BookingActionData is set to false, true otherwise. Other non
 * 'adult' passengers BookingActionData are not modified by this strategy.
 */
public class LastAdultTravellerRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.last.adult.traveller.alternative.message";
	private BookingService bookingService;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		for (final ReservationItemData reservationItemData : reservationData.getReservationItems())
		{

			final List<TravellerData> passengers = reservationItemData.getReservationItinerary().getTravellers().stream()
					.filter(traveller -> traveller.getTravellerType().equals(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER))
					.collect(Collectors.toList());

			final List<TravellerData> adultPassengers = passengers.stream()
					.filter(passenger -> ((PassengerInformationData) passenger.getTravellerInfo()).getPassengerType().getCode()
							.equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
					.collect(Collectors.toList());

			if (CollectionUtils.size(adultPassengers) > 1)
			{
				return;
			}

			if (reservationData.getFilteredTravellers())
			{
				if (CollectionUtils.isNotEmpty(adultPassengers) && !getBookingService()
						.atleastOneAdultTravellerRemaining(reservationData.getCode(), adultPassengers.get(0).getLabel()))
				{
					// set false to bookingActionData with same originDestinationRefNumber and passenger Adult
					bookingActionDataList.stream()
							.filter(bookingActionData -> bookingActionData.getOriginDestinationRefNumber() == reservationItemData
									.getOriginDestinationRefNumber() && adultPassengers.contains(bookingActionData.getTraveller()))
							.forEach(bookingActionData ->
							{
								bookingActionData.setEnabled(false);
								bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
							});
				}
			}
			else
			{
				// set false to bookingActionData with same originDestinationRefNumber and passenger Adult
				bookingActionDataList.stream()
						.filter(bookingActionData -> bookingActionData.getOriginDestinationRefNumber() == reservationItemData
								.getOriginDestinationRefNumber() && adultPassengers.contains(bookingActionData.getTraveller()))
						.forEach(bookingActionData ->
						{
							bookingActionData.setEnabled(false);
							bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
						});
			}
		}
	}

	/**
	 * Gets booking service.
	 *
	 * @return the booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 * 		the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
