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

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.List;


/**
 * AccommodationOffering Service interface which provides functionality to manage Accommodation Offering .
 */
public interface AccommodationOfferingService
{
	/**
	 * Get a AccommodationOfferingModel by code.
	 *
	 * @param code
	 * 		the unique code for an accommodation offering
	 * @return AccommodationOfferingModel accommodation offering
	 * @throws ModelNotFoundException
	 * 		the model not found exception
	 */
	AccommodationOfferingModel getAccommodationOffering(String code) throws ModelNotFoundException;

	/**
	 * Get a list of all AccommodationOfferingModel configured in the system.
	 *
	 * @return List<AccommodationOfferingModel> accommodation offerings
	 */
	List<AccommodationOfferingModel> getAccommodationOfferings();

	/**
	 * Get a list of all AccommodationOfferingModel configured in the system.
	 *
	 * @param batchSize
	 * 		the batch size
	 * @param offset
	 * 		the offset
	 * @return List<AccommodationOfferingModel> accommodation offerings
	 */
	SearchResult<AccommodationOfferingModel> getAccommodationOfferings(int batchSize, int offset);
}
