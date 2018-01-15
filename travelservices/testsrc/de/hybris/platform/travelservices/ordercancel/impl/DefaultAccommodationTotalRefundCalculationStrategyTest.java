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

package de.hybris.platform.travelservices.ordercancel.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.CancelPenaltiesCalculationService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultAccommodationTotalRefundCalculationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationTotalRefundCalculationStrategyTest
{

	@InjectMocks
	DefaultAccommodationTotalRefundCalculationStrategy defaultAccommodationTotalRefundCalculationStrategy;

	@Mock
	private CancelPenaltiesCalculationService cancelPenaltiesCalculationService;

	@Mock
	private TimeService timeService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testCalculate()
	{
		when(cancelPenaltiesCalculationService.getActiveCancelPenalty(Matchers.anyCollection(), Matchers.any(Date.class),
				Matchers.any(BigDecimal.class))).thenReturn(null);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100));
		paymentTransactions
				.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.REFUND_STANDALONE, 50));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);

		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);

		Assert.assertEquals(50, defaultAccommodationTotalRefundCalculationStrategy.getTotalToRefund(orderModel).intValue());
	}

	@Test
	public void testCalculateWithCancelPenality()
	{
		final CancelPenaltyModel cancelPenalty = new CancelPenaltyModel();
		when(cancelPenaltiesCalculationService.getActiveCancelPenalty(Matchers.anyCollection(), Matchers.any(Date.class),
				Matchers.any(BigDecimal.class))).thenReturn(cancelPenalty);
		when(cancelPenaltiesCalculationService.getCancelPenaltyAmount(Matchers.any(CancelPenaltyModel.class),
				Matchers.any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(20));
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100));
		paymentTransactions
				.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.REFUND_STANDALONE, 50));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);

		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);

		Assert.assertEquals(30, defaultAccommodationTotalRefundCalculationStrategy.getTotalToRefund(orderModel).intValue());
	}

	@Test
	public void testCalculateWithZeroRefund()
	{
		final CancelPenaltyModel cancelPenalty = new CancelPenaltyModel();
		when(cancelPenaltiesCalculationService.getActiveCancelPenalty(Matchers.anyCollection(), Matchers.any(Date.class),
				Matchers.any(BigDecimal.class))).thenReturn(cancelPenalty);
		when(cancelPenaltiesCalculationService.getCancelPenaltyAmount(Matchers.any(CancelPenaltyModel.class),
				Matchers.any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(80));
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100));
		paymentTransactions
				.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.REFUND_STANDALONE, 50));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);

		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);

		Assert.assertEquals(0, defaultAccommodationTotalRefundCalculationStrategy.getTotalToRefund(orderModel).intValue());
	}

	@Test
	public void testCalculateWithNoRefundTransactions()
	{
		final CancelPenaltyModel cancelPenalty = new CancelPenaltyModel();
		when(cancelPenaltiesCalculationService.getActiveCancelPenalty(Matchers.anyCollection(), Matchers.any(Date.class),
				Matchers.any(BigDecimal.class))).thenReturn(cancelPenalty);
		when(cancelPenaltiesCalculationService.getCancelPenaltyAmount(Matchers.any(CancelPenaltyModel.class),
				Matchers.any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(80));
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.ACCOMMODATION, PaymentTransactionType.CAPTURE, 100));
		paymentTransactions.add(createPaymentTransaction(OrderEntryType.TRANSPORT, PaymentTransactionType.CAPTURE, 200));

		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);

		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);

		Assert.assertEquals(20, defaultAccommodationTotalRefundCalculationStrategy.getTotalToRefund(orderModel).intValue());
	}

	private PaymentTransactionModel createPaymentTransaction(final OrderEntryType orderType,
			final PaymentTransactionType paymentTransactionType, final double amount)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(orderType));
		final PaymentTransactionModel paymentModel = new PaymentTransactionModel();
		paymentModel.setAbstractOrderEntries(abstractOrderEntries);
		paymentModel.setPlannedAmount(BigDecimal.valueOf(amount));
		final List<PaymentTransactionEntryModel> entries = new ArrayList<>();
		entries.add(createPaymentTransactionEntryModel(paymentTransactionType, paymentModel, amount));
		paymentModel.setEntries(entries);

		return paymentModel;
	}

	private AbstractOrderEntryModel createOrderEntry(final OrderEntryType orderType)
	{
		final AbstractOrderEntryModel order = new AbstractOrderEntryModel();
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);
		entryGroup.setStartingDate(TravelDateUtils.convertStringDateToDate("05/01/2017", TravelservicesConstants.DATE_PATTERN));
		entryGroup.setRatePlan(createRatePlan());
		order.setEntryGroup(entryGroup);
		order.setType(orderType);
		return order;
	}

	private RatePlanModel createRatePlan()
	{
		final List<CancelPenaltyModel> cancelPenalities = new ArrayList<>();
		cancelPenalities.add(createCancelPenalty(false, "", false, "", false, 0d, false, 0d));
		cancelPenalities.add(createCancelPenalty(true, "03/01/2017", false, "", false, 0d, false, 0d));
		cancelPenalities.add(createCancelPenalty(true, "08/01/2017", false, "", false, 0d, false, 0d));
		cancelPenalities.add(createCancelPenalty(true, "03/01/2017", true, "02/02/2017", true, 10d, false, 10d));
		cancelPenalities.add(createCancelPenalty(true, "03/01/2017", true, "02/02/2017", true, 10d, true, 10d));
		cancelPenalities.add(createCancelPenalty(true, "03/01/2017", true, "02/02/2017", false, 10d, true, 10d));
		final RatePlanModel ratePlanModel = new RatePlanModel();
		ratePlanModel.setCancelPenalty(cancelPenalities);
		return ratePlanModel;
	}

	private CancelPenaltyModel createCancelPenalty(final boolean hasAbsoluteDeadline, final String absoluteDeadLineDate,
			final boolean hasRelativeDeadline, final String relativeDeadLineDate, final boolean hasFixedAmount,
			final double fixedAmount, final boolean hasPercentageAmount, final double percentageAmount)
	{
		final CancelPenaltyModel cancelPenaltyModel = new CancelPenaltyModel();
		if (hasAbsoluteDeadline)
		{
			cancelPenaltyModel.setAbsoluteDeadline(
					TravelDateUtils.convertStringDateToDate(absoluteDeadLineDate, TravelservicesConstants.DATE_PATTERN));
		}
		if (hasRelativeDeadline)
		{
			cancelPenaltyModel.setRelativeDeadline(
					TravelDateUtils.convertStringDateToDate(relativeDeadLineDate, TravelservicesConstants.DATE_PATTERN).getTime());
		}
		if (hasFixedAmount)
		{
			cancelPenaltyModel.setFixedAmount(fixedAmount);
		}
		if (hasPercentageAmount)
		{
			cancelPenaltyModel.setPercentageAmount(percentageAmount);
		}
		return cancelPenaltyModel;

	}

	private PaymentTransactionEntryModel createPaymentTransactionEntryModel(final PaymentTransactionType paymentTransactionType,
			final PaymentTransactionModel paymentTransaction, final double amount)
	{
		final PaymentTransactionEntryModel entryModel = new PaymentTransactionEntryModel();
		entryModel.setType(paymentTransactionType);
		entryModel.setPaymentTransaction(paymentTransaction);
		entryModel.setAmount(BigDecimal.valueOf(amount));
		return entryModel;
	}
}
