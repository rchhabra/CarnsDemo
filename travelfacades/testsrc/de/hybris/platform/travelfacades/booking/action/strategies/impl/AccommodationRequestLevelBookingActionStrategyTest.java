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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.RemarkData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link AccommodationRequestLevelBookingActionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationRequestLevelBookingActionStrategyTest
{
	@InjectMocks
	AccommodationRequestLevelBookingActionStrategy strategy;
	@Mock
	private Map<String, String> accommodationBookingActionTypeUrlMap;

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		final SpecialRequestDetailData splRequestDetailData = new SpecialRequestDetailData();
		final RemarkData remark = new RemarkData();
		remark.setCode("remark1");
		splRequestDetailData.setRemarks(Stream.of(remark).collect(Collectors.toList()));
		roomStay.setSpecialRequestDetail(splRequestDetailData);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		given(accommodationBookingActionTypeUrlMap.get(ActionTypeOption.CANCEL_REQUEST.toString()))
				.willReturn("/manage-booking/cancel-request/{bookingReference}/{roomStayRefNumber}/{requestCode}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.CANCEL_REQUEST, accommodationReservationData);
		Assert.assertEquals("/manage-booking/cancel-request/acc1/0/remark1", bookingActionDataList.get(0).getActionUrl());
	}

	@Test
	public void testBookingActionWithEmptyRoomStays()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setRoomStays(Collections.EMPTY_LIST);
		given(accommodationBookingActionTypeUrlMap.get(ActionTypeOption.CANCEL_REQUEST.toString()))
				.willReturn("/manage-booking/cancel-request/{bookingReference}/{roomStayRefNumber}/{requestCode}");
		strategy.applyStrategy(bookingActionDataList, ActionTypeOption.CANCEL_REQUEST, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.isEmpty());
	}

}
