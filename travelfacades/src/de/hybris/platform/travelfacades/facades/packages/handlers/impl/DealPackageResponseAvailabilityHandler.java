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
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.StandardPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Check and set the availability of the packageResponseData. It will be set available if TransportPackageResponse,
 * AccommodationPackageResponse and StandardPackageResponses are all available
 */
public class DealPackageResponseAvailabilityHandler implements PackageResponseHandler
{
	private AccommodationOfferingFacade accommodationOfferingFacade;

	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		final boolean isTransportPackageResponseAvailable = isTransportPackageResponseAvailable(packageRequestData,
				packageResponseData);
		final boolean isAccommodationPackageResponseAvailable = isAccommodationPackageResponseAvailable(packageResponseData);
		final boolean isStandardPackageResponsesAvailable = isStandardPackageResponsesAvailable(packageResponseData);

		packageResponseData.setAvailable(isTransportPackageResponseAvailable && isAccommodationPackageResponseAvailable
				&& isStandardPackageResponsesAvailable);
	}

	/**
	 * Returns the availability status of the {@link TransportPackageResponseData}. It returns true if all the
	 * {@link ItineraryPricingInfoData} are available, otherwise false.
	 *
	 * @param packageRequestData
	 *           as the packageRequestData
	 * @param packageResponseData
	 *           as the packageResponseData
	 *
	 * @return true if all the itineraryPricingInfos are available, false otherwise
	 */
	protected boolean isTransportPackageResponseAvailable(final PackageRequestData packageRequestData,
			final PackageResponseData packageResponseData)
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfos = packageResponseData.getTransportPackageResponse()
				.getFareSearchResponse().getPricedItineraries().stream()
				.flatMap(pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream()).collect(Collectors.toList());
		return itineraryPricingInfos.stream().allMatch(ItineraryPricingInfoData::isAvailable) && CollectionUtils.size(
				packageRequestData.getTransportPackageRequest().getFareSearchRequest().getOriginDestinationInfo()) == CollectionUtils
						.size(packageResponseData.getTransportPackageResponse().getFareSearchResponse().getPricedItineraries());
	}

	/**
	 * Returns the availability status of the {@link AccommodationPackageResponseData}.
	 *
	 * @param packageResponseData
	 * 		as the packageResponseData
	 *
	 * @return true if the accommodation is available, false otherwise
	 */
	protected boolean isAccommodationPackageResponseAvailable(final PackageResponseData packageResponseData)
	{
		return getAccommodationOfferingFacade().isAccommodationAvailableForQuickSelection(
				packageResponseData.getAccommodationPackageResponse().getAccommodationAvailabilityResponse());
	}

	/**
	 * Returns the availability status of the {@link StandardPackageResponseData}. It returns true if all the productData
	 * in the {@link PackageProductData} are available, false otherwise. A productData is available if its stockLevel is
	 * greater than 0 if it is null (force in stock).
	 *
	 * @param packageResponseData
	 * 		as the packageResponseData
	 *
	 * @return true if all the productData are available, false otherwise
	 */
	protected boolean isStandardPackageResponsesAvailable(final PackageResponseData packageResponseData)
	{
		return packageResponseData.getStandardPackageResponses().stream()
				.flatMap(response -> response.getPackageProducts().stream())
				.allMatch(packageProductData -> packageProductData.getProduct().getStock() == null
						|| packageProductData.getProduct().getStock().getStockLevel() > 0);
	}

	/**
	 * @return the accommodationOfferingFacade
	 */
	protected AccommodationOfferingFacade getAccommodationOfferingFacade()
	{
		return accommodationOfferingFacade;
	}

	/**
	 * @param accommodationOfferingFacade
	 * 		the accommodationOfferingFacade to set
	 */
	@Required
	public void setAccommodationOfferingFacade(final AccommodationOfferingFacade accommodationOfferingFacade)
	{
		this.accommodationOfferingFacade = accommodationOfferingFacade;
	}
}
