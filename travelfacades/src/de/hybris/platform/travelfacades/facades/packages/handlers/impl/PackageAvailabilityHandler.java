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

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Concrete implementation of {@link PackageResponseHandler} which checks if the package is available for selection
 */
public class PackageAvailabilityHandler implements PackageResponseHandler
{
	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = packageResponseData
				.getAccommodationPackageResponse().getAccommodationAvailabilityResponse();

		final boolean isAccommodationAvailable =
				CollectionUtils.isNotEmpty(accommodationAvailabilityResponse.getReservedRoomStays())
						&& accommodationAvailabilityResponse.getRoomStays().stream().anyMatch(
						roomStay -> roomStay.getRatePlans().stream().anyMatch(ratePlan -> ratePlan.getAvailableQuantity() > 0));

		final boolean isTransportAvailable = isTransportAvailable(packageResponseData);

		packageResponseData.setAvailable(isAccommodationAvailable && isTransportAvailable);

	}

	/**
	 * First check if in case there is something already selected in cart, it is still available. Then make sure that at least 1
	 * itinerary pricing info is available.
	 *
	 * @param packageResponseData
	 * 		package response data
	 * @return availability of transportation response
	 */
	protected boolean isTransportAvailable(final PackageResponseData packageResponseData)
	{
		final List<PricedItineraryData> pricedItineraries = packageResponseData.getTransportPackageResponse()
				.getFareSearchResponse().getPricedItineraries();

		if (CollectionUtils.isEmpty(pricedItineraries))
		{
			return Boolean.FALSE;
		}

		final boolean isSelectedUnavailable = pricedItineraries.stream().anyMatch(
				pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream().anyMatch(
						itineraryPricingInfoData -> itineraryPricingInfoData.isSelected() && !itineraryPricingInfoData.isAvailable()));

		// In case at least one of the selected options is no longer available, further evaluation of this package won't happen.
		if (isSelectedUnavailable)
		{
			return Boolean.FALSE;
		}

		return pricedItineraries.stream().anyMatch(pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream()
				.anyMatch(ItineraryPricingInfoData::isAvailable));
	}

}
