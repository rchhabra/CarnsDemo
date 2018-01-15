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

import de.hybris.platform.travelservices.model.travel.TravelRouteModel;

import java.util.List;


/**
 * TravelRoute Service interface which provides TravelRoute specific services
 */
public interface TravelRouteService
{

	/**
	 * Method returns a list of TravelRouteModel
	 *
	 * @param originCode
	 * 		the TransportFacility code for origin
	 * @param destinationCode
	 * 		the TransportFacility code for destination
	 * @return List<TravelRouteModel> travel routes
	 */
	List<TravelRouteModel> getTravelRoutes(String originCode, String destinationCode);

	/**
	 * Returns a TravelRouteModel for a give routeCode.
	 *
	 * @param routeCode
	 * 		the code for the travelRoute
	 * @return TravelRouteModel travel route
	 */
	TravelRouteModel getTravelRoute(String routeCode);
}
