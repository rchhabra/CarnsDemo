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

package de.hybris.platform.travelfacades.accommodation.search.mock.request;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Class to create mock accommodation search request data which will be later replaced by actual request data object
 */
public class MockAccommodationSearchRequestData
{
	public AccommodationSearchRequestData buildRequestData()
	{
		final AccommodationSearchRequestData accommodationSearchRequestData = new AccommodationSearchRequestData();

		accommodationSearchRequestData.setCriterion(buildCriterionData());

		return accommodationSearchRequestData;
	}

	private CriterionData buildCriterionData()
	{
		final CriterionData criterionData = new CriterionData();
		criterionData.setAddress(buildAddressData());
		criterionData.setPosition(buildPositionData());
		criterionData.setStayDateRange(buildStayDateRangeData());
		criterionData.setRoomStayCandidates(buildRoomStayCandidates());
		return criterionData;
	}


	private List<RoomStayCandidateData> buildRoomStayCandidates()
	{
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();

		final RoomStayCandidateData twoAdultsCandidate = new RoomStayCandidateData();
		twoAdultsCandidate.setPassengerTypeQuantityList(buildGuestCount(2, 1));
		twoAdultsCandidate.setRoomStayCandidateRefNumber(Integer.valueOf(0));

		final RoomStayCandidateData threeAdultsCandidate = new RoomStayCandidateData();
		threeAdultsCandidate.setPassengerTypeQuantityList(buildGuestCount(3, 0));
		threeAdultsCandidate.setRoomStayCandidateRefNumber(Integer.valueOf(0));

		roomStayCandidates.addAll(Arrays.asList(twoAdultsCandidate, threeAdultsCandidate));

		return roomStayCandidates;
	}

	private List<PassengerTypeQuantityData> buildGuestCount(final int adults, final int children)
	{
		final List<PassengerTypeQuantityData> guestCount = new ArrayList<>();

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		final PassengerTypeData adultTypeData = new PassengerTypeData();
		adultTypeData.setCode("adult");
		adultTypeData.setName("Adult");
		adultTypeData.setPassengerType("Adult");
		passengerTypeQuantityData.setPassengerType(adultTypeData);
		passengerTypeQuantityData.setQuantity(adults);
		guestCount.add(passengerTypeQuantityData);

		if (children > 0)
		{
			final PassengerTypeData childTypeData = new PassengerTypeData();
			childTypeData.setCode("child");
			childTypeData.setName("Child");
			childTypeData.setPassengerType("Child");
			passengerTypeQuantityData.setPassengerType(childTypeData);
			passengerTypeQuantityData.setQuantity(children);
			guestCount.add(passengerTypeQuantityData);
		}
		return guestCount;
	}

	private StayDateRangeData buildStayDateRangeData()
	{
		final StayDateRangeData stayDateRangeData = new StayDateRangeData();
		stayDateRangeData.setStartTime(TravelDateUtils.convertStringDateToDate("01/12/2016", TravelservicesConstants.DATE_PATTERN));
		stayDateRangeData.setEndTime(TravelDateUtils.convertStringDateToDate("04/12/2016", TravelservicesConstants.DATE_PATTERN));
		return stayDateRangeData;
	}

	private SearchAddressData buildAddressData()
	{
		final SearchAddressData addressData = new SearchAddressData();
		addressData.setTown("Paris");
		return addressData;
	}

	private PositionData buildPositionData()
	{
		final PositionData positionData = new PositionData();
		positionData.setLatitude(48.83836882);
		positionData.setLongitude(2.25017488);
		return null;
	}
}
