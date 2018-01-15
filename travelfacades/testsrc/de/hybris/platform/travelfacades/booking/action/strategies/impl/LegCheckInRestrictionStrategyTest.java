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
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link LegCheckInRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LegCheckInRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.leg.check.in.alternative.message";

	@InjectMocks
	LegCheckInRestrictionStrategy legCheckInRestrictionStrategy;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CustomerAccountService customerAccountService;

	@Test
	public void testAllPassengersCheckedIn()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setOriginDestinationRefNumber(0);
		abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);
		abstractOrderEntryModel.setType(OrderEntryType.TRANSPORT);

		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		consignmentEntryModel.setOrderEntry(abstractOrderEntryModel);

		final ConsignmentModel consignmentModel = new ConsignmentModel();
		consignmentModel.setStatus(ConsignmentStatus.CHECKED_IN);
		consignmentModel.setConsignmentEntries(Stream.of(consignmentEntryModel).collect(Collectors.toSet()));

		final OrderModel orderModel = new OrderModel();
		orderModel.setConsignments(Stream.of(consignmentModel).collect(Collectors.toSet()));
		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		legCheckInRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

}
