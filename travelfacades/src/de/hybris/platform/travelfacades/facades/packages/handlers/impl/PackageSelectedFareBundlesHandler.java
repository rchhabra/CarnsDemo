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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Concrete implementation of {@link PackageResponseHandler} which checks if there is already a bundle selected, otherwise it
 * chooses the first available option for each origin destination.
 */
public class PackageSelectedFareBundlesHandler implements PackageResponseHandler
{

	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		if (!packageResponseData.isAvailable())
		{
			return;
		}

		final Map<Integer, List<PricedItineraryData>> pricedItinerariesGroupedByOriginDestRefNum = packageResponseData
				.getTransportPackageResponse().getFareSearchResponse().getPricedItineraries().stream()
				.collect(Collectors.groupingBy(PricedItineraryData::getOriginDestinationRefNumber));

		boolean isSelectedForAllOD = true;

		for (final Map.Entry<Integer, List<PricedItineraryData>> entry : pricedItinerariesGroupedByOriginDestRefNum.entrySet())
		{
			final List<PricedItineraryData> pricedItineraries = entry.getValue();
			final boolean isSelected = pricedItineraries.stream().anyMatch(
					pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream()
							.anyMatch(ItineraryPricingInfoData::isSelected));

			isSelectedForAllOD &= isSelected;
		}

		if (isSelectedForAllOD)
		{
			return;
		}

		for (final Map.Entry<Integer, List<PricedItineraryData>> entry : pricedItinerariesGroupedByOriginDestRefNum.entrySet())
		{
			selectPackagePricedItinerary(entry.getValue());
		}
	}

	/**
	 * Selects the package priced itinerary from the list of priced itineraries for given origin destination.
	 *
	 * @param pricedItineraries
	 * 		the priced itineraries
	 */
	protected void selectPackagePricedItinerary(final List<PricedItineraryData> pricedItineraries)
	{
		for (final PricedItineraryData pricedItinerary : pricedItineraries)
		{
			if (pricedItinerary.isAvailable())
			{
				final ItineraryPricingInfoData itineraryPricingInfoData = getFirstAvailableItineraryPricingInfo(pricedItinerary);
				if (Objects.nonNull(itineraryPricingInfoData))
				{
					itineraryPricingInfoData.setSelected(true);
					return;
				}
			}
		}
	}

	/**
	 * Gets the first available itinerary pricing info.
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
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
