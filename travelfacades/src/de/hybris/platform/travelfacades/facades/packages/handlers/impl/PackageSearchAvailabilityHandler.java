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

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;


/**
 * This handler verifies if the package is available and can be displayed in results package search results.
 */
public class PackageSearchAvailabilityHandler implements AccommodationSearchHandler
{

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		if (CollectionUtils.isEmpty(accommodationSearchResponse.getProperties())
				|| !(accommodationSearchRequest instanceof PackageSearchRequestData))
		{
			return;
		}

		final PackageSearchRequestData packageSearchRequest = (PackageSearchRequestData) accommodationSearchRequest;

		final List<PropertyData> availablePackages = new ArrayList<>();

		accommodationSearchResponse.getProperties().stream().filter(property -> property instanceof PackageData)
				.forEach(propertyData ->
				{
					final PackageData packageData = (PackageData) propertyData;

					// Only transportation availability check is performed because there is no stock check performed for
					// accommodations during package search
					final boolean isTransportAvailable = isTransportAvailable(packageSearchRequest, packageData);
					if (isTransportAvailable)
					{
						availablePackages.add(packageData);
					}

				});

		accommodationSearchResponse.setProperties(availablePackages);

	}

	protected boolean isTransportAvailable(final PackageSearchRequestData packageSearchRequest, final PackageData packageData)
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfos = packageData.getFareSelectionData().getPricedItineraries()
				.stream().flatMap(pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream())
				.collect(Collectors.toList());

		return itineraryPricingInfos.stream().allMatch(ItineraryPricingInfoData::isAvailable)
				&& CollectionUtils.size(packageSearchRequest.getFareSearchRequestData().getOriginDestinationInfo()) ==
				CollectionUtils
				.size(packageData.getFareSelectionData().getPricedItineraries());
	}

}
