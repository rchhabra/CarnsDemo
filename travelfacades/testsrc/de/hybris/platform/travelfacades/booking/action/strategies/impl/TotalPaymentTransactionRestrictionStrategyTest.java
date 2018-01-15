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
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.accommodation.strategies.OrderTotalByEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TotalPaymentTransactionRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TotalPaymentTransactionRestrictionStrategyTest
{
	@InjectMocks
	TotalPaymentTransactionRestrictionStrategy strategy;

	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private OrderTotalByEntryTypeCalculationStrategy orderTotalByEntryTypeCalculationStrategy;

	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final RateData rateData = new RateData();
		final PriceData actualPrice = new PriceData();
		actualPrice.setValue(BigDecimal.valueOf(30d));
		rateData.setActualRate(actualPrice);
		accommodationReservationData.setTotalRate(rateData);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerAccountService.getOrderForCode(accommodationReservationData.getCode(), baseStoreModel))
				.willReturn(orderModel);

		given(orderTotalByEntryTypeCalculationStrategy.calculate(orderModel, OrderEntryType.ACCOMMODATION))
		.willReturn(BigDecimal.valueOf(50d));

		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithTotalAmountLessThanReservationAmount()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final RateData rateData = new RateData();
		final PriceData actualPrice = new PriceData();
		actualPrice.setValue(BigDecimal.valueOf(30d));
		rateData.setActualRate(actualPrice);
		accommodationReservationData.setTotalRate(rateData);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerAccountService.getOrderForCode(accommodationReservationData.getCode(), baseStoreModel))
				.willReturn(orderModel);

		given(orderTotalByEntryTypeCalculationStrategy.calculate(orderModel, OrderEntryType.ACCOMMODATION))
				.willReturn(BigDecimal.valueOf(20d));

		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithDisabledBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}
}
