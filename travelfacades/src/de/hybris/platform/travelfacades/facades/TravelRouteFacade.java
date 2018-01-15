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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.TravelRouteData;

import java.util.List;


/**
 * Facade that exposes Travel Route specific services
 */
public interface TravelRouteFacade
{
	/**
	 * Returns a list of routes for given origin and destination
	 *
	 * @param originCode
	 * 		- origin transport facility
	 * @param destinationCode
	 * 		- destination transport facility
	 * @return list of applicable routes
	 */
	List<TravelRouteData> getTravelRoutes(final String originCode, final String destinationCode);

	/**
	 * Returs a route for given unique code
	 *
	 * @param routeCode
	 * 		- unique code of the route
	 * @return route travel route
	 */
	TravelRouteData getTravelRoute(final String routeCode);
}
