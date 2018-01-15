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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link RemoveTravellerUserActionTypeRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RemoveTravellerUserActionTypeRestrictionStrategyTest
{

	@InjectMocks
	RemoveTravellerUserActionTypeRestrictionStrategy strategy = new RemoveTravellerUserActionTypeRestrictionStrategy();

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private UserService userService;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private CustomerModel bookerCustomerModel;

	@Mock
	private OrderModel orderModel;


	@Test
	public void testEmptyBookingAction()
	{
		final List<BookingActionData> enabledBookingActions = new ArrayList<>();
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(false);
		bookingActionData.setAlternativeMessages(new ArrayList<>());
		enabledBookingActions.add(bookingActionData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setFilteredTravellers(false);
		strategy.applyStrategy(enabledBookingActions, reservationData);
	}

	@Test
	public void testBookerModifyBookingMadeByRegisteredUser()
	{
		final ReservationData reservationData = new ReservationData();

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerModel.getType()).willReturn(CustomerType.REGISTERED);
		given(orderModel.getUser()).willReturn(customerModel);

		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);
		given(userService.getCurrentUser()).willReturn(customerModel);
		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(bookingActionData);

		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingMadeByGuestUser()
	{
		final ReservationData reservationData = new ReservationData();

		given(bookerCustomerModel.getType()).willReturn(CustomerType.REGISTERED);
		given(orderModel.getUser()).willReturn(bookerCustomerModel);
		given(orderModel.getAdditionalSecurity()).willReturn(false);
		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);
		given(userService.getCurrentUser()).willReturn(customerModel);

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		bookingActionDataList.add(bookingActionData);

		strategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}
}
