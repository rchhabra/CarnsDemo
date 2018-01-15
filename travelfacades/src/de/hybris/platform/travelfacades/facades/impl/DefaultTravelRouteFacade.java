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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.facades.TravelRouteFacade} interface.
 */
public class DefaultTravelRouteFacade implements TravelRouteFacade
{
	private TravelRouteService travelRouteService;
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;

	@Override
	public List<TravelRouteData> getTravelRoutes(final String originCode, final String destinationCode)
	{
		final List<TravelRouteModel> travelRoutes = travelRouteService.getTravelRoutes(originCode, destinationCode);
		if (!CollectionUtils.isEmpty(travelRoutes))
		{
			return Converters.convertAll(travelRoutes, travelRouteConverter);
		}
		return Collections.emptyList();
	}

	@Override
	public TravelRouteData getTravelRoute(final String routeCode)
	{
		return travelRouteConverter.convert(travelRouteService.getTravelRoute(routeCode));
	}

	/**
	 * Gets travel route service.
	 *
	 * @return the travel route service
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * Sets travel route service.
	 *
	 * @param travelRouteService
	 * 		the travel route service
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

	/**
	 * Gets travel route converter.
	 *
	 * @return the travel route converter
	 */
	protected Converter<TravelRouteModel, TravelRouteData> getTravelRouteConverter()
	{
		return travelRouteConverter;
	}

	/**
	 * Sets travel route converter.
	 *
	 * @param travelRouteConverter
	 * 		the travel route converter
	 */
	@Required
	public void setTravelRouteConverter(final Converter<TravelRouteModel, TravelRouteData> travelRouteConverter)
	{
		this.travelRouteConverter = travelRouteConverter;
	}

}
