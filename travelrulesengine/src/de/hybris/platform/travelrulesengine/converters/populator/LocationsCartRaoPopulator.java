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
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;


/**
 * This populator populates origin and destination location to the CartRao
 */
public class LocationsCartRaoPopulator implements Populator<AbstractOrderModel, CartRAO>
{

	@Override
	public void populate(final AbstractOrderModel source, final CartRAO target) throws ConversionException
	{
		if (Objects.isNull(source) || CollectionUtils.isEmpty(source.getEntries()))
		{
			return;
		}
		final Set<String> departureLocations = new HashSet<>();
		final Set<String> arrivalLocations = new HashSet<>();
		final Optional<AbstractOrderEntryModel> fareProductEntry = source.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null)
				.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0).findAny();
		if (fareProductEntry.isPresent())
		{
			populateLocationsFromLocation(
					fareProductEntry.get().getTravelOrderEntryInfo().getTravelRoute().getOrigin().getLocation(),
						departureLocations);
			populateLocationsFromLocation(
					fareProductEntry.get().getTravelOrderEntryInfo().getTravelRoute().getDestination().getLocation(),
						arrivalLocations);
		}
		target.setOriginLocations(departureLocations);
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

}
