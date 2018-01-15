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

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.strategies.TransportSuggestionsDisplayStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to create the display view for transport suggestion results.
 */
public class DefaultTransportSuggestionsDisplayStrategy implements TransportSuggestionsDisplayStrategy
{

	private ConfigurationService configurationService;

	private static String FORMAT_CITY = "{city}";
	private static String FORMAT_COUNTRY = "{country}";
	private static String FORMAT_CODE = "{code}";
	private static String FORMAT_NAME = "{name}";

	private static String DEFAULT_HEADER_FORMAT = FORMAT_CITY + " - " + FORMAT_COUNTRY;
	private static String DEFAULT_OPTION_FORMAT = FORMAT_CITY + " - " + FORMAT_NAME + "(" + FORMAT_CODE + ")";

	private static String PROPERTY_FORMAT_HEADER = "suggestions.display.format.header";
	private static String PROPERTY_FORMAT_OPTION = "suggestions.display.format.option";

	@SuppressWarnings("unchecked")
	@Override
	public Map<GlobalSuggestionData, List<GlobalSuggestionData>> createSuggestionsMap(final String searchText,
			final List<TransportOfferingData> transportOfferingDataList, final LegSuggestionType type)
	{

		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> mapToReturn = new HashMap<>();
		final Collection<Map<String, List<TransportOfferingData>>> mapsByCity = LegSuggestionType.ORIGIN.equals(type)
				? transportOfferingDataList.stream()
				.collect(Collectors.groupingBy(TransportOfferingData::getOriginLocationCountry,
						Collectors.groupingBy(TransportOfferingData::getOriginLocationCity)))
						.values()
				: transportOfferingDataList.stream()
						.collect(Collectors.groupingBy(TransportOfferingData::getDestinationLocationCountry,
								Collectors.groupingBy(TransportOfferingData::getDestinationLocationCity)))
						.values();
		mapsByCity.stream().forEach(map -> {
			map.entrySet().forEach(entry -> populateEntry(entry.getValue(), mapToReturn, type));
		});

		return mapToReturn;
	}

	protected void populateEntry(final List<TransportOfferingData> traOffList,
			final Map<GlobalSuggestionData, List<GlobalSuggestionData>> mapToReturn, final LegSuggestionType type)
	{

		final GlobalSuggestionData header = buildHeader(CollectionUtils.isEmpty(traOffList) ? null : traOffList.get(0), type);
		final List<GlobalSuggestionData> options = traOffList.stream()
				.map(transportOfferingData -> buildOption(transportOfferingData, type)).collect(Collectors.toList());
		mapToReturn.put(header, options);
	}

	private GlobalSuggestionData buildHeader(final TransportOfferingData transportOfferingData, final LegSuggestionType type)
	{
		final GlobalSuggestionData header = new GlobalSuggestionData();
		if (Objects.isNull(transportOfferingData) || Objects.isNull(type))
		{
			return header;
		}

		if (LegSuggestionType.ORIGIN.equals(type))
		{
			header.setName(getFormattedValue(
					getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_HEADER, DEFAULT_HEADER_FORMAT),
					transportOfferingData.getOriginLocationCountry(), transportOfferingData.getOriginLocationCity(), null, null));
			header.setCode(transportOfferingData.getSector().getOrigin().getLocation().getCode());
		}
		else
		{
			header.setName(getFormattedValue(
					getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_HEADER, DEFAULT_HEADER_FORMAT),
					transportOfferingData.getDestinationLocationCountry(), transportOfferingData.getDestinationLocationCity(), null,
					null));
			header.setCode(transportOfferingData.getSector().getDestination().getLocation().getCode());
		}
		header.setSuggestionType(SuggestionType.CITY);
		return header;
	}

	protected GlobalSuggestionData buildOption(final TransportOfferingData transportOfferingData, final LegSuggestionType type)
	{
		final GlobalSuggestionData option = new GlobalSuggestionData();
		if (LegSuggestionType.ORIGIN.equals(type))
		{
			final TransportFacilityData facilityData = transportOfferingData.getSector().getOrigin();
			option.setName(getFormattedValue(
					getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_OPTION, DEFAULT_OPTION_FORMAT),
					transportOfferingData.getOriginLocationCountry(), transportOfferingData.getOriginLocationCity(),
					facilityData.getCode(), facilityData.getName()));
			option.setCode(facilityData.getCode());
		}
		else
		{
			final TransportFacilityData facilityData = transportOfferingData.getSector().getDestination();
			option.setName(getFormattedValue(
					getConfigurationService().getConfiguration().getString(PROPERTY_FORMAT_OPTION, DEFAULT_OPTION_FORMAT),
					transportOfferingData.getDestinationLocationCountry(), transportOfferingData.getDestinationLocationCity(),
					facilityData.getCode(), facilityData.getName()));
			option.setCode(facilityData.getCode());
		}
		option.setSuggestionType(SuggestionType.AIRPORTGROUP);
		return option;
	}

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
	 *
	 * @return configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}




}
