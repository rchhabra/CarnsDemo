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

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.PropertyFacilityDao;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.services.PropertyFacilityService;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link PropertyFacilityService}
 */
public class DefaultPropertyFacilityService implements PropertyFacilityService
{
	private static final Logger LOG = Logger.getLogger(DefaultPropertyFacilityService.class);

	private PropertyFacilityDao propertyFacilityDao;

	@Override
	public PropertyFacilityModel getPropertyFacility(final String code)
	{

		try
		{
			return getPropertyFacilityDao().findPropertyFacility(code);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error(String.format("Code %s doesn't belong to any known accommodation offering instance.", code), e);
		}
		return null;
	}

	@Override
	public List<PropertyFacilityModel> getPropertyFacilities()
	{
		return getPropertyFacilityDao().findPropertyFacilities();
	}

	@Override
	public SearchResult<PropertyFacilityModel> getPropertyFacilities(final int batchSize, final int offset)
	{
		return getPropertyFacilityDao().findPropertyFacilities(batchSize, offset);
	}

	/**
	 * @return the propertyFacilityDao
	 */
	protected PropertyFacilityDao getPropertyFacilityDao()
	{
		return propertyFacilityDao;
	}

	/**
	 * @param propertyFacilityDao
	 *           the propertyFacilityDao to set
	 */
	public void setPropertyFacilityDao(final PropertyFacilityDao propertyFacilityDao)
	{
		this.propertyFacilityDao = propertyFacilityDao;
	}
}
