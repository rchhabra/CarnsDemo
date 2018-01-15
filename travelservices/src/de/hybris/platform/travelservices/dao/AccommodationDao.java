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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.List;


/**
 * AccommodationDao interface which provides functionality to manage Accommodation.
 */
public interface AccommodationDao extends Dao
{

	/**
	 * Returns a list of AccommodationModel for the given accommodationOfferingCode
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the AccommodationOffering to use to get the Accommodation
	 * @return the list of AccommodationModel
	 */
	List<AccommodationModel> findAccommodationForAccommodationOffering(String accommodationOfferingCode);

	/**
	 * Returns an object of AccommodationModel for the given accommodationOfferingCode and accommodationCode
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the AccommodationOffering to use to get the Accommodation
	 * @param accommodationCode
	 * 		the code of the Accommodation
	 * @return object of AccommodationModel
	 */
	AccommodationModel findAccommodationForAccommodationOffering(String accommodationOfferingCode, String accommodationCode);

}
