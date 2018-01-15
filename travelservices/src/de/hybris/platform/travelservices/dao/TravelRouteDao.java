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

import de.hybris.platform.travelservices.model.travel.TravelRouteModel;

import java.util.List;


/**
 * TravelRoute Dao interface which provides services to get TravelRoute data from the database
 */
public interface TravelRouteDao
{

	/**
	 * Method returns a list of TravelRouteModel from the database based on origin and destination
	 *
	 * @param origin
	 * 		the origin TransportFacilityModel
	 * @param destination
	 * 		the destination TransportFacilityModel
	 * @return List<TravelRouteModel> list
	 */
	List<TravelRouteModel> findTravelRoutes(String origin, String destination);

	/**
	 * Returns a TravelRouteModel for a give routeCode.
	 *
	 * @param routeCode
	 * 		the code for the travelRoute
	 * @return TravelRouteModel travel route model
	 */
	TravelRouteModel findTravelRoute(String routeCode);
}
