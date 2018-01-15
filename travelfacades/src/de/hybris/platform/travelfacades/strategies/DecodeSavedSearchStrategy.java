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

package de.hybris.platform.travelfacades.strategies;

import java.util.Map;


/**
 * Strategy responsible for decoding a String representing a Saved Search into Map containing key value pairs of search
 * criteria.
 */
public interface DecodeSavedSearchStrategy
{
	/**
	 * Converts key value pairs from String to Map.
	 *
	 * @param encodedData
	 * 		It should be of following format: 1. The key-value pairs should be pipe("|") separated. 2. Key should be
	 * 		assigned value using assignment("=")operator. For example: key1=value1|key2=value2
	 * @return encodedDataMap encoded data map
	 */
	Map<String, String> getEncodedDataMap(String encodedData);

}
