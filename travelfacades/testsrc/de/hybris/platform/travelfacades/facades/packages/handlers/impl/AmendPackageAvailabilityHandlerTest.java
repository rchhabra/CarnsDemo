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
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AmendPackageAvailabilityHandlerTest
{
	@InjectMocks
	AmendPackageAvailabilityHandler amendPackageAvailabilityHandler;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();

		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStays.add(reservedRoomStayData);
		accommodationAvailabilityResponse.setReservedRoomStays(reservedRoomStays);

		final List<RoomStayData> roomStays = new ArrayList<>();
		final RoomStayData roomStay = new RoomStayData();

		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setAvailableQuantity(10);
		final List<RatePlanData> ratePlans = new ArrayList<>();
		ratePlans.add(ratePlanData);
		roomStay.setRatePlans(ratePlans);
		roomStays.add(roomStay);
		accommodationAvailabilityResponse.setRoomStays(roomStays);

		accommodationPackageResponse.setAccommodationAvailabilityResponse(accommodationAvailabilityResponse);
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponse);


		amendPackageAvailabilityHandler.handle(packageRequestData, packageResponseData);
		Assert.assertTrue(packageResponseData.isAvailable());
	}

}
