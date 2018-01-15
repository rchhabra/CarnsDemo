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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import java.util.List;


/**
 * The interface Property facility service.
 */
public interface PropertyFacilityService
{
	/**
	 * Get a PropertyFacilityModel by code.
	 *
	 * @param code
	 * 		the unique code for a PropertyFacility
	 * @return PropertyFacilityModel property facility
	 */
	PropertyFacilityModel getPropertyFacility(String code);

	/**
	 * Get a list of all PropertyFacilityModel configured in the system.
	 *
	 * @return List<PropertyFacilityModel> property facilities
	 */
	List<PropertyFacilityModel> getPropertyFacilities();

	/**
	 * Get a list of all PropertyFacilityModel configured in the system.
	 *
	 * @param batchSize
	 * 		the batch size
	 * @param offset
	 * 		the offset
	 * @return List<PropertyFacilityModel> property facilities
	 */
	SearchResult<PropertyFacilityModel> getPropertyFacilities(int batchSize, int offset);

}
