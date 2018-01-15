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

package de.hybris.platform.travelfacades.accommodation.search.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.travelfacades.accommodation.search.mock.request.MockAccommodationSearchRequestData;
import de.hybris.platform.travelfacades.accommodation.search.mock.response.MockAccommodationOfferingDayRateDataList;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataAccommodationInfoHandler;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyDataAccommodationInfoHandlerTest
{
	MockAccommodationSearchRequestData requestBuilder = new MockAccommodationSearchRequestData();
	MockAccommodationOfferingDayRateDataList dayRatesBuilder = new MockAccommodationOfferingDayRateDataList();

	PropertyData propertyData = new PropertyData();

	PropertyDataAccommodationInfoHandler handler = new PropertyDataAccommodationInfoHandler();

	@Before
	public void setUp()
	{
		final RateRangeData rateRange = new RateRangeData();
		propertyData.setRateRange(rateRange);
		propertyData.setRatePlanConfigs(Stream.of("").collect(Collectors.toList()));
	}

	@Test
	public void testHandler()
	{
		handler.handle(dayRatesBuilder.buildMapForSingleProperty(), requestBuilder.buildRequestData(), propertyData);
		Assert.assertTrue(Arrays.asList("1_Family Room", "1_Superior Double Room")
				.contains(propertyData.getRateRange().getAccommodationInfos().get(0).getAccommodationName()));

	}
}
