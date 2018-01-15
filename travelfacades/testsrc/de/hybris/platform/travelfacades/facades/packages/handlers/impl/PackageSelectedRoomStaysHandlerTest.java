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
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageSelectedRoomStaysHandlerTest
{
	@InjectMocks
	PackageSelectedRoomStaysHandler packageSelectedRoomStaysHandler;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		packageSelectedRoomStaysHandler.handle(packageRequestData, packageResponseData);

		packageResponseData.setAvailable(true);
		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(1);
		final RoomTypeData roomType = new RoomTypeData();
		roomType.setCode("Room");
		final List<RoomTypeData> roomTypes = Collections.singletonList(roomType);
		roomStay.setRoomTypes(roomTypes);
		reservedRoomStays.add(roomStay);
		accommodationPackageResponse.setAccommodationAvailabilityResponse(accommodationAvailabilityResponse);
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponse);

		packageSelectedRoomStaysHandler.handle(packageRequestData, packageResponseData);


		accommodationAvailabilityResponse.setReservedRoomStays(reservedRoomStays);
		final List<RoomStayData> roomStays = new ArrayList<>();
		roomStays.add(roomStay);
		accommodationAvailabilityResponse.setRoomStays(roomStays);

		final List<RatePlanData> ratePlans = new ArrayList<>();
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setCode("ratePlanCode");
		ratePlans.add(ratePlanData);
		roomStay.setRatePlans(ratePlans);

		packageSelectedRoomStaysHandler.handle(packageRequestData, packageResponseData);
		Assert.assertTrue(ratePlanData.getSelected());
	}
}
