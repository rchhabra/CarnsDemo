/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelfacades.facades.accommodation.search;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;


/**
 * Interface for the Accommodation Search Facade
 */
public interface AccommodationSearchFacade
{
	/**
	 * Performs a search for accommodation offerings based on accommodation offering request
	 *
	 * @param accommodationRequest
	 * 		the accommodation request
	 * @return AccommodationSearchRequestData object with accommodation offering matching request parameters
	 */
	AccommodationSearchResponseData doSearch(AccommodationSearchRequestData accommodationRequest);
}
