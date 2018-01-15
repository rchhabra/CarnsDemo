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


import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.dao.RuleTransportFacilityDao;
import de.hybris.platform.travelrulesengine.dao.RuleTravelLocationDao;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type originating locations cart rao populator. This class should connect to a service that is able to retrieve the
 * Originating Locations based on the IP address set in the Session.
 */
public class OriginatingLocationsCartRaoPopulator implements Populator<AbstractOrderModel, CartRAO>
{
	private RuleTravelLocationDao ruleTravelLocationDao;
	private RuleTransportFacilityDao ruleTransportFacilityDao;

	@Override
	public void populate(final AbstractOrderModel source, final CartRAO target)
			throws ConversionException
	{
		if(Objects.isNull(source) || CollectionUtils.isEmpty(source.getEntries()))
		{
			return;
		}

		if (!BookingJourneyType.BOOKING_ACCOMMODATION_ONLY.equals(source.getBookingJourneyType()))
		{
			// MOCK OriginatingLocations
			// We assume that the location from which the request is originated is corresponding to the departure location.
			final Optional<AbstractOrderEntryModel> fareProductEntry = source.getEntries().stream()
					.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
							|| entry.getProduct() instanceof FareProductModel)
					.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()))
					.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().equals(0)).findAny();

			if(!fareProductEntry.isPresent())
			{
				return;
			}

			final TransportFacilityModel origin = fareProductEntry.get().getTravelOrderEntryInfo().getTravelRoute().getOrigin();

			final Set<String> departureLocations = new HashSet<>();
			populateLocationsFromLocation(origin.getLocation(), departureLocations);

			target.setOriginatingLocations(departureLocations);
		}
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
