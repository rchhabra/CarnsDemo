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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageAccommodationInfoHandlerTest
{
	@InjectMocks
	PackageAccommodationInfoHandler packageAccommodationInfoHandler;

	@Test
	public void testHandlingAttributes()
	{
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = new HashMap<Integer, List<AccommodationOfferingDayRateData>>();
		final PropertyData propertyData = new PropertyData();

		final RateRangeData rateRange = new RateRangeData();
		propertyData.setRateRange(rateRange);

		final AccommodationOfferingDayRateData accommodationOfferingDayRateData = new AccommodationOfferingDayRateData();

		final List<String> ratePlanConfigs = new ArrayList<>();
		ratePlanConfigs.add("rate|plan|1");
		accommodationOfferingDayRateData.setRatePlanConfigs(ratePlanConfigs);
		dayRatesForRoomStayCandidate.put(1, Collections.singletonList(accommodationOfferingDayRateData));

		final List<String> accommodationInfos = new ArrayList<>();
		accommodationInfos.add("hotel");
		accommodationOfferingDayRateData.setAccommodationInfos(accommodationInfos);

		packageAccommodationInfoHandler.handlingAttributes(dayRatesForRoomStayCandidate, propertyData);
		Assert.assertTrue(CollectionUtils.isNotEmpty(propertyData.getRateRange().getAccommodationInfos()));
	}
}
