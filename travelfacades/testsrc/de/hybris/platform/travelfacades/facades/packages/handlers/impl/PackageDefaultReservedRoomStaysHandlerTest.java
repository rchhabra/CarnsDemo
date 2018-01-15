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
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageDefaultReservedRoomStaysHandlerTest
{
	@InjectMocks
	PackageDefaultReservedRoomStaysHandler packageDefaultReservedRoomStaysHandler;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		final AccommodationPackageRequestData accommodationPackageRequest = new AccommodationPackageRequestData();
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequest = new AccommodationAvailabilityRequestData();

		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setNonModifiable(false);
		reservedRoomStayData.setRoomStayRefNumber(1);
		reservedRoomStays.add(reservedRoomStayData);
		accommodationAvailabilityResponseData.setReservedRoomStays(reservedRoomStays);

		final CriterionData criterion = new CriterionData();
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final RoomStayCandidateData roomStayCandidateData1 = new RoomStayCandidateData();
		roomStayCandidates.add(roomStayCandidateData1);
		criterion.setRoomStayCandidates(roomStayCandidates);
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setRatePlanConfigs(Collections.singletonList("ratePlan|Config|1"));
		criterion.setAccommodationReference(accommodationReference);
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		criterion.setStayDateRange(stayDateRange);
		accommodationSearchRequest.setCriterion(criterion);
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequest);
		accommodationPackageRequest.setAccommodationAvailabilityRequest(accommodationAvailabilityRequest);
		accommodationAvailabilityRequest.setCriterion(criterion);
		accommodationPackageRequest.setAccommodationSearchRequest(accommodationSearchRequest);

		packageDefaultReservedRoomStaysHandler.handle(packageRequestData, accommodationAvailabilityResponseData);

		final RoomStayCandidateData roomStayCandidateData2 = new RoomStayCandidateData();
		roomStayCandidates.add(roomStayCandidateData2);
		final List<RoomTypeData> roomTypes = new ArrayList<>();
		final RoomTypeData roomTypeData = new RoomTypeData();
		roomTypeData.setCode("Room");
		roomTypes.add(roomTypeData);
		final List<RoomStayData> roomStays = new ArrayList<>();
		final RoomStayData roomStayData = new RoomStayData();
		roomStayData.setRoomStayRefNumber(2);
		roomStayData.setRoomTypes(roomTypes);
		reservedRoomStayData.setRoomTypes(roomTypes);
		roomStays.add(roomStayData);
		accommodationAvailabilityResponseData.setRoomStays(roomStays);
		accommodationAvailabilityResponseData.setReservedRoomStays(reservedRoomStays);

		final List<RatePlanData> ratePlans = new ArrayList<>();
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setAvailableQuantity(2);

		final PriceData actualRate = new PriceData();
		ratePlanData.setActualRate(actualRate);
		ratePlans.add(ratePlanData);
		roomStayData.setRatePlans(ratePlans);

		packageDefaultReservedRoomStaysHandler.handle(packageRequestData, accommodationAvailabilityResponseData);

		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays()));
	}
}
