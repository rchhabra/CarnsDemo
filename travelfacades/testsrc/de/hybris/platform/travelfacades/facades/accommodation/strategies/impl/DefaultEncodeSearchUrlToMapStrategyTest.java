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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultEncodeSearchUrlToMapStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEncodeSearchUrlToMapStrategyTest
{
	@InjectMocks
	DefaultEncodeSearchUrlToMapStrategy defaultEncodeSearchUrlToMapStrategy;

	@Test
	public void testEncodeForNullScenarios()
	{
		final AccommodationSearchRequestData request = new AccommodationSearchRequestData();
		request.setCriterion(new CriterionData());
		Assert.assertTrue(MapUtils.isEmpty(defaultEncodeSearchUrlToMapStrategy.encode(request)));
	}

	@Test
	public void testEncode()
	{
		final PositionData positionData = new PositionData();
		positionData.setLatitude(new Double(180d));
		positionData.setLongitude(new Double(80d));

		final RadiusData radius = new RadiusData();
		radius.setValue(new Double(10d));

		final SearchAddressData address = new SearchAddressData();
		address.setLine1("TEST_LINE_1");
		address.setLine2("TEST_LINE_2");

		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(new Date());
		stayDateRange.setEndTime(new Date());

		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("adult");
		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setQuantity(0);
		passengerTypeQuantityData.setPassengerType(passengerTypeData);
		final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
		roomStayCandidate.setPassengerTypeQuantityList(Arrays.asList(passengerTypeQuantityData));
		final CriterionData criteria = new CriterionData();
		criteria.setPosition(positionData);
		criteria.setRadius(radius);
		criteria.setAddress(address);
		criteria.setSuggestionType(SuggestionType.PROPERTY.toString());
		criteria.setStayDateRange(stayDateRange);
		criteria.setRoomStayCandidates(Arrays.asList(roomStayCandidate));
		criteria.setPropertyFilterText("TEST_PROPERTY_FILTER");
		final AccommodationSearchRequestData request = new AccommodationSearchRequestData();
		request.setCriterion(criteria);

		Assert.assertEquals("TEST_PROPERTY_FILTER", defaultEncodeSearchUrlToMapStrategy.encode(request).get("propertyName"));
	}

	@Test
	public void testEncodeForNullCriteria()
	{
		final AccommodationSearchRequestData request = new AccommodationSearchRequestData();
		Assert.assertTrue(MapUtils.isEmpty(defaultEncodeSearchUrlToMapStrategy.encode(request)));
	}

}
