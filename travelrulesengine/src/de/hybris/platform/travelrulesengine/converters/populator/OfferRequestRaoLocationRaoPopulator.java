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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.dao.RuleTransportFacilityDao;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class populates origin and destination locations in a OfferRequestRao
 */
public class OfferRequestRaoLocationRaoPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{
	private RuleTransportFacilityDao ruleTransportFacilityDao;

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		final Set<String> departureLocations = new HashSet<>();
		final TransportFacilityModel originTransportFacilityModel = getRuleTransportFacilityDao().findTransportFacility(source.getItineraries().get(0).getRoute().getOrigin().getCode());
		populateLocationsFromLocation(originTransportFacilityModel.getLocation(), departureLocations);
		target.setOriginLocations(departureLocations);

		final Set<String> arrivalLocations = new HashSet<>();
		final TransportFacilityModel destinationTransportFacilityModel = getRuleTransportFacilityDao()
				.findTransportFacility(source.getItineraries().get(0).getRoute().getDestination().getCode());
		populateLocationsFromLocation(destinationTransportFacilityModel.getLocation(), arrivalLocations);
		target.setDestinationLocations(arrivalLocations);
	}

	/**
	 * Populates locations from location.
	 *
	 * @param location
	 *           the location
	 * @param locationSet
	 *           the location set
	 */
	protected void populateLocationsFromLocation(final LocationModel location, final Set<String> locationSet)
	{
		if (Objects.isNull(location))
		{
			return;
		}
		locationSet.add(location.getCode());
		location.getSuperlocations().forEach(superLocation -> populateLocationsFromLocation(superLocation, locationSet));
	}

	/**
	 * @return the ruleTransportFacilityDao
	 */
	protected RuleTransportFacilityDao getRuleTransportFacilityDao()
	{
		return ruleTransportFacilityDao;
	}

	/**
	 * @param ruleTransportFacilityDao
	 *           the ruleTransportFacilityDao to set
	 */
	@Required
	public void setRuleTransportFacilityDao(final RuleTransportFacilityDao ruleTransportFacilityDao)
	{
		this.ruleTransportFacilityDao = ruleTransportFacilityDao;
	}

}
