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
 *
 */
package de.hybris.platform.travelfacades.fare.search;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;

import java.util.Map;


/**
 * Interface for the Fare Search Facade
 */
public interface FareSearchFacade
{

	/**
	 * Performs a search for fare selection options based on fare search request
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @return FareSelectionData object with available fare options
	 */
	FareSelectionData doSearch(FareSearchRequestData fareSearchRequestData);

	/**
	 * Returns a map representing the remaining stockLevel of the fareProduct for each bundleType and each
	 * pricedItineraries.
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 * @return a Map<Integer, Map<String, Long>> where the key of the first map is the priceItineraryData.id, the key of
	 * the second map is the bundleType and the value of the second map is the remaining stockLevel.
	 */
	Map<Integer, Map<String, Long>> getRemainingSeats(FareSelectionData fareSelectionData);

}
