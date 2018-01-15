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

package de.hybris.platform.travelfacades.facades.packages.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.travelservices.enums.LocationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEncodeSearchUrlToMapPackageStrategyTest
{
	@InjectMocks
	DefaultEncodeSearchUrlToMapPackageStrategy defaultEncodeSearchUrlToMapPackageStrategy;

	@Test
	public void testEncode()
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		Assert.assertTrue(MapUtils.isEmpty(defaultEncodeSearchUrlToMapPackageStrategy.encode(packageSearchRequestData)));

		final CriterionData criterion = new CriterionData();
		final PositionData position = new PositionData();
		position.setLongitude(51.5147594);
		position.setLatitude(-0.14773);
		criterion.setPosition(position);
		final RadiusData radius = new RadiusData();
		radius.setValue(13.5);
		criterion.setRadius(radius);

		final SearchAddressData address = new SearchAddressData();
		address.setLine1("line1");
		address.setLine2("line2");
		criterion.setAddress(address);

		criterion.setSuggestionType(SuggestionType.CITY.name());
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(new GregorianCalendar(2017, 06, 10).getTime());
		stayDateRange.setEndTime(new GregorianCalendar(2017, 06, 12).getTime());
		criterion.setStayDateRange(stayDateRange);

		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode("adult");
		passengerTypeQuantityData.setPassengerType(passengerType);
		passengerTypeQuantityData.setQuantity(2);
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = Collections.singletonList(passengerTypeQuantityData);
		roomStayCandidate.setPassengerTypeQuantityList(passengerTypeQuantityList);
		roomStayCandidate.setRoomStayCandidateRefNumber(1);
		roomStayCandidates.add(roomStayCandidate);
		criterion.setRoomStayCandidates(roomStayCandidates);

		criterion.setPropertyFilterText("Hotel");

		packageSearchRequestData.setCriterion(criterion);
		Assert.assertTrue(MapUtils.isNotEmpty(defaultEncodeSearchUrlToMapPackageStrategy.encode(packageSearchRequestData)));

		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final TravelPreferencesData travelPreferences = new TravelPreferencesData();
		fareSearchRequestData.setTravelPreferences(travelPreferences);
		fareSearchRequestData.setTripType(TripType.RETURN);

		final List<OriginDestinationInfoData> originDestinationInfo = new ArrayList<>();
		final OriginDestinationInfoData originDestinationInfoData1=new OriginDestinationInfoData();
		originDestinationInfoData1.setReferenceNumber(0);
		originDestinationInfoData1.setDepartureLocation("London");
		originDestinationInfoData1.setDepartureTime(stayDateRange.getStartTime());
		originDestinationInfoData1.setDepartureLocationType(LocationType.CITY);
		originDestinationInfoData1.setArrivalLocation("Paris");
		originDestinationInfoData1.setArrivalTime(stayDateRange.getEndTime());
		originDestinationInfoData1.setArrivalLocationType(LocationType.CITY);


		final OriginDestinationInfoData originDestinationInfoData2 = new OriginDestinationInfoData();
		originDestinationInfoData2.setReferenceNumber(1);
		originDestinationInfoData2.setDepartureLocation("Paris");
		originDestinationInfoData2.setDepartureTime(stayDateRange.getStartTime());
		originDestinationInfoData2.setDepartureLocationType(LocationType.CITY);
		originDestinationInfoData2.setArrivalLocation("London");
		originDestinationInfoData2.setArrivalTime(stayDateRange.getEndTime());
		originDestinationInfoData2.setArrivalLocationType(LocationType.CITY);

		originDestinationInfo.add(originDestinationInfoData1);
		originDestinationInfo.add(originDestinationInfoData2);

		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfo);
		packageSearchRequestData.setFareSearchRequestData(fareSearchRequestData);
		Assert.assertTrue(MapUtils.isNotEmpty(defaultEncodeSearchUrlToMapPackageStrategy.encode(packageSearchRequestData)));
	}
}
