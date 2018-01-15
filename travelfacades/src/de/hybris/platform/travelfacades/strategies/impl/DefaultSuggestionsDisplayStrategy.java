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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to create the display view for suggestion results.
 */
public class DefaultSuggestionsDisplayStrategy implements SuggestionsDisplayStrategy
{

	private static String FORMAT_CITY = "{city}";
	private static String FORMAT_COUNTRY = "{country}";
	private static String FORMAT_CODE = "{code}";
	private static String FORMAT_NAME = "{name}";

	private static String DEFAULT_HEADER_FORMAT = FORMAT_CITY + " - " + FORMAT_COUNTRY;
	private static String DEFAULT_OPTION_FORMAT = FORMAT_CITY + " - " + FORMAT_NAME + "(" + FORMAT_CODE + ")";

	private static String PROPERTY_FORMAT_HEADER = "suggestions.display.format.header";
	private static String PROPERTY_FORMAT_OPTION = "suggestions.display.format.option";

	private ConfigurationService configurationService;

	@Override
	public Map<String, Map<String, String>> createStructuredView(final SuggestionType suggestionType, final String searchText,
			final List<TransportOfferingData> transportOfferingDataList)
	{
		if (CollectionUtils.isEmpty(transportOfferingDataList))
		{
			return Collections.emptyMap();
		}

		final Map<String, Map<String, String>> resultMap = new TreeMap<String, Map<String, String>>();
		for (final TransportOfferingData data : transportOfferingDataList)
		{
			String locationCountry = null;
			String locationCity = null;
			TransportFacilityData transportFacilityData = null;
			if (SuggestionType.ORIGIN.equals(suggestionType))
			{
				locationCountry = data.getOriginLocationCountry();
				locationCity = data.getOriginLocationCity();
				transportFacilityData = data.getSector().getOrigin();
			}
			else if (SuggestionType.DESTINATION.equals(suggestionType))
			{
				locationCountry = data.getDestinationLocationCountry();
				locationCity = data.getDestinationLocationCity();
				transportFacilityData = data.getSector().getDestination();
			}

			if (transportFacilityData == null)
			{
				continue;
			}

			final String header = getHeader(locationCountry, locationCity, transportFacilityData.getCode(),
					transportFacilityData.getName(), searchText);
			final String option = getOption(locationCountry, locationCity, transportFacilityData.getCode(),
					transportFacilityData.getName(), searchText);

			if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(option))
			{
				if (resultMap.containsKey(header))
				{
					resultMap.get(header).put(option, transportFacilityData.getCode());
				}
				else
				{
					final Map<String, String> values = new TreeMap<>();
					values.put(option, transportFacilityData.getCode());
					resultMap.put(header, values);
				}
			}
		}
		return resultMap;
	}

	/**
	 * Gets header.
	 *
	 * @param country
	 * 		the country
	 * @param city
	 * 		the city
	 * @param code
	 * 		the code
	 * @param name
	 * 		the name
	 * @param searchText
	 * 		the search text
	 * @return formatted header value
	 */
	protected String getHeader(final String country, final String city, final String code, final String name,
			final String searchText)
	{
		final String headerFormat = getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_HEADER,
				DEFAULT_HEADER_FORMAT);
		return getFormattedValue(headerFormat, country, city, code, name);
	}

	/**
	 * Gets option.
	 *
	 * @param country
	 * 		the country
	 * @param city
	 * 		the city
	 * @param code
	 * 		the code
	 * @param name
	 * 		the name
	 * @param searchText
	 * 		the search text
	 * @return formatted option value
	 */
	protected String getOption(final String country, final String city, final String code, final String name,
			final String searchText)
	{
		final String optionFormat = getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_OPTION,
				DEFAULT_OPTION_FORMAT);

		return getFormattedValue(optionFormat, country, city, code, name);
	}

	/**
	 * Method to create the formatted string
	 *
	 * @param format
	 * 		the format
	 * @param country
	 * 		the country
	 * @param city
	 * 		the city
	 * @param code
	 * 		the code
	 * @param name
	 * 		the name
	 * @return formatted string
	 */
	protected String getFormattedValue(final String format, final String country, final String city, final String code,
			final String name)
	{
		String formattedValue = format;
		formattedValue = formattedValue.replace(FORMAT_CITY, StringUtils.defaultString(city));
		formattedValue = formattedValue.replace(FORMAT_COUNTRY, StringUtils.defaultString(country));
		formattedValue = formattedValue.replace(FORMAT_CODE, StringUtils.defaultString(code));
		formattedValue = formattedValue.replace(FORMAT_NAME, StringUtils.defaultString(name));
		return formattedValue;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
