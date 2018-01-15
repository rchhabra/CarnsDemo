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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.TravelLocationDao;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.travelservices.services.impl.DefaultTravelLocationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link TravelLocationDao}
 */
public class DefaultTravelLocationDao extends DefaultGenericDao<LocationModel> implements TravelLocationDao
{

	private static final Logger LOG = Logger.getLogger(DefaultTravelLocationDao.class);

	public DefaultTravelLocationDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public LocationModel findLocation(final String code)
	{
		validateParameterNotNull(code, "Location code must not be null!");

		final List<LocationModel> locationModels = find(Collections.singletonMap(LocationModel.CODE, (Object) code));
		if (CollectionUtils.isEmpty(locationModels))
		{
			LOG.info("No result for the given query");
			return null;
		}
		else if (locationModels.size() > 1)
		{
			LOG.warn("Found " + locationModels.size() + " results for the given query");
			return null;
		}

		return locationModels.get(0);
	}
}
