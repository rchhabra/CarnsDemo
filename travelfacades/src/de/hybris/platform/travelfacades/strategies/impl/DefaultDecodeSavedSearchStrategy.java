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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.travelfacades.strategies.DecodeSavedSearchStrategy;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation class for DecodeSavedSearchStrategy interface.
 */
public class DefaultDecodeSavedSearchStrategy implements DecodeSavedSearchStrategy
{
	@Override
	public Map<String, String> getEncodedDataMap(final String encodedData)
	{
		final String[] encodedList = encodedData.split("\\|");
		final Map<String, String> encodedDataMap = new HashMap<String, String>(encodedList.length);
		for (final String element : encodedList)
		{
			final String[] keyValue = element.split("=");
			String value = "";
			if (keyValue.length > 1)
			{
				value = keyValue[1].trim();
			}
			encodedDataMap.put(keyValue[0].trim(), value);
		}
		return encodedDataMap;
	}

}
