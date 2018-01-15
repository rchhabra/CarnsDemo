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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.List;


/**
 * Interface to retrieve the booking information for my account -> my booking section.
 */
public interface BookingListFacade
{
	/**
	 * Returns a list of reservation data which corresponds to all the bookings of the current customer in session
	 *
	 * @return list of current customer's bookings
	 */
	List<ReservationData> getCurrentCustomerBookings();

	/**
	 * Returns a list of accommodation reservation data which corresponds to all accommodation bookings of the current
	 * customer in session
	 *
	 * @return list of current customer's bookings
	 */
	List<AccommodationReservationData> getCurrentCustomerAccommodationBookings();

	/**
	 * Returns a list of global travel reservation data which corresponds to all travel bookings of the current customer
	 * in session
	 *
	 * @return list of current customer's bookings
	 */
	List<GlobalTravelReservationData> getCurrentCustomerTravelBookings();

	/**
	 * Returns only the bookings visible to the current user
	 *
	 * @return list of visible current customer travel bookings
	 */
	List<GlobalTravelReservationData> getVisibleCurrentCustomerTravelBookings();

	/**
	 * Returns only the accommodation bookings visible to the current user
	 *
	 * @return list of visible current customer accommodation bookings
	 */
	List<AccommodationReservationData> getVisibleCurrentCustomerAccommodationBookings();

}
