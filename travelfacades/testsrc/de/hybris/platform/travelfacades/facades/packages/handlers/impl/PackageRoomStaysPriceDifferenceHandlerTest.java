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
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageRoomStaysPriceDifferenceHandlerTest
{
	@InjectMocks
	PackageRoomStaysPriceDifferenceHandler packageRoomStaysPriceDifferenceHandler;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testHandle()
	{

		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = Mockito.mock(PackageResponseData.class, Mockito.RETURNS_DEEP_STUBS);

		packageRoomStaysPriceDifferenceHandler.handle(packageRequestData, packageResponseData);

		Mockito.when(packageResponseData.isAvailable()).thenReturn(true);
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setRoomStayRefNumber(1);
		reservedRoomStayData.setNonModifiable(true);
		final RatePlanData ratePlan = new RatePlanData();
		final PriceData actualRate = new PriceData();
		actualRate.setValue(BigDecimal.valueOf(100));
		actualRate.setCurrencyIso("GBP");
		ratePlan.setActualRate(actualRate);
		ratePlan.setAvailableQuantity(2);
		final List<RatePlanData> ratePlans = Collections.singletonList(ratePlan);
		reservedRoomStayData.setRatePlans(ratePlans);
		final List<ReservedRoomStayData> reservedRoomStays = Collections.singletonList(reservedRoomStayData);
		Mockito.when(
				packageResponseData.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getReservedRoomStays())
				.thenReturn(reservedRoomStays);

		packageRoomStaysPriceDifferenceHandler.handle(packageRequestData, packageResponseData);

		reservedRoomStayData.setNonModifiable(false);
		final RoomStayData roomStayData = new RoomStayData();
		roomStayData.setRoomStayRefNumber(1);
		roomStayData.setRatePlans(ratePlans);
		final List<RoomStayData> roomStays = Collections.singletonList(roomStayData);
		Mockito.when(packageResponseData.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getRoomStays())
				.thenReturn(roomStays);
		final PriceData value = new PriceData();
		value.setValue(BigDecimal.valueOf(10));
		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString())).thenReturn(value);

		packageRoomStaysPriceDifferenceHandler.handle(packageRequestData, packageResponseData);
		Assert.assertEquals(value, ratePlan.getPriceDifference());
	}
}
