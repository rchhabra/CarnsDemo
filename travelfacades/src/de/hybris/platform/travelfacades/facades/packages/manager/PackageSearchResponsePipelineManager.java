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

package de.hybris.platform.travelfacades.facades.packages.manager;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;

import java.util.List;


/**
 * Pipeline Manager class that will build a {@link PackageSearchResponseData} after executing a list of handlers on the
 * list of {@link AccommodationOfferingDayRateData} and {@link PackageSearchRequestData} given as inputs
 */
public interface PackageSearchResponsePipelineManager
{

	/**
	 * Execute pipeline.
	 *
	 * @param accommodationOfferingDayRates
	 *           the accommodation offering day rates
	 * @param packageSearchRequestData
	 *           the package search request data
	 * @param fareSelectionData
	 *           the fare selection data
	 * @return the package search response data
	 */
	PackageSearchResponseData executePipeline(List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			PackageSearchRequestData packageSearchRequestData);
}
