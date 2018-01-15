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

package de.hybris.platform.travelfacades.facades.accommodation.search.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataBasicHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test cases for implementation of {@link DefaultPropertyPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPropertyPipelineManagerTest
{

	@InjectMocks
	DefaultPropertyPipelineManager defaultPropertyPipelineManager;

	private List<PropertyHandler> handlers;

	@Before
	public void setUp()
	{
		handlers = new ArrayList<>();
		defaultPropertyPipelineManager.setHandlers(Collections.emptyList());
	}

	@Test
	public void test()
	{
		final List<AccommodationOfferingDayRateData> aodRateDatas = new ArrayList<>();
		final AccommodationOfferingDayRateData aodRateData1 = new AccommodationOfferingDayRateData();
		aodRateData1.setRoomStayCandidateRefNumber(1);
		final AccommodationOfferingDayRateData aodRateData2 = new AccommodationOfferingDayRateData();
		aodRateData2.setRoomStayCandidateRefNumber(2);
		aodRateDatas.add(aodRateData1);
		aodRateDatas.add(aodRateData2);
		final Map.Entry<String, List<AccommodationOfferingDayRateData>> propertyEntry = new Map.Entry<String, List<AccommodationOfferingDayRateData>>()
		{

			@Override
			public List<AccommodationOfferingDayRateData> setValue(final List<AccommodationOfferingDayRateData> value)
			{
				return aodRateDatas;
			}

			@Override
			public List<AccommodationOfferingDayRateData> getValue()
			{
				return aodRateDatas;
			}

			@Override
			public String getKey()
			{
				return "TEST_DEMO";
			}
		};

		final PropertyData response = defaultPropertyPipelineManager.executePipeline(propertyEntry, null);

		Assert.assertNotNull(response);
	}

	@Test
	public void testWithHandlers()
	{
		final List<AccommodationOfferingDayRateData> aodRateDatas = new ArrayList<>();
		final AccommodationOfferingDayRateData aodRateData1 = new AccommodationOfferingDayRateData();
		aodRateData1.setRoomStayCandidateRefNumber(1);
		final AccommodationOfferingDayRateData aodRateData2 = new AccommodationOfferingDayRateData();
		aodRateData2.setRoomStayCandidateRefNumber(2);
		aodRateDatas.add(aodRateData1);
		aodRateDatas.add(aodRateData2);
		final Map.Entry<String, List<AccommodationOfferingDayRateData>> propertyEntry = new Map.Entry<String, List<AccommodationOfferingDayRateData>>()
		{

			@Override
			public List<AccommodationOfferingDayRateData> setValue(final List<AccommodationOfferingDayRateData> value)
			{
				return aodRateDatas;
			}

			@Override
			public List<AccommodationOfferingDayRateData> getValue()
			{
				return aodRateDatas;
			}

			@Override
			public String getKey()
			{
				return "TEST_DEMO";
			}
		};
		handlers.add(new PropertyDataBasicHandler());
		defaultPropertyPipelineManager.setHandlers(handlers);
		final PropertyData response = defaultPropertyPipelineManager.executePipeline(propertyEntry, null);

		Assert.assertNotNull(response);
	}
}
