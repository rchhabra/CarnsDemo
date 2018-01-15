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
package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for
 * populating the Itinerary identifier {@link ItineraryPricingInfoData}
 */
public class ItineraryIdentifierHandler implements FareSearchHandler
{
	private FareSearchHashResolver fareSearchHashResolver;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		populateItineraryPricingInfoIdentifier(
				fareSelectionData.getPricedItineraries().stream().flatMap(entry -> entry.getItineraryPricingInfos().stream()).collect(
						Collectors.toList()));
	}

	/**
	 * This method populate the itinerary identifier for each {@link ItineraryPricingInfoData} provided
	 *
	 * @param itineraryPricingInfos
	 * 		the List of ItineraryPricingInfoData
	 */
	protected void populateItineraryPricingInfoIdentifier(final List<ItineraryPricingInfoData> itineraryPricingInfos)
	{
		for (final ItineraryPricingInfoData itineraryPricingInfoData : itineraryPricingInfos)
		{
			itineraryPricingInfoData
					.setItineraryIdentifier(getFareSearchHashResolver().generateIdentifier(itineraryPricingInfoData));
		}
	}

	/**
	 * Gets fare search hash resolver.
	 *
	 * @return the fare search hash resolver
	 */
	protected FareSearchHashResolver getFareSearchHashResolver()
	{
		return fareSearchHashResolver;
	}

	/**
	 * Sets fare search hash resolver.
	 *
	 * @param fareSearchHashResolver
	 * 		the fare search hash resolver
	 */
	@Required
	public void setFareSearchHashResolver(final FareSearchHashResolver fareSearchHashResolver)
	{
		this.fareSearchHashResolver = fareSearchHashResolver;
	}
}
