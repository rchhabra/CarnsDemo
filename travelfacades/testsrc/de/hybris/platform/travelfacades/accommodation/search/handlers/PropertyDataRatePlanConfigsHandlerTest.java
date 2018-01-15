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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataRatePlanConfigsHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyDataRatePlanConfigsHandlerTest
{
	@InjectMocks
	PropertyDataRatePlanConfigsHandler propertyDataRatePlanConfigsHandler;

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	private final String TEST_RATE_PLAN_COFIG_CODE = "RATE_PLAN|ACCOMMODATION_OFFERING_CODE|4";

	@Test
	public void testHandleForUnEqualSize()
	{
		final Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySet = new HashSet<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>>();
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = new HashMap<Integer, List<AccommodationOfferingDayRateData>>()
		{
			@Override
			public Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySet()
			{
				return entrySet;
			}
		};

		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationSearchRequest.setCriterion(createCriterionData());
		final PropertyData propertyData = new PropertyData();
		propertyDataRatePlanConfigsHandler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, propertyData);
		Assert.assertNull(propertyData.getRatePlanConfigs());
	}

	@Test
	public void testHandleForEmptyRatePlanConfig()
	{
		final List<AccommodationOfferingDayRateData> aodRateDatas = new ArrayList<>();
		final AccommodationOfferingDayRateData aodRateData1 = new AccommodationOfferingDayRateData();
		aodRateData1.setRatePlanConfigs(Collections.emptyList());
		aodRateDatas.add(aodRateData1);
		final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> propertyEntry = new Map.Entry<Integer, List<AccommodationOfferingDayRateData>>()
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
			public Integer getKey()
			{
				return 2;
			}
		};
		final Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySets = new HashSet<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>>();
		entrySets.add(propertyEntry);

		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = new HashMap<Integer, List<AccommodationOfferingDayRateData>>()
		{
			@Override
			public Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySet()
			{
				return entrySets;
			}
		};
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationSearchRequest.setCriterion(createCriterionData());
		final PropertyData propertyData = new PropertyData();
		propertyDataRatePlanConfigsHandler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, propertyData);
		Assert.assertNull(propertyData.getRatePlanConfigs());
	}

	@Test
	public void testHandle()
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString())).willReturn(5);
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = createDayRatesForRoomStayCandidate();

		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationSearchRequest.setCriterion(createCriterionData());
		final PropertyData propertyData = new PropertyData();
		propertyDataRatePlanConfigsHandler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, propertyData);
		Assert.assertEquals(TEST_RATE_PLAN_COFIG_CODE, propertyData.getRatePlanConfigs().get(0));
	}

	@Test
	public void testHandleForQuantityGreaterThanAllowed()
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString())).willReturn(2);
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = createDayRatesForRoomStayCandidate();
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationSearchRequest.setCriterion(createCriterionData());
		final PropertyData propertyData = new PropertyData();
		propertyDataRatePlanConfigsHandler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, propertyData);
		Assert.assertNull(propertyData.getRatePlanConfigs());
	}

	private CriterionData createCriterionData()
	{
		final CriterionData criterion = new CriterionData();
		criterion.setRoomStayCandidates(createRoomStayCandidateDatas());
		return criterion;

	}

	private List<RoomStayCandidateData> createRoomStayCandidateDatas()
	{
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		roomStayCandidates.add(createRoomStayCandidateData());
		return roomStayCandidates;
	}

	private RoomStayCandidateData createRoomStayCandidateData()
	{
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		return roomStayCandidateData;
	}

	private Map<Integer, List<AccommodationOfferingDayRateData>> createDayRatesForRoomStayCandidate()
	{
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = new HashMap<Integer, List<AccommodationOfferingDayRateData>>()
		{
			@Override
			public Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySet()
			{
				return createEntrySets();
			}
		};

		return dayRatesForRoomStayCandidate;
	}

	private Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> createEntrySets()
	{
		final Set<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> entrySets = new HashSet<java.util.Map.Entry<Integer, List<AccommodationOfferingDayRateData>>>();
		entrySets.add(createEntrySet());
		return entrySets;
	}

	private Map.Entry<Integer, List<AccommodationOfferingDayRateData>> createEntrySet()
	{
		final List<AccommodationOfferingDayRateData> aodRateDatas = new ArrayList<>();
		final AccommodationOfferingDayRateData aodRateData1 = new AccommodationOfferingDayRateData();
		aodRateData1.setRatePlanConfigs(Arrays.asList(TEST_RATE_PLAN_COFIG_CODE));
		aodRateDatas.add(aodRateData1);
		final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> propertyEntry = new Map.Entry<Integer, List<AccommodationOfferingDayRateData>>()
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
			public Integer getKey()
			{
				return 2;
			}
		};
		return propertyEntry;
	}
}
