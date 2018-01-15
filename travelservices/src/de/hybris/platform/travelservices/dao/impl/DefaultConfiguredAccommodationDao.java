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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.dao.ConfiguredAccommodationDao;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class DefaultConfiguredAccommodationDao extends DefaultGenericDao<ConfiguredAccommodationModel>
		implements ConfiguredAccommodationDao
{

	public DefaultConfiguredAccommodationDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<ConfiguredAccommodationModel> findAccommodationMapConfiguration(
			final AccommodationMapModel accommodationMap, final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(accommodationMap, "Accommodation Map must not be null!");
		validateParameterNotNull(catalogVersion, "catalog version must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ConfiguredAccommodationModel.ACCOMMODATIONMAP, accommodationMap);
		params.put(ConfiguredAccommodationModel.CATALOGVERSION, catalogVersion);
		return find(params);
	}

	@Override
	public ConfiguredAccommodationModel findAccommodation(final String uid, final CatalogVersionModel catalogVersion)
			throws AccommodationMapDataSetUpException
	{
		validateParameterNotNull(uid, "uid must not be null!");
		validateParameterNotNull(catalogVersion, "catalog version must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ConfiguredAccommodationModel.UID, uid);
		params.put(ConfiguredAccommodationModel.CATALOGVERSION, catalogVersion);
		final List<ConfiguredAccommodationModel> accommodations = find(params);
		if (CollectionUtils.isEmpty(accommodations))
		{
			throw new AccommodationMapDataSetUpException("No accommodation found for uid : " + uid);
		}
		if (accommodations.size() > 1)
		{
			throw new AccommodationMapDataSetUpException("More than one accommodation found for uid : " + uid);
		}
		return accommodations.get(0);
	}
}
