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

package de.hybris.platform.travelfacades.facades.accommodation;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AvailableServiceData;

import java.util.List;


/**
 * Facade that exposes the methods related to the extra services of an accommodation and accommodationOffering
 */
public interface AccommodationExtrasFacade
{

	/**
	 * Returns the list of all the availableServiceData for the reserved accommodation
	 *
	 * @param reservationData
	 * 		as the current reservationData
	 * @return the list of all the available serviceData
	 */
	List<AvailableServiceData> getAvailableServices(AccommodationReservationData reservationData);

}
