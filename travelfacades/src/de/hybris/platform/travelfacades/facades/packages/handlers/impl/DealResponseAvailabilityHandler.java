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
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Extension of {@link DealPackageResponseAvailabilityHandler} to accommodate different logic to check transport part
 * availability on Deal Details. This is due to the fact that the whole fare selection is returned and not only the transport
 * options included in the deal.
 */
public class DealResponseAvailabilityHandler extends DealPackageResponseAvailabilityHandler implements PackageResponseHandler
{
	@Override
	protected boolean isTransportPackageResponseAvailable(final PackageRequestData packageRequestData,
			final PackageResponseData packageResponseData)
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfos = packageResponseData.getTransportPackageResponse()
				.getFareSearchResponse().getPricedItineraries().stream().flatMap(
						pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream()
								.filter(ItineraryPricingInfoData::isSelected)).collect(Collectors.toList());
		return itineraryPricingInfos.stream().allMatch(ItineraryPricingInfoData::isAvailable) && CollectionUtils
				.size(packageRequestData.getTransportPackageRequest().getFareSearchRequest().getOriginDestinationInfo())
				== CollectionUtils.size(itineraryPricingInfos);
	}
}
