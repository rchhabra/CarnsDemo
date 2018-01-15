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
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * Exposes the services relevant to Reservation DTO.
 */
public interface ReservationFacade
{

	/**
	 * Populates the Reservation DTO from Abstract Order Model.
	 *
	 * @param abstractOrderModel
	 * 		- abstract order model to be converted
	 * @return reservation DTO
	 */
	ReservationData getReservationData(AbstractOrderModel abstractOrderModel);

	/**
	 * Populates the Reservation DTO from the current Session Cart.
	 *
	 * @return reservation DTO
	 */
	ReservationData getCurrentReservationData();

	/**
	 * Populates the ReservationData from the current session cart with data only needed for displaying the transport
	 * summary.
	 *
	 * @return current reservation summary
	 */
	ReservationData getCurrentReservationSummary();

	/**
	 * Gets the accommodation reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return the accommodation reservation data
	 */
	AccommodationReservationData getAccommodationReservationData(AbstractOrderModel abstractOrderModel);

	/**
	 * Gets the current accommodation reservation data.
	 *
	 * @return the accommodation reservation data
	 */
	AccommodationReservationData getCurrentAccommodationReservation();

	/**
	 * Populates the AccommodationReservationData from the current session cart with data only needed for displaying the
	 * accommodation summary.
	 *
	 * @return current reservation summary
	 */
	AccommodationReservationData getCurrentAccommodationReservationSummary();

	/**
	 * Populates the AccommodationReservationData from the order with data only needed for displaying the accommodation
	 * summary.
	 * 
	 * @param orderCode
	 *
	 * @return current reservation summary
	 */
	AccommodationReservationData getAccommodationReservationSummary(String orderCode);

	/**
	 * Gets the global travel reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return the global travel reservation data
	 */
	GlobalTravelReservationData getGlobalTravelReservationData(AbstractOrderModel abstractOrderModel);

	/**
	 * Retrieves and returns the {@link GlobalTravelReservationData} associated to the {@link OrderModel} with the provided
	 * bookingReference, null otherwise
	 *
	 * @param bookingReference
	 * 		a String representing bookingReference Number
	 * @return global travel reservation data
	 */
	GlobalTravelReservationData retrieveGlobalReservationData(String bookingReference);

	/**
	 * Gets the cancelled global travel reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the order
	 * @return the cancelled global travel reservation data
	 */
	GlobalTravelReservationData getCancelledGlobalTravelReservationData(AbstractOrderModel abstractOrderModel);

	/**
	 * Populates the ReservationData from the current session cart with data only needed for displaying the transport
	 * summary.
	 *
	 * @return current reservation summary
	 */
	ReservationData getCurrentPackageTransportReservationSummary();

	/**
	 * Populates the ReservationData from the provided abstractOrderModel without the pricing information
	 * 
	 * @param abstractOrderModel
	 * @return reservation summary
	 */
	ReservationData getBasicReservationData(AbstractOrderModel abstractOrderModel);

	/**
	 * Gets booker email id from reservation data.
	 *
	 * @param globalReservationData
	 * 		the global reservation data
	 * @param lastName
	 * 		the last name
	 * @param passengerReference
	 * 		the passenger reference
	 * @return the booker email id from reservation data
	 */
	String getBookerEmailIDFromReservationData(GlobalTravelReservationData globalReservationData, String lastName,
			String passengerReference);

	/**
	 * Returns the string representing the BookingJourneyType corresponding to the given orderCode.
	 *
	 * @param orderCode
	 * 		as the order code
	 * @return the string representing the BookingJourneyType corresponding to the given orderCode, null if not present.
	 */
	String getBookingJourneyType(String orderCode);
}
