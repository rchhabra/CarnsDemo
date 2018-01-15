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

import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;


/**
 * TransportFacility Dao interface which provides services to get TransportFacility data from the database
 */
public interface TransportFacilityDao
{
	/**
	 * Method to query the database and returns a TransportFacilityModel based on the given Transport Facility code if
	 * one exists otherwise the method will return null.
	 *
	 * @param code
	 * 		the code
	 * @return TransportFacilityModel transport facility model
	 */
	TransportFacilityModel findTransportFacility(final String code);
}
