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

import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of TransportFacilityService
 */
public class DefaultTransportFacilityService implements TransportFacilityService
{

	private TransportFacilityDao transportFacilityDao;

	@Override
	public TransportFacilityModel getTransportFacility(final String code)
	{
		return transportFacilityDao.findTransportFacility(code);
	}

	@Override
	public LocationModel getCountry(final TransportFacilityModel transportFacility)
	{
		final LocationModel locationModel = transportFacility.getLocation();
		if (locationModel == null)
		{
			return null;
		}

		if (LocationType.COUNTRY.getCode().equalsIgnoreCase(locationModel.getLocationType().getCode()))
		{
			return locationModel;
		}

		for (final LocationModel location : locationModel.getSuperlocations())
		{
			if (LocationType.COUNTRY.getCode().equalsIgnoreCase(location.getLocationType().getCode()))
			{
				return location;
			}
		}
		return null;
	}

	@Override
	public LocationModel getCity(final TransportFacilityModel transportFacility)
	{
		final LocationModel locationModel = transportFacility.getLocation();
		if (locationModel == null)
		{
			return null;
		}

		if (LocationType.CITY.getCode().equalsIgnoreCase(locationModel.getLocationType().getCode()))
		{
			return locationModel;
		}

		for (final LocationModel location : locationModel.getSuperlocations())
		{
			if (LocationType.CITY.getCode().equalsIgnoreCase(location.getLocationType().getCode()))
			{
				return location;
			}
		}
		return null;
	}

	/**
	 * @return the transportFacilityDao
	 */
	protected TransportFacilityDao getTransportFacilityDao()
	{
		return transportFacilityDao;
	}

	/**
	 * @param transportFacilityDao
	 *           the transportFacilityDao to set
	 */
	@Required
	public void setTransportFacilityDao(final TransportFacilityDao transportFacilityDao)
	{
		this.transportFacilityDao = transportFacilityDao;
	}

}
