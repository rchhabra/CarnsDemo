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
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Concrete implementation of {@link FareSearchHandler} which is responsible to filter out priced itineraries based on
 * availability so that only one priced itinerary containing one itinerary pricing info is left for each origin
 * destination.
 */
public class PackageFilterBundlesHandler implements FareSearchHandler
{
	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			return;
		}

		final Map<Integer, List<PricedItineraryData>> pricedItinerariesGroupedByOriginDestRefNum = fareSelectionData
				.getPricedItineraries().stream().collect(Collectors.groupingBy(PricedItineraryData::getOriginDestinationRefNumber));

		final List<PricedItineraryData> packagePricedItineraries = new ArrayList<>();

		for (final Entry<Integer, List<PricedItineraryData>> entry : pricedItinerariesGroupedByOriginDestRefNum.entrySet())
		{
			final List<PricedItineraryData> pricedItineraries = entry.getValue();
			pricedItineraries.removeIf(pricedItineraryData -> !pricedItineraryData.isAvailable());

			if (CollectionUtils.isNotEmpty(pricedItineraries))
			{
				final PricedItineraryData packagePricedItinerary = getPackagePricedItinerary(pricedItineraries);
				if (Objects.nonNull(packagePricedItinerary))
				{
					packagePricedItineraries.add(packagePricedItinerary);
				}
			}
		}

		fareSelectionData.setPricedItineraries(packagePricedItineraries);
	}

	protected PricedItineraryData getPackagePricedItinerary(final List<PricedItineraryData> pricedItineraries)
	{
		for (final PricedItineraryData pricedItinerary : pricedItineraries)
		{
				final ItineraryPricingInfoData itineraryPricingInfoData = getFirstAvailableItineraryPricingInfo(pricedItinerary);
				if (Objects.nonNull(itineraryPricingInfoData))
				{
					pricedItinerary.setItineraryPricingInfos(Collections.singletonList(itineraryPricingInfoData));
					return pricedItinerary;
				}
		}

		return null;
	}

	/**
	 * Gets the first available itinerary pricing info.
	 *
	 * @param pricedItinerary
	 *           the priced itinerary
	 * @return the first available itinerary pricing info
	 */
	protected ItineraryPricingInfoData getFirstAvailableItineraryPricingInfo(final PricedItineraryData pricedItinerary)
	{
		for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
		{
			if (itineraryPricingInfoData.isAvailable())
			{
				final boolean isAvailable = itineraryPricingInfoData.getBundleTemplates().stream()
						.allMatch(TravelBundleTemplateData::isAvailable);
				if (isAvailable)
				{
					return itineraryPricingInfoData;
				}
			}
		}
		return null;
	}
}
