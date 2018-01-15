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

package de.hybris.platform.travelfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.core.PK;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.strategies.DecodeSavedSearchStrategy;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * unit test for {@link SavedSearchPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SavedSearchPopulatorTest
{
	@InjectMocks
	private SavedSearchPopulator savedSearchPopulator;

	@Mock
	private PassengerTypeFacade mockPassengerTypeFacade;

	@Mock
	private TravellerSortStrategy mockTravellerSortStrategy;

	@Mock
	private DecodeSavedSearchStrategy mockDecodeSavedSearchStrategy;

	@Mock
	private SavedSearchModel mockSource;

	@Before
	public void setUp()
	{
		final List<PassengerTypeData> passengerTypeDatas = new ArrayList<PassengerTypeData>();
		final Map<String, String> encodedSearchMap = new HashMap<String, String>();
		encodedSearchMap.put("departureLocationName", "departureLocationName");
		encodedSearchMap.put("returnDateTime", "15/04/2016");
		encodedSearchMap.put("cabinClass", "cabinClass");
		encodedSearchMap.put("tripType", "RETURN");
		encodedSearchMap.put("departureLocation", "departureLocation");
		encodedSearchMap.put("departingDateTime", "13/04/2016");
		encodedSearchMap.put("arrivalLocationName", "arrivalLocationName");
		encodedSearchMap.put("adult", "3");
		encodedSearchMap.put("infant", "0");
		encodedSearchMap.put("arrivalLocation", "arrivalLocation");
		encodedSearchMap.put("child", "1");

		final String passengerTypes[] =
		{ "adult", "infant", "child" };

		for (final String passengerType : passengerTypes)
		{
			final PassengerTypeData passengerTypeData = new PassengerTypeData();
			passengerTypeData.setCode(passengerType);
			passengerTypeDatas.add(passengerTypeData);
		}

		savedSearchPopulator.setPassengerTypeFacade(mockPassengerTypeFacade);
		savedSearchPopulator.setTravellerSortStrategy(mockTravellerSortStrategy);
		savedSearchPopulator.setDecodeSavedSearchStrategy(mockDecodeSavedSearchStrategy);

		//Mocking call to methods.
		given(mockTravellerSortStrategy.sortPassengerTypes(passengerTypeDatas)).willReturn(passengerTypeDatas);
		given(mockPassengerTypeFacade.getPassengerTypes()).willReturn(passengerTypeDatas);
		given(mockDecodeSavedSearchStrategy.getEncodedDataMap(Matchers.anyString())).willReturn(encodedSearchMap);

		given(mockSource.getPk()).willReturn(PK.fromLong(00001l));
	}

	@Test
	public void testPopulateSavedSearchData()
	{
		final SavedSearchData target = new SavedSearchData();
		given(mockSource.getEncodedSearch()).willReturn("testEncodedSearch");
		savedSearchPopulator.populate(mockSource, target);
		Assert.assertEquals(target.getTripType(), "RETURN");
	}

	@Test
	public void testPopulateSavedSearchDataForNull()
	{
		final SavedSearchData target = new SavedSearchData();
		given(mockSource.getEncodedSearch()).willReturn(null);
		savedSearchPopulator.populate(mockSource, target);
		Assert.assertNull(target.getTripType());
	}
}
