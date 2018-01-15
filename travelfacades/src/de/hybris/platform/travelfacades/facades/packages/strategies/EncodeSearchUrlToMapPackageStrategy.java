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
package de.hybris.platform.travelfacades.facades.packages.strategies;

import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;

import java.util.Map;


/**
 * Interface exposing APIs to encode {@link PackageSearchRequestData} into a String url
 */
public interface EncodeSearchUrlToMapPackageStrategy
{
	/**
	 * Encode the Package Search Request Data to a map of Strings where the key is the search param and the value is the
	 * corresponding value.
	 *
	 * @param request
	 *           the request
	 * @return the map
	 */
	Map<String, String> encode(PackageSearchRequestData request);
}
