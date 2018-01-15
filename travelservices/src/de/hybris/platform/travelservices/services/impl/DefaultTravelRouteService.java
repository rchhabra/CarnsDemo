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
package de.hybris.platform.travelservices.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TravelRouteService}.
 */
public class DefaultTravelRouteService implements TravelRouteService
{
	private TravelRouteDao travelRouteDao;

	@Override
	public List<TravelRouteModel> getTravelRoutes(final String originCode, final String destinationCode)
	{
		validateParameterNotNull(originCode, "Parameter originCode cannot be null");
		validateParameterNotNull(destinationCode, "Parameter destinationCode cannot be null");

		final List<TravelRouteModel> travelRoutes = new ArrayList<>();

		travelRoutes.addAll(travelRouteDao.findTravelRoutes(originCode, destinationCode));

		return travelRoutes;
	}

	/**
	 * Returns TravelRouteModel for a given route code.
	 *
	 * @param routeCode
	 *           String representing route code.
	 * @return TravelRouteModel
	 */
	@Override
	public TravelRouteModel getTravelRoute(final String routeCode)
	{
		return travelRouteDao.findTravelRoute(routeCode);
	}

	/**
	 * Gets travel route dao.
	 *
	 * @return the travel route dao
	 */
	protected TravelRouteDao getTravelRouteDao()
	{
		return travelRouteDao;
	}

	/**
	 * Sets travel route dao.
	 *
	 * @param travelRouteDao
	 * 		the travel route dao
	 */
	@Required
	public void setTravelRouteDao(final TravelRouteDao travelRouteDao)
	{
		this.travelRouteDao = travelRouteDao;
	}

}
