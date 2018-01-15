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
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelservices.jalo.warehouse.AccommodationOffering;

import java.util.List;
import java.util.Map.Entry;


/**
 * Pipeline Manager class that will return a {@link PackageData} after executing a list of handlers on a map entry
 * related to an {@link AccommodationOffering} and {@link AccommodationSearchRequestData} given as inputs
 */
public interface PackagePipelineManager
{

	/**
	 * Execute pipeline.
	 *
	 * @param entry
	 *           the entry
	 * @param accommodationSearchRequest
	 *           the accommodation search request
	 * @return the package data
	 */
	PackageData executePipeline(Entry<String, List<AccommodationOfferingDayRateData>> entry,
			AccommodationSearchRequestData accommodationSearchRequest);

}
