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
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete Implementation of the {@link AccommodationSearchHandler} class. Handler is responsible for setting filter
 * property on packges based upon the price range
 */
public class PackagePriceRangeFilterHandler implements AccommodationSearchHandler
{

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		if (accommodationSearchRequest instanceof PackageSearchRequestData)
		{
			final PackageSearchRequestData packageSearchRequestData = (PackageSearchRequestData) accommodationSearchRequest;
			final Long lowerPriceRange = packageSearchRequestData.getMinPrice();
			final Long upperPriceRange = packageSearchRequestData.getMaxPrice();

			if (CollectionUtils.isEmpty(accommodationSearchResponse.getProperties()))
			{
				return;
			}
			final List<PropertyData> packages = accommodationSearchResponse.getProperties().stream()
					.filter(propertyData -> propertyData instanceof PackageData).collect(Collectors.toList());

			packages.forEach(propertyData -> {
				final PackageData packageData = (PackageData) propertyData;
				final double totalPackagePrice = Objects.nonNull(packageData.getTotalPackagePrice())
						? packageData.getTotalPackagePrice().getValue().doubleValue() : 0;
				if ((Objects.isNull(lowerPriceRange) || Objects.isNull(upperPriceRange))
						|| (totalPackagePrice >= lowerPriceRange && totalPackagePrice <= upperPriceRange))
				{
					packageData.setFiltered(true);
				}
				else
				{
					packageData.setFiltered(false);
				}
			});
		}
	}
}
