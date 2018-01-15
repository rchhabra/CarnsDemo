package de.hybris.platform.travelrulesengine.dao.impl;/*
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
 *
 */

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.travelrulesengine.dao.RuleTravelLocationDao;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * The type Default rule travel location dao.
 */
public class DefaultRuleTravelLocationDao extends DefaultGenericDao<LocationModel> implements RuleTravelLocationDao
{
	private static final Logger LOG = Logger.getLogger(DefaultRuleTravelLocationDao.class);

	/**
	 * Instantiates a new Default rule travel location dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultRuleTravelLocationDao(final String typecode)
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
