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

package de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper.impl;

import de.hybris.platform.commercefacades.accommodation.AutocompletionResponseData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.accommodation.PlaceDetailsResponseData;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.parser.AccommodationAutosuggestResponseParser;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.parser.AccommodationPlaceDetailResponseParser;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper.AccommodationAutoSuggestWrapper;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationRadiusCalculationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *	Implementation of AccommodationAutoSuggestWrapper.
 *
 * This Wrapper uses GoogleAPI, therefore any use of results in front end is subjected to use Google LOGO.
 */
public class GoogleMapAutosuggestWrapper implements AccommodationAutoSuggestWrapper
{
	private static final String ZERO_RESULTS = "ZERO_RESULTS";
	private static final Logger LOGGER = Logger.getLogger(GoogleMapAutosuggestWrapper.class);
	private static final String URL_GOOGLE_API_PLACE = "https://maps.googleapis.com/maps/api/place";
	private static final String URL_REQUEST_TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String URL_REQUEST_TYPE_DETAILS = "/details";
	private static final String URL_OUTPUT_TYPE_XML = "/xml";
	private static final String URL_PARAM_KEY = "?key=";
	private static final String URL_PARAM_PLACE_ID = "&placeid=";
	private static final String URL_PARAM_INPUT = "&input=";
	private static final String URL_PARAM_TYPES = "&types=";

	private static final String STATUS_OK = "OK";

	private String googleAPIKey;
	private String searchPlaceTypes;
	private AccommodationRadiusCalculationStrategy accommodationRadiusCalculationStrategy;
	private AccommodationAutosuggestResponseParser accommodationAutosuggestResponseParser;
	private AccommodationPlaceDetailResponseParser accommodationPlaceDetailResponseParser;

	@Override
	public List<GlobalSuggestionData> getAutoCompleteResults(final String searchText)
	{
		final List<GlobalSuggestionData> autoCompleteSuggestions = new ArrayList<>();

		if(StringUtils.isBlank(getGoogleAPIKey()))
		{
			return autoCompleteSuggestions;

		}

		final StringBuilder urlAddress = new StringBuilder(URL_GOOGLE_API_PLACE);
		urlAddress.append(URL_REQUEST_TYPE_AUTOCOMPLETE);
		urlAddress.append(URL_OUTPUT_TYPE_XML);
		urlAddress.append(URL_PARAM_KEY + getGoogleAPIKey());
		urlAddress.append(URL_PARAM_TYPES + getSearchPlaceTypes());
		urlAddress.append(URL_PARAM_INPUT + searchText);

		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		final RestTemplate restTemplate = createRestTemplate();
		final AutocompletionResponseData autocompletionResponseData = restTemplate
				.execute(urlAddress.toString(), HttpMethod.GET, null, getAccommodationAutosuggestResponseParser(),
						Collections.emptyMap());

		if (autocompletionResponseData != null && StringUtils.equalsIgnoreCase(autocompletionResponseData.getStatus(), STATUS_OK)
				&& autocompletionResponseData.getPredictions() != null)
		{
			autocompletionResponseData.getPredictions().forEach(prediction ->
			{
				final PlaceDetailsResponseData placeDetailsResponseData = getPlaceDetails(prediction.getPlaceId());

				if (placeDetailsResponseData != null && StringUtils.equalsIgnoreCase(placeDetailsResponseData.getStatus(),
						STATUS_OK))
				{
					final GlobalSuggestionData globalSuggestionData = new GlobalSuggestionData();
					globalSuggestionData.setName(prediction.getDescription());
					globalSuggestionData.setLatitude(Double.valueOf(placeDetailsResponseData.getLatitude()));
					globalSuggestionData.setLongitude(Double.valueOf(placeDetailsResponseData.getLongitude()));
					globalSuggestionData
							.setRadius(getAccommodationRadiusCalculationStrategy().calculateRadius(placeDetailsResponseData.getTypes
									()));
					autoCompleteSuggestions.add(globalSuggestionData);
				}
			});
		}
		else if (autocompletionResponseData != null && !StringUtils
				.equalsIgnoreCase(autocompletionResponseData.getStatus(), ZERO_RESULTS))
		{
			LOGGER.warn("Place autocomplete error. Status: " + autocompletionResponseData.getStatus() + " --- Error Message: "
					+ autocompletionResponseData.getMessage());
		}
		return autoCompleteSuggestions;
	}

	@Override
	public PlaceDetailsResponseData getPlaceDetails(final String placeID)
	{
		if(StringUtils.isBlank(getGoogleAPIKey()))
		{
			return null;
		}

		final StringBuilder urlAddress = new StringBuilder(URL_GOOGLE_API_PLACE);
		urlAddress.append(URL_REQUEST_TYPE_DETAILS);
		urlAddress.append(URL_OUTPUT_TYPE_XML);
		urlAddress.append(URL_PARAM_KEY + getGoogleAPIKey());
		urlAddress.append(URL_PARAM_PLACE_ID + placeID);

		final RestTemplate restTemplate = createRestTemplate();

		return restTemplate.execute(urlAddress.toString(), HttpMethod.GET, null, getAccommodationPlaceDetailResponseParser(),
				Collections.emptyMap());
	}

	protected RestTemplate createRestTemplate()
	{
		return new RestTemplate();
	}

	/**
	 * @return the googleAPIKey
	 */
	protected String getGoogleAPIKey()
	{
		return googleAPIKey;
	}

	/**
	 * @param googleAPIKey the googleAPIKey to set
	 */
	@Required
	public void setGoogleAPIKey(final String googleAPIKey)
	{
		this.googleAPIKey = googleAPIKey;
	}

	/**
	 * @return the searchPlaceTypes
	 */
	protected String getSearchPlaceTypes()
	{
		return searchPlaceTypes;
	}

	/**
	 * @param searchPlaceTypes the searchPlaceTypes to set
	 */
	@Required
	public void setSearchPlaceTypes(final String searchPlaceTypes)
	{
		this.searchPlaceTypes = searchPlaceTypes;
	}

	/**
	 * @return the accommodationAutosuggestResponseParser
	 */
	protected AccommodationAutosuggestResponseParser getAccommodationAutosuggestResponseParser()
	{
		return accommodationAutosuggestResponseParser;
	}

	/**
	 * @param accommodationAutosuggestResponseParser the accommodationAutosuggestResponseParser to set
	 */
	@Required
	public void setAccommodationAutosuggestResponseParser(
			final AccommodationAutosuggestResponseParser accommodationAutosuggestResponseParser)
	{
		this.accommodationAutosuggestResponseParser = accommodationAutosuggestResponseParser;
	}

	/**
	 * @return the accommodationPlaceDetailResponseParser
	 */
	protected AccommodationPlaceDetailResponseParser getAccommodationPlaceDetailResponseParser()
	{
		return accommodationPlaceDetailResponseParser;
	}

	/**
	 * @param accommodationPlaceDetailResponseParser the accommodationPlaceDetailResponseParser to set
	 */
	@Required
	public void setAccommodationPlaceDetailResponseParser(
			final AccommodationPlaceDetailResponseParser accommodationPlaceDetailResponseParser)
	{
		this.accommodationPlaceDetailResponseParser = accommodationPlaceDetailResponseParser;
	}

	/**
	 * @return the accommodationRadiusCalculationStrategy
	 */
	protected AccommodationRadiusCalculationStrategy getAccommodationRadiusCalculationStrategy()
	{
		return accommodationRadiusCalculationStrategy;
	}

	/**
	 * @param accommodationRadiusCalculationStrategy the accommodationRadiusCalculationStrategy to set
	 */
	@Required
	public void setAccommodationRadiusCalculationStrategy(
			final AccommodationRadiusCalculationStrategy accommodationRadiusCalculationStrategy)
	{
		this.accommodationRadiusCalculationStrategy = accommodationRadiusCalculationStrategy;
	}
}

