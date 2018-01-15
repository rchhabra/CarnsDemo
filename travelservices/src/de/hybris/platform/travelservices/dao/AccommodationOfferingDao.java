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
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.List;


/**
 * Accommodation Offering Dao interface which provides functionality to manage Accommodation Offering.
 */
public interface AccommodationOfferingDao extends Dao
{
	/**
	 * Return list of accommodation offerings configured in the system.
	 *
	 * @return List<AccommodationOfferingModel> list
	 */
	List<AccommodationOfferingModel> findAccommodationOfferings();

	/**
	 * Return list of accommodation offerings configured in the system.
	 *
	 * @param batchSize
	 * 		the batch size
	 * @param offset
	 * 		the offset
	 * @return List<AccommodationOfferingModel> search result
	 */
	SearchResult<AccommodationOfferingModel> findAccommodationOfferings(int batchSize, int offset);

	/**
	 * Returns a AccommodationOfferingModel for the given code.
	 *
	 * @param code
	 * 		of AccommodationOffering
	 * @return AccommodationOfferingModel accommodation offering model
	 */
	AccommodationOfferingModel findAccommodationOffering(String code);
}
