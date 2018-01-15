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
package de.hybris.platform.travelfacades.facades.accommodation.strategies;

import java.util.Map;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;


/**
 * Interface exposing APIs to encode {@link de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData}
 * into a String url
 */
public interface EncodeSearchUrlToMapStrategy
{
	/**
	 * Encode the Accommodation Search Request Data to a map of Strings where the key is the search param and the value is the
	 * corresponding value.
	 *
	 * @param request
	 * 		the request
	 * @return the map
	 */
	Map<String, String> encode(AccommodationSearchRequestData request);
}
