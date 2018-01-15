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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.dao.RuleTravelLocationDao;
import de.hybris.platform.travelrulesengine.dao.RuleTransportFacilityDao;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Search params location rao populator.
 */
public class SearchParamsLocationRaoPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{
	private RuleTravelLocationDao ruleTravelLocationDao;
	private RuleTransportFacilityDao ruleTransportFacilityDao;

	@Override
	public void populate(final FareSearchRequestData source, final SearchParamsRAO target)
			throws ConversionException
	{
		final Set<String> departureLocations = new HashSet<>();
		populateLocations(source.getOriginDestinationInfo().get(0).getDepartureLocation(),
				source.getOriginDestinationInfo().get(0).getDepartureLocationType(), departureLocations);
		target.setOriginLocations(departureLocations);

		final Set<String> arrivalLocations = new HashSet<>();
		populateLocations(source.getOriginDestinationInfo().get(0).getArrivalLocation(),
				source.getOriginDestinationInfo().get(0).getArrivalLocationType(), arrivalLocations);
		target.setDestinationLocations(arrivalLocations);
	}

	/**
	 * Populate locations.
	 *
	 * @param locationString
	 * 		the location string
	 * @param locationType
	 * 		the location type
	 * @param locationSet
	 * 		the location set
	 */
	protected void populateLocations(final String locationString, final LocationType locationType, final Set<String> locationSet)
	{
		final LocationModel location;
		if(LocationType.AIRPORTGROUP.equals(locationType))
		{
			final TransportFacilityModel transportFacility = getRuleTransportFacilityDao().findTransportFacility(locationString);
			if(Objects.isNull(transportFacility))
			{
				return;
			}
			location = transportFacility.getLocation();
		}
		else
		{
			location = getRuleTravelLocationDao().findLocation(locationString);
		}
		populateLocationsFromLocation(location, locationSet);
	}

	/**
	 * Populates locations from location.
	 *
	 * @param location
	 * 		the location
	 * @param locationSet
	 * 		the location set
	 */
	protected void populateLocationsFromLocation(final LocationModel location, final Set<String> locationSet)
	{
		if(Objects.isNull(location))
		{
			return;
		}
		locationSet.add(location.getCode());
		location.getSuperlocations().forEach(superLocation -> populateLocationsFromLocation(superLocation, locationSet));
	}

	/**
	 * Gets rule travel location dao.
	 *
	 * @return the rule travel location dao
	 */
	protected RuleTravelLocationDao getRuleTravelLocationDao()
	{
		return ruleTravelLocationDao;
	}

	/**
	 * Sets rule travel location dao.
	 *
	 * @param ruleTravelLocationDao
	 * 		the rule travel location dao
	 */
	@Required
	public void setRuleTravelLocationDao(final RuleTravelLocationDao ruleTravelLocationDao)
	{
		this.ruleTravelLocationDao = ruleTravelLocationDao;
	}

	/**
	 * Gets rule transport facility dao.
	 *
	 * @return the rule transport facility dao
	 */
	protected RuleTransportFacilityDao getRuleTransportFacilityDao()
	{
		return ruleTransportFacilityDao;
	}

	/**
	 * Sets rule transport facility dao.
	 *
	 * @param ruleTransportFacilityDao
	 * 		the rule transport facility dao
	 */
	@Required
	public void setRuleTransportFacilityDao(final RuleTransportFacilityDao ruleTransportFacilityDao)
	{
		this.ruleTransportFacilityDao = ruleTransportFacilityDao;
	}
}
