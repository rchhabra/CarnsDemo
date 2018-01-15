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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataRatePlanConfigsHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultPackagePipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackagePipelineManagerTest
{
	@InjectMocks
	DefaultPackagePipelineManager defaultPackagePipelineManager;

	@Mock
	PropertyDataRatePlanConfigsHandler propertyRatePlanConfigsHandler;

	@Test
	public void testExecutePipeline()
	{
		final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();
		accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(0);
		defaultPackagePipelineManager.setHandlers(Collections.singletonList(propertyRatePlanConfigsHandler));

		final AccommodationOfferingDayRateData accommodationOfferingDayRateData2 = new AccommodationOfferingDayRateData();
		accommodationOfferingDayRateData2.setRoomStayCandidateRefNumber(1);
		final Map.Entry<String, List<AccommodationOfferingDayRateData>> packageEntry = new Map.Entry<String, List<AccommodationOfferingDayRateData>>()
		{

			@Override
			public List<AccommodationOfferingDayRateData> getValue()
			{
				return Arrays.asList(accommodationOfferingDayRateData1, accommodationOfferingDayRateData2);
			}

			@Override
			public String getKey()
			{
				return "TEST_DEMO";
			}

			@Override
			public List<AccommodationOfferingDayRateData> setValue(final List<AccommodationOfferingDayRateData> value)
			{
				return null;
			}
		};
		Mockito.doNothing().when(propertyRatePlanConfigsHandler).handle(Matchers.anyMap(),
				Matchers.any(AccommodationSearchRequestData.class), Matchers.any(PackageData.class));
		Assert.assertNotNull(defaultPackagePipelineManager.executePipeline(packageEntry, new AccommodationSearchRequestData()));
	}
}
