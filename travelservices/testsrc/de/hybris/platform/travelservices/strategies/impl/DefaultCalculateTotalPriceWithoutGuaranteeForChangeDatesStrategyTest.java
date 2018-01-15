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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategyTest
{
	@InjectMocks
	DefaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy;

	@Test
	public void testCalculateForEmptyArguments()
	{
		Assert.assertNull(defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(null, StringUtils.EMPTY));
		Assert.assertNull(defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy
				.calculate(new AccommodationAvailabilityResponseData(), StringUtils.EMPTY));
		Assert.assertNull(defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy
				.calculate(new AccommodationAvailabilityResponseData(), "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculateForRoomStayDataWithNullTotalPrice()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
		roomStayData.setRoomStayRefNumber(0);
		final RateData rateData = new RateData();
		final PriceData priceData = new PriceData();
		final Date date = new Date();
		priceData.setValue(BigDecimal.valueOf(200d));
		rateData.setActualRate(priceData);
		roomStayData.setCheckInDate(date);
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(roomStayData));
		Assert.assertNull(defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculateForEmptyReservedRoomStayData()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomStayData roomStayData = new RoomStayData();
		roomStayData.setRoomStayRefNumber(0);
		final RateData rateData = new RateData();
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(200d));
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(roomStayData));
		Assert.assertNull(defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculate()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
		roomStayData.setRoomStayRefNumber(0);
		final RateData rateData = new RateData();
		final PriceData priceData = new PriceData();
		final Date date = new Date();
		priceData.setValue(BigDecimal.valueOf(200d));
		rateData.setActualRate(priceData);
		roomStayData.setTotalRate(rateData);
		roomStayData.setCheckInDate(date);
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(roomStayData));

		Assert.assertEquals(200, defaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE").intValue());
	}
}
