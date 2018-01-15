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


import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;


/**
 * The interface Accommodation booking facade.
 */
public interface AccommodationBookingFacade
{
	/**
	 * Gets booker email id from accommodation reservation data.
	 *
	 * @param globalReservationData
	 * 		the global reservation data
	 * @param lastName
	 * 		the last name
	 * @return the booker email id from accommodation reservation data
	 */
	String getBookerEmailIDFromAccommodationReservationData(GlobalTravelReservationData globalReservationData, String lastName);
}
