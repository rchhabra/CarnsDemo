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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AutocompletePredictionData;
import de.hybris.platform.commercefacades.accommodation.AutocompletionResponseData;
import de.hybris.platform.commercefacades.accommodation.PlaceDetailsResponseData;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.parser.AccommodationAutosuggestResponseParser;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.parser.AccommodationPlaceDetailResponseParser;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationRadiusCalculationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

/**
 * Unit Test for the implementation of {@link GoogleMapAutosuggestWrapper}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GoogleMapAutosuggestWrapperTest
{
	@InjectMocks
	GoogleMapAutosuggestWrapper googleMapAutosuggestWrapper = new GoogleMapAutosuggestWrapper()
	{
		@Override
		public RestTemplate createRestTemplate()
		{
			return restTemplate;
		}
	};

	@Mock
	RestTemplate restTemplate;

	private final String TEST_SEARCH_CODE = "TEST_SEARCH_CODE";
	private final String TEST_STATUS_CODE = "TEST_STATUS_CODE";
	private final String TEST_PLACE_ID1 = "TEST_PLACE_ID1";
	private final String TEST_PLACE_ID2 = "TEST_PLACE_ID2";
	private final String TEST_PLACE_ID3 = "TEST_PLACE_ID3";
	private final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
	private static final String STATUS_OK = "OK";
	private static final String GOOGLE_API_KEY = "googleAPIKey";
	@Mock
	private AccommodationRadiusCalculationStrategy accommodationRadiusCalculationStrategy;
	@Mock
	private AccommodationAutosuggestResponseParser accommodationAutosuggestResponseParser;
	@Mock
	private AccommodationPlaceDetailResponseParser accommodationPlaceDetailResponseParser;

	@Before
	public void setUp()
	{
		googleMapAutosuggestWrapper.setGoogleAPIKey(GOOGLE_API_KEY);
		googleMapAutosuggestWrapper.setSearchPlaceTypes(StringUtils.EMPTY);
	}
	@Test
	public void testGetAutoCompleteResultsForNullAutocompletionResponseData()
	{
		given(restTemplate.execute(Matchers.anyString(), Matchers.any(HttpMethod.class), Matchers.any(RequestCallback.class),
				Matchers.any(AccommodationAutosuggestResponseParser.class), Matchers.anyList())).willReturn(null);
		Assert.assertTrue(CollectionUtils.isEmpty(googleMapAutosuggestWrapper.getAutoCompleteResults(TEST_SEARCH_CODE)));
	}

	@Test
	public void testGetAutoCompleteResultsForFailStatus()
	{
		final AutocompletionResponseData autocompletionResponseData = new AutocompletionResponseData();
		autocompletionResponseData.setStatus(TEST_STATUS_CODE);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/autocomplete/xml?key=" + GOOGLE_API_KEY
						+ "&types=&input=TEST_SEARCH_CODE",
				HttpMethod.GET, null, accommodationAutosuggestResponseParser, Collections.emptyMap()))
						.willReturn(autocompletionResponseData);
		Assert.assertTrue(CollectionUtils.isEmpty(googleMapAutosuggestWrapper.getAutoCompleteResults(TEST_SEARCH_CODE)));
	}

	@Test
	public void testGetAutoCompleteResultsForNullPredictions()
	{
		final AutocompletionResponseData autocompletionResponseData = new AutocompletionResponseData();
		autocompletionResponseData.setStatus(STATUS_OK);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/autocomplete/xml?key=" + GOOGLE_API_KEY
						+ "&types=&input=TEST_SEARCH_CODE",
				HttpMethod.GET, null, accommodationAutosuggestResponseParser, Collections.emptyMap()))
						.willReturn(autocompletionResponseData);
		Assert.assertTrue(CollectionUtils.isEmpty(googleMapAutosuggestWrapper.getAutoCompleteResults(TEST_SEARCH_CODE)));
	}

	@Test
	public void testGetAutoCompleteResults()
	{
		final AutocompletionResponseData autocompletionResponseData = new AutocompletionResponseData();
		autocompletionResponseData.setStatus(STATUS_OK);
		autocompletionResponseData.setPredictions(createPredictions());

		final PlaceDetailsResponseData placeDetailData2 = new PlaceDetailsResponseData();
		placeDetailData2.setLatitude("80");
		placeDetailData2.setLongitude("90");
		placeDetailData2.setStatus(TEST_STATUS_CODE);
		final PlaceDetailsResponseData placeDetailData3 = new PlaceDetailsResponseData();
		placeDetailData3.setLatitude("80");
		placeDetailData3.setLongitude("90");
		placeDetailData3.setStatus(STATUS_OK);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/autocomplete/xml?key=" + GOOGLE_API_KEY
						+ "&types=&input=TEST_SEARCH_CODE",
				HttpMethod.GET, null, accommodationAutosuggestResponseParser, Collections.emptyMap()))
						.willReturn(autocompletionResponseData);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/details/xml?key=" + GOOGLE_API_KEY + "&placeid=TEST_PLACE_ID1",
				HttpMethod.GET, null,
				accommodationPlaceDetailResponseParser, Collections.emptyMap())).willReturn(null);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/details/xml?key=" + GOOGLE_API_KEY + "&placeid=TEST_PLACE_ID2",
				HttpMethod.GET, null, accommodationPlaceDetailResponseParser, Collections.emptyMap())).willReturn(placeDetailData2);
		given(restTemplate.execute(
				"https://maps.googleapis.com/maps/api/place/details/xml?key=" + GOOGLE_API_KEY + "&placeid=TEST_PLACE_ID3",
				HttpMethod.GET, null, accommodationPlaceDetailResponseParser, Collections.emptyMap()))
						.willReturn(placeDetailData3);
		given(accommodationRadiusCalculationStrategy.calculateRadius(Matchers.anyList())).willReturn(null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(googleMapAutosuggestWrapper.getAutoCompleteResults(TEST_SEARCH_CODE)));
	}

	protected List<AutocompletePredictionData> createPredictions()
	{
		final List<AutocompletePredictionData> predictions = new ArrayList<>();
		predictions.add(createPrediction(TEST_PLACE_ID1));
		predictions.add(createPrediction(TEST_PLACE_ID2));
		predictions.add(createPrediction(TEST_PLACE_ID3));
		return predictions;
	}

	protected AutocompletePredictionData createPrediction(final String placeId)
	{
		final AutocompletePredictionData prediction = new AutocompletePredictionData();
		prediction.setPlaceId(placeId);
		prediction.setDescription(TEST_DESCRIPTION);
		return prediction;
	}
}
