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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Concrete implementation of {@link FareSearchHandler} which is responsible to filter out priced itineraries based on
 * availability so that only one priced itinerary containing one itinerary pricing info is left for each origin destination.
 */
public class DealFilterBundlesHandler implements FareSearchHandler
{
	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			return;
		}

		fareSelectionData.getPricedItineraries().removeIf(pricedItinerary -> !pricedItinerary.isAvailable());

		final Map<Integer, List<PricedItineraryData>> pricedItinerariesGroupedByOriginDestRefNum = fareSelectionData
				.getPricedItineraries().stream().collect(Collectors.groupingBy(PricedItineraryData::getOriginDestinationRefNumber));

		final List<PricedItineraryData> availablePricedItineraries = pricedItinerariesGroupedByOriginDestRefNum.entrySet().stream()
				.map(entry -> getFirstAvailablePricedItinerary(entry.getValue())).filter(Objects::nonNull)
				.collect(Collectors.toList());

		fareSelectionData.setPricedItineraries(availablePricedItineraries);
	}

	protected PricedItineraryData getFirstAvailablePricedItinerary(final List<PricedItineraryData> pricedItineraryDataList)
	{
		for (final PricedItineraryData pricedItinerary : pricedItineraryDataList)
		{
			if (pricedItinerary.getItineraryPricingInfos().stream().anyMatch(ItineraryPricingInfoData::isAvailable))
			{
				return pricedItinerary;
			}
		}
		final PricedItineraryData firstPricedItineraryData = pricedItineraryDataList.get(0);
		firstPricedItineraryData.setAvailable(false);
		return firstPricedItineraryData;
	}
}
