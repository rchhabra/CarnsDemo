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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.TravelSectorDao;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class DefaultTravelSectorDao extends DefaultGenericDao<TravelSectorModel>implements TravelSectorDao
{

	public DefaultTravelSectorDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public TravelSectorModel findTravelSector(final TransportFacilityModel origin, final TransportFacilityModel destination)
	{
		validateParameterNotNull(origin, "Travel Sector origin must not be null!");
		validateParameterNotNull(destination, "Travel Sector destination must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravelSectorModel.ORIGIN, origin);
		params.put(TravelSectorModel.DESTINATION, destination);
		final List<TravelSectorModel> travelSectorModel = find(params);
		if (CollectionUtils.isNotEmpty(travelSectorModel))
		{
			return travelSectorModel.get(0);
		}
		return null;
	}

}
