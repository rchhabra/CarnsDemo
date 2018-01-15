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

package de.hybris.platform.travelfacades.accommodation.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for {@link DefaultAccommodationSuggestionsDisplayStrategy} implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationSuggestionsDisplayStrategyTest
{
	@InjectMocks
	private DefaultAccommodationSuggestionsDisplayStrategy defaultAccommodationSuggestionsDisplayStrategy;

	private final String TEST_LOCATION_CODE = "TEST_LOCATION_CODE";
	private final String TEST_ACCOMMODATION_OFFERING_CODE = "TEST_ACCOMMODATION_OFFERING_CODE";
	private final String TEST_LOCATION_NAME_A = "TEST_LOCATION_NAME_A";
	private final String TEST_LOCATION_NAME_B = "TEST_LOCATION_NAME_B";

	@Test
	public void testCreateGlobalSuggestionDataForEmptyResults()
	{
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationSuggestionsDisplayStrategy
				.createGlobalSuggestionData(StringUtils.EMPTY, Collections.emptyList(), StringUtils.EMPTY)));
	}

	@Test
	public void testCreateGlobalSuggestionDataForLocation()
	{
		final AccommodationOfferingDayRateData testAccommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
		testAccommodationOfferingDayRateData.setLocationCodes(TEST_LOCATION_CODE);
		testAccommodationOfferingDayRateData.setLocationNames(Arrays.asList(TEST_LOCATION_NAME_A, TEST_LOCATION_NAME_B));

		final GlobalSuggestionData actualResult = defaultAccommodationSuggestionsDisplayStrategy.createGlobalSuggestionData(
				StringUtils.EMPTY, Arrays.asList(testAccommodationOfferingDayRateData), "SUGGESTIONS_ACCOMMODATION_LOCATION").get(0);

		Assert.assertEquals(TEST_LOCATION_CODE, actualResult.getCode());
		Assert.assertEquals(SuggestionType.LOCATION, actualResult.getSuggestionType());
	}

	@Test
	public void testCreateGlobalSuggestionDataForProperty()
	{
		final AccommodationOfferingDayRateData testAccommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
		testAccommodationOfferingDayRateData.setAccommodationOfferingCode(TEST_ACCOMMODATION_OFFERING_CODE);
		final GlobalSuggestionData actualResult = defaultAccommodationSuggestionsDisplayStrategy.createGlobalSuggestionData(
				StringUtils.EMPTY, Arrays.asList(testAccommodationOfferingDayRateData), "SUGGESTIONS_ACCOMMODATION_PROPERTY").get(0);

		Assert.assertEquals(TEST_ACCOMMODATION_OFFERING_CODE, actualResult.getCode());
		Assert.assertEquals(SuggestionType.PROPERTY, actualResult.getSuggestionType());
	}

	@Test
	public void testCreateGlobalSuggestionDataForUnknown()
	{
		final AccommodationOfferingDayRateData testAccommodationOfferingDayRateData = new AccommodationOfferingDayRateData();

		final GlobalSuggestionData actualResult = defaultAccommodationSuggestionsDisplayStrategy.createGlobalSuggestionData(
				StringUtils.EMPTY, Arrays.asList(testAccommodationOfferingDayRateData), "TEST_SEARCH_TYPE").get(0);

		Assert.assertTrue(StringUtils.isEmpty(actualResult.getCode()));
		Assert.assertNull(actualResult.getSuggestionType());
	}
}
