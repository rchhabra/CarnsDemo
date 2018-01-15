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
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;


/**
 * this interface finds and return travel sector
 */
public interface TravelSectorDao
{

	/**
	 * This API find and return Travel sector based on origin and destination.
	 *
	 * @param origin
	 * 		the origin
	 * @param destination
	 * 		the destination
	 * @return travel sector model
	 */
	TravelSectorModel findTravelSector(TransportFacilityModel origin, TransportFacilityModel destination);
}
