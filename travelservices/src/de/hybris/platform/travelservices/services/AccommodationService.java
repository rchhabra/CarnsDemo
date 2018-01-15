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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.List;


/**
 * Accommodation Service interface which provides functionality to manage Accommodation Offering.
 */
public interface AccommodationService
{
	/**
	 * Returns a list of AccommodationModel for a given AccommodationOfferingCode
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the accommodationOffering to use to get the list of AccommodationModel
	 * @return the list of Accommodations found
	 */
	List<AccommodationModel> getAccommodationForAccommodationOffering(String accommodationOfferingCode);

	/**
	 * Returns a list of AccommodationModel for the given accommodationOfferingCode and accommodationCodes
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the AccommodationOffering to use to get the Accommodation
	 * @param accommodationCode
	 * 		the codes of the Accommodation
	 * @return the object of AccommodationModel
	 */
	AccommodationModel getAccommodationForAccommodationOffering(String accommodationOfferingCode,
			String accommodationCode);
}
