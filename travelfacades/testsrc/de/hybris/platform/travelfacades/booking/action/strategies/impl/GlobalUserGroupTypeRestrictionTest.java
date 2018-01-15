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
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link GlobalUserGroupTypeRestriction}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GlobalUserGroupTypeRestrictionTest
{
	@InjectMocks
	GlobalUserGroupTypeRestriction strategy;

	@Mock
	private UserService userService;

	@Mock
	private UserModel user;

	@Mock
	private PrincipalGroupModel group;

	private List<String> restrictedUserGroupCodeList;

	@Before
	public void setUp()
	{
		restrictedUserGroupCodeList = new ArrayList<>();
		restrictedUserGroupCodeList.add("b2badmingroup");
		strategy.setRestrictedUserGroupCodeList(restrictedUserGroupCodeList);
		strategy.setUserService(userService);
	}

	@Test
	public void testBookingAction()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		given(userService.getCurrentUser()).willReturn(user);
		given(group.getUid()).willReturn("admingroup");
		given(user.getAllGroups()).willReturn(Stream.of(group).collect(Collectors.toSet()));
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithB2BAdmin()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		given(userService.getCurrentUser()).willReturn(user);
		given(group.getUid()).willReturn("b2badmingroup");
		given(user.getAllGroups()).willReturn(Stream.of(group).collect(Collectors.toSet()));
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledAction()
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionData actionData = new BookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		bookingActionDataList.add(actionData);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();
		strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

}
