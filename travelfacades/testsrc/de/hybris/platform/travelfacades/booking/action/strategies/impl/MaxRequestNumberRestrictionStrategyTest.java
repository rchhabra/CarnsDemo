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
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link MaxRequestNumberRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MaxRequestNumberRestrictionStrategyTest
{
	@InjectMocks
	MaxRequestNumberRestrictionStrategy strategy;
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration config;

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		given(configurationService.getConfiguration()).willReturn(config);
		given(config.getInt(Matchers.anyString())).willReturn(1);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		final SpecialRequestDetailData specialRequest = new SpecialRequestDetailData();
		final RemarkData remarks1 = new RemarkData();
		final RemarkData remarks2 = new RemarkData();
		specialRequest.setRemarks(Arrays.asList(remarks1, remarks2));
		roomStay.setSpecialRequestDetail(specialRequest);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledActions()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

}
