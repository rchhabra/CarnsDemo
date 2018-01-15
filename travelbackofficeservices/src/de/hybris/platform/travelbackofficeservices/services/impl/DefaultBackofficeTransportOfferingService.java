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
 *
 */
package de.hybris.platform.travelbackofficeservices.services.impl;

import de.hybris.platform.travelbackofficeservices.dao.BackofficeTransportOfferingDao;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.impl.DefaultTransportOfferingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default backoffice transport offering service.
 */
public class DefaultBackofficeTransportOfferingService extends DefaultTransportOfferingService implements BackofficeTransportOfferingService
{
	private BackofficeTransportOfferingDao backofficeTransportOfferingDao;

	@Override
	public void updateTransportOfferingsWithLocations(final List<TransportOfferingModel> transportOfferings)
	{
		for (final TransportOfferingModel transportOffering : transportOfferings)
		{
			final TravelSectorModel travelSector = transportOffering.getTravelSector();
			if (Objects.isNull(travelSector))
			{
				continue;
			}
			final LocationModel originLocation = travelSector.getOrigin().getLocation();
			final LocationModel destinationLocation = travelSector.getDestination().getLocation();
			transportOffering.setOriginLocations(getLocations(originLocation));
			transportOffering.setDestinationLocations(getLocations(destinationLocation));
			transportOffering.setOriginTransportFacility(travelSector.getOrigin());
			transportOffering.setDestinationTransportFacility(travelSector.getDestination());
		}

		getModelService().saveAll(transportOfferings);
	}

	@Override
	public List<TransportOfferingModel> findTransportOfferingsWithoutSchedule()
	{
		return getBackofficeTransportOfferingDao().findTransportOfferingsWithoutSchedule();
	}

	/**
	 * Gets locations.
	 *
	 * @param location
	 * 		the location
	 * @return the locations
	 */
	protected List<LocationModel> getLocations(LocationModel location)
	{
		final List<LocationModel> locations = new ArrayList<>();
		locations.add(location);
		if (location != null)
		{
			do
			{
				location = CollectionUtils.isNotEmpty(location.getSuperlocations()) ? location.getSuperlocations().get(0) : null;
				if (Objects.nonNull(location))
				{
					locations.add(location);
				}
			}
			while (location != null);
		}
		return locations;
	}

	/**
	 *
	 * @return backofficeTransportOfferingDao
	 */
	protected BackofficeTransportOfferingDao getBackofficeTransportOfferingDao()
	{
		return backofficeTransportOfferingDao;
	}

	/**
	 *
	 * @param backofficeTransportOfferingDao the backofficeTransportOfferingDao to set
	 */
	@Required
	public void setBackofficeTransportOfferingDao(
			final BackofficeTransportOfferingDao backofficeTransportOfferingDao)
	{
		this.backofficeTransportOfferingDao = backofficeTransportOfferingDao;
	}
}
