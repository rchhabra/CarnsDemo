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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.accommodation.strategies.OrderTotalByEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>, based on the total payment transactions.
 */
public class TotalPaymentTransactionRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.total.payment.transaction.alternative.message";

	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private OrderTotalByEntryTypeCalculationStrategy orderTotalByEntryTypeCalculationStrategy;

	@Override
	public void applyStrategy(final List<AccommodationBookingActionData> bookingActionDataList,
			final AccommodationReservationData reservationData)
	{
		final List<AccommodationBookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(AccommodationBookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(reservationData.getCode(), baseStoreModel);

		final BigDecimal totalAmount = getOrderTotalByEntryTypeCalculationStrategy().calculate(orderModel,
				OrderEntryType.ACCOMMODATION);

		final boolean enabled = totalAmount.compareTo(reservationData.getTotalRate().getActualRate().getValue()) < 0;

		if (!enabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}

	}

	/**
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the orderTotalByEntryTypeCalculationStrategy
	 */
	protected OrderTotalByEntryTypeCalculationStrategy getOrderTotalByEntryTypeCalculationStrategy()
	{
		return orderTotalByEntryTypeCalculationStrategy;
	}

	/**
	 * @param orderTotalByEntryTypeCalculationStrategy
	 *           the orderTotalByEntryTypeCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalByEntryTypeCalculationStrategy(
			final OrderTotalByEntryTypeCalculationStrategy orderTotalByEntryTypeCalculationStrategy)
	{
		this.orderTotalByEntryTypeCalculationStrategy = orderTotalByEntryTypeCalculationStrategy;
	}

}
