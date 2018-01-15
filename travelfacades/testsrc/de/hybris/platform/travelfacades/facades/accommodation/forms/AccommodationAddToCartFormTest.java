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

package de.hybris.platform.travelfacades.facades.accommodation.forms;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AccommodationAddToCartForm}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationAddToCartFormTest
{
	@InjectMocks
	AccommodationAddToCartForm accommodationAddToCartForm;

	@Test
	public void testGetNumberOfRooms()
	{
		accommodationAddToCartForm.setNumberOfRooms(0);
		Assert.assertEquals(0, accommodationAddToCartForm.getNumberOfRooms());
	}

	@Test
	public void testGetCheckInDateTime()
	{
		final String date = "19/12/2016";
		accommodationAddToCartForm.setCheckInDateTime(date);
		Assert.assertEquals(date, accommodationAddToCartForm.getCheckInDateTime());
	}

	@Test
	public void testGetCheckOutDateTime()
	{
		final String date = "19/12/2016";
		accommodationAddToCartForm.setCheckOutDateTime(date);
		Assert.assertEquals(date, accommodationAddToCartForm.getCheckOutDateTime());
	}

	@Test
	public void testGetAccommodationOfferingCode()
	{
		accommodationAddToCartForm.setAccommodationOfferingCode("TEST_ACCOMMODATION_OFFERING_CODE");
		Assert.assertEquals("TEST_ACCOMMODATION_OFFERING_CODE", accommodationAddToCartForm.getAccommodationOfferingCode());
	}

	@Test
	public void testGetAccommodationCode()
	{
		accommodationAddToCartForm.setAccommodationCode("TEST_ACCOMMODATION_CODE");
		Assert.assertEquals("TEST_ACCOMMODATION_CODE", accommodationAddToCartForm.getAccommodationCode());
	}

	@Test
	public void testGetRatePlanCode()
	{
		accommodationAddToCartForm.setRatePlanCode("TEST_RATE_PLAN_CODE");
		Assert.assertEquals("TEST_RATE_PLAN_CODE", accommodationAddToCartForm.getRatePlanCode());
	}

	@Test
	public void testGetRoomRateCodes()
	{
		accommodationAddToCartForm.setRoomRateCodes(Arrays.asList("TEST_ROOM_RATE_CODE"));
		Assert.assertEquals("TEST_ROOM_RATE_CODE", accommodationAddToCartForm.getRoomRateCodes().get(0));
	}

	@Test
	public void testGetRoomRateDates()
	{
		accommodationAddToCartForm.setRoomRateDates(Arrays.asList("TEST_ROOM_RATE_DATE"));
		Assert.assertEquals("TEST_ROOM_RATE_DATE", accommodationAddToCartForm.getRoomRateDates().get(0));

	}

	@Test
	public void testGetRoomStayRefNumber()
	{
		accommodationAddToCartForm.setRoomStayRefNumber(0);
		Assert.assertEquals(0, accommodationAddToCartForm.getRoomStayRefNumber().intValue());
	}
}
