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
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete Implementation of the {@link AccommodationSearchHandler} class. Handler is responsible for getting the
 * FareSelectionData and link the same to each package
 */
public class PackageFareSearchResponseHandler implements AccommodationSearchHandler
{
	private FareSearchFacade packageSearchFareSearchFacade;

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		if (accommodationSearchRequest instanceof PackageSearchRequestData)
		{
			final PackageSearchRequestData packageSearchRequestData = (PackageSearchRequestData) accommodationSearchRequest;
			final FareSelectionData fareSelectionData = getPackageSearchFareSearchFacade()
					.doSearch(packageSearchRequestData.getFareSearchRequestData());
			for (final PropertyData propertyData : accommodationSearchResponse.getProperties())
			{
				if (propertyData instanceof PackageData)
				{
					final PackageData packageData = (PackageData) propertyData;
					packageData.setFareSelectionData(fareSelectionData);
				}
			}
		}
	}

	/**
	 * Gets package search fare search facade.
	 *
	 * @return the package search fare search facade
	 */
	protected FareSearchFacade getPackageSearchFareSearchFacade()
	{
		return packageSearchFareSearchFacade;
	}

	/**
	 * Sets package search fare search facade.
	 *
	 * @param packageSearchFareSearchFacade
	 * 		the package search fare search facade
	 */
	@Required
	public void setPackageSearchFareSearchFacade(final FareSearchFacade packageSearchFareSearchFacade)
	{
		this.packageSearchFareSearchFacade = packageSearchFareSearchFacade;
	}
}
