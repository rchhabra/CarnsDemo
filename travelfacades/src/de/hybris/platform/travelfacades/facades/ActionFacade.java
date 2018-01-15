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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;


/**
 * Facade that exposes BookingAction specific methods
 */
public interface ActionFacade
{

	/**
	 * Returns a BookingActionResponseData for a given BookingActionRequestData and ReservationData Used for actions in
	 * transportation scenarios
	 *
	 * @param bookingActionRequest
	 *           as the BookingActionRequestData to be used to create the BookingActionResponseData
	 * @param reservationData
	 *           as the ReservationData to be used to create the BookingActionResponseData
	 * @return the BookingActionResponseData
	 */
	BookingActionResponseData getBookingAction(BookingActionRequestData bookingActionRequest, ReservationData reservationData);

	/**
	 * Returns a BookingActionResponseData for a given BookingActionRequestData and AccommodationReservationData Used for
	 * actions in accommodation scenarios
	 *
	 * @param bookingActionRequest
	 *           as the BookingActionRequestData to be used to create the BookingActionResponseData
	 * @param reservationData
	 *           as the AccommodationReservationData to be used to create the BookingActionResponseData
	 * @return the BookingActionResponseData
	 */
	BookingActionResponseData getAccommodationBookingAction(BookingActionRequestData bookingActionRequest,
			AccommodationReservationData reservationData);

	/**
	 * Returns a BookingActionResponseData for the given BookingActionRequestDatsa and GlobalReservationData Used for
	 * actions in transport plus accommodation scenarios
	 *
	 * @param transportBookingActionRequest
	 *           as the BookingActionRequestData to be used to create the BookingActionResponseData related to the
	 *           transport part of the booking
	 * @param accommodationBookingActionRequest
	 *           as the BookingActionRequestData to be used to create the BookingActionResponseData related to the
	 *           accommodation part of the booking
	 * @param globalBookingActionRequest
	 *           as the BookingActionRequestData to be used to create the BookingActionResponseData related to the the
	 *           complete booking
	 * @param globalReservationData
	 *           as the GlobalReservationData to be used to create the BookingActionResponseData
	 * @return the BookingActionResponseData
	 */
	BookingActionResponseData getTravelBookingAction(BookingActionRequestData transportBookingActionRequest,
			BookingActionRequestData accommodationBookingActionRequest, BookingActionRequestData globalBookingActionRequest,
			GlobalTravelReservationData globalReservationData);

}
