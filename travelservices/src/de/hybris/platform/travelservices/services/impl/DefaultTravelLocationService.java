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
 */

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.dao.TravelLocationDao;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.services.TravelLocationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TravelLocationService}
 */
public class DefaultTravelLocationService implements TravelLocationService
{

	private TravelLocationDao travelLocationDao;

	@Override
	public LocationModel getLocation(final String code)
	{
		return getTravelLocationDao().findLocation(code);
	}

	protected TravelLocationDao getTravelLocationDao()
	{
		return travelLocationDao;
	}

	@Required
	public void setTravelLocationDao(final TravelLocationDao travelLocationDao)
	{
		this.travelLocationDao = travelLocationDao;
	}
}
