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
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageAvailabilityHandlerTest
{
	@InjectMocks
	PackageAvailabilityHandler packageAvailabilityHandler;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		accommodationPackageResponse.setAccommodationAvailabilityResponse(accommodationAvailabilityResponse);
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponse);

		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		reservedRoomStays.add(roomStay);
		accommodationAvailabilityResponse.setReservedRoomStays(reservedRoomStays);
		final List<RoomStayData> roomStays = new ArrayList<>();
		roomStays.add(roomStay);
		final List<RatePlanData> ratePlans = new ArrayList<>();
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setAvailableQuantity(5);
		ratePlans.add(ratePlanData);
		roomStay.setRatePlans(ratePlans);
		accommodationAvailabilityResponse.setRoomStays(roomStays);

		final TransportPackageResponseData transportPackageResponse = new TransportPackageResponseData();
		packageResponseData.setTransportPackageResponse(transportPackageResponse);

		final FareSelectionData fareSearchResponse = new FareSelectionData();
		transportPackageResponse.setFareSearchResponse(fareSearchResponse);

		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraries.add(pricedItineraryData);
		fareSearchResponse.setPricedItineraries(pricedItineraries);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);

		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);

		packageAvailabilityHandler.handle(packageRequestData, packageResponseData);
		Assert.assertTrue(packageResponseData.isAvailable());
	}
}
