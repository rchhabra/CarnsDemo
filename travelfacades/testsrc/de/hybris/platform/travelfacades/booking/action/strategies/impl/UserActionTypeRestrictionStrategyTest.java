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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

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
 * Unit Test for the implementation of {@link UserActionTypeRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UserActionTypeRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.user.type.alternative.message";

	@InjectMocks
	UserActionTypeRestrictionStrategy userActionTypeRestrictionStrategy;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private UserService userService;

	@Test
	public void testNonBookerModifyBookingMadeByRegisteredUser()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);

		final CustomerModel customerModel = new CustomerModel();

		final OrderModel orderModel = new OrderModel();
		orderModel.setUser(customerModel);

		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		userActionTypeRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

}
