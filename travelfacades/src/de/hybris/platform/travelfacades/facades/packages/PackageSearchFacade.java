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

package de.hybris.platform.travelfacades.facades.packages;

import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;

import java.util.List;


/**
 * Interface for the Package Search Facade
 */
public interface PackageSearchFacade
{

	/**
	 * Do search for packages.
	 *
	 * @param packageSearchRequestData
	 *           the package search request data
	 * @return AccommodationSearchRequestData object with accommodation offering matching request parameters
	 */
	PackageSearchResponseData doSearch(PackageSearchRequestData packageSearchRequestData);

	/**
	 * Gets the min priced package.
	 *
	 * @param packages
	 *           the packages
	 * @return the min priced package
	 */
	PackageData getMinPricedPackage(List<PropertyData> packages);

	/**
	 * Gets the max priced package.
	 *
	 * @param packages
	 *           the packages
	 * @return the max priced package
	 */
	PackageData getMaxPricedPackage(List<PropertyData> packages);

	/**
	 * Gets the filtered package response filtered by price range.
	 *
	 * @param packageSearchRequestData
	 *           the package search request data
	 * @return the filtered package response filtered by price range
	 */
	PackageSearchResponseData getFilteredPackageResponseFilteredByPriceRange(PackageSearchRequestData packageSearchRequestData);
}
