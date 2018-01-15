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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.travelservices.enums.TravellerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravellerSortStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravellerSortStrategyTest
{
	@InjectMocks
	DefaultTravellerSortStrategy defaultTravellerSortStrategy;

	private final String TEST_PASSENGER_TYPE_ADULT = "adult";
	private final String TEST_PASSENGER_TYPE_CHILD = "child";
	private final String TEST_PASSENGER_TYPE_INFANT = "infant";
	private final String TEST_PASSENGER_TYPE_UNKNOWN = "unknown";
	private PassengerTypeData ptdAdult;
	private PassengerTypeData ptdChild;
	private PassengerTypeData ptdInfant;
	private PassengerTypeData ptdUnknown;
	private final List<String> sortedPassengerTypes = Arrays.asList(TEST_PASSENGER_TYPE_ADULT, TEST_PASSENGER_TYPE_CHILD,
			TEST_PASSENGER_TYPE_INFANT);


	@Before
	public void setUp()
	{
		defaultTravellerSortStrategy.setSortedPassengerTypes(sortedPassengerTypes);

		ptdAdult = new PassengerTypeData();
		ptdAdult.setCode(TEST_PASSENGER_TYPE_ADULT);

		ptdChild = new PassengerTypeData();
		ptdChild.setCode(TEST_PASSENGER_TYPE_CHILD);

		ptdInfant = new PassengerTypeData();
		ptdInfant.setCode(TEST_PASSENGER_TYPE_INFANT);

		ptdUnknown = new PassengerTypeData();
		ptdUnknown.setCode(TEST_PASSENGER_TYPE_UNKNOWN);
	}

	@Test
	public void testApplyStrategy()
	{
		final List<TravellerData> actualResult = defaultTravellerSortStrategy.applyStrategy(createTravellerData());
		Assert.assertEquals(TravellerType.PASSENGER.getCode(), actualResult.get(0).getTravellerType());
		Assert.assertEquals(TEST_PASSENGER_TYPE_ADULT,
				((PassengerInformationData) actualResult.get(0).getTravellerInfo()).getPassengerType().getCode());
		Assert.assertEquals(TravellerType.PET.getCode(), actualResult.get(actualResult.size() - 1).getTravellerType());
		Assert.assertEquals(TEST_PASSENGER_TYPE_UNKNOWN,
				((PassengerInformationData) actualResult.get(actualResult.size() - 1).getTravellerInfo()).getPassengerType()
						.getCode());
	}

	@Test
	public void testSortPassengerTypes()
	{
		final List<PassengerTypeData> actualResult = defaultTravellerSortStrategy.sortPassengerTypes(createPassengerTypeData());
		Assert.assertEquals(TEST_PASSENGER_TYPE_ADULT, actualResult.get(0).getCode());
		Assert.assertEquals(TEST_PASSENGER_TYPE_UNKNOWN, actualResult.get(actualResult.size() - 1).getCode());
	}

	protected List<TravellerData> createTravellerData()
	{
		final PassengerInformationData pid1 = new PassengerInformationData();
		pid1.setPassengerType(ptdAdult);

		final PassengerInformationData pid2 = new PassengerInformationData();
		pid2.setPassengerType(ptdChild);

		final PassengerInformationData pid3 = new PassengerInformationData();
		pid3.setPassengerType(ptdInfant);

		final PassengerInformationData pid4 = new PassengerInformationData();
		pid4.setPassengerType(ptdUnknown);


		final TravellerData td1 = new TravellerData();
		td1.setTravellerType(TravellerType.PASSENGER.getCode());
		td1.setTravellerInfo(pid1);

		final TravellerData td2 = new TravellerData();
		td2.setTravellerType(TravellerType.PASSENGER.getCode());
		td2.setTravellerInfo(pid2);

		final TravellerData td3 = new TravellerData();
		td3.setTravellerType(TravellerType.PASSENGER.getCode());
		td3.setTravellerInfo(pid3);

		final TravellerData td4 = new TravellerData();
		td4.setTravellerType(TravellerType.PET.getCode());
		td4.setTravellerInfo(pid4);

		final List<TravellerData> travellers = new ArrayList<>(4);
		travellers.add(td3);
		travellers.add(td4);
		travellers.add(td1);
		travellers.add(td2);

		return travellers;
	}

	protected List<PassengerTypeData> createPassengerTypeData()
	{
		final List<PassengerTypeData> passengers = new ArrayList<>(4);
		passengers.add(ptdInfant);
		passengers.add(ptdUnknown);
		passengers.add(ptdChild);
		passengers.add(ptdAdult);

		return passengers;
	}
}
