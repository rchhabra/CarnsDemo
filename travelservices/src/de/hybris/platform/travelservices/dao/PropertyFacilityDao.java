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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import java.util.List;


/**
 * Property Facility Dao interface which provides functionality to manage Property Facility.
 */
public interface PropertyFacilityDao
{
	/**
	 * Return list of Property Facilities configured in the system.
	 *
	 * @return List<PropertyFacilityModel> list
	 */
	List<PropertyFacilityModel> findPropertyFacilities();

	/**
	 * Return list of Property Facilities configured in the system.
	 *
	 * @param batchSize
	 * 		the batch size
	 * @param offset
	 * 		the offset
	 * @return List<PropertyFacilityModel> search result
	 */
	SearchResult<PropertyFacilityModel> findPropertyFacilities(int batchSize, int offset);

	/**
	 * Returns a PropertyFacilityModel for the given code.
	 *
	 * @param code
	 * 		of PropertyFacility
	 * @return PropertyFacilityModel property facility model
	 */
	PropertyFacilityModel findPropertyFacility(String code);
}
