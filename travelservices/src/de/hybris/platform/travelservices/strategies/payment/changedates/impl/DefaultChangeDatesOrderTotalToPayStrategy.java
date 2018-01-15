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

package de.hybris.platform.travelservices.strategies.payment.changedates.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RatePlanService;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesOrderTotalToPayStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/*
 * Implementation of @link={ChangeDatesOrderTotalToPayStrategy}
 */
public class DefaultChangeDatesOrderTotalToPayStrategy implements ChangeDatesOrderTotalToPayStrategy
{
	private BookingService bookingService;

	private CartService cartService;

	private RatePlanService ratePlanService;

	private TimeService timeService;

	@Override
	public BigDecimal calculate()
	{
		final CartModel cartModel = getCartService().getSessionCart();

		if (Objects.isNull(cartModel))
		{
			return null;
		}

		final List<AbstractOrderEntryModel> activeEntries = cartModel.getEntries().stream()
				.filter(entry -> entry.getActive() && OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(activeEntries))
		{
			return null;
		}

		final OrderModel orderModel = cartModel.getOriginalOrder();

		if (Objects.isNull(orderModel))
		{
			return null;
		}

		final Double totalAmountPaid = getBookingService()
				.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.ACCOMMODATION).doubleValue();

		final Double totalOrderAmount = getBookingService().getOrderTotalPriceByType(orderModel, OrderEntryType.ACCOMMODATION);

		final boolean isPartialPayment = totalAmountPaid < totalOrderAmount;

		Double totalCartAmount = 0d;

		for (final AbstractOrderEntryModel entry : activeEntries)
		{
			Double totalEntryPrice = 0d;

			totalEntryPrice = Double.sum(totalEntryPrice, entry.getTotalPrice());


			if (CollectionUtils.isNotEmpty(entry.getTaxValues()))
			{
				totalEntryPrice = Double.sum(totalEntryPrice,
						entry.getTaxValues().stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum());
			}

			final boolean isRoomRateProductModelEntry = entry.getProduct() instanceof RoomRateProductModel;


			if (!isRoomRateProductModelEntry && isPartialPayment)
			{
				continue;
			}
			else if (isRoomRateProductModelEntry && isPartialPayment)
			{
				final AccommodationOrderEntryGroupModel group = (AccommodationOrderEntryGroupModel) entry.getEntryGroup();
				final GuaranteeModel guaranteeToApply = getRatePlanService().getGuaranteeToApply(group, group.getStartingDate(),
						getTimeService().getCurrentTime());

				if (guaranteeToApply == null)
				{
					continue;
				}

				final Double guaranteeAmount = getRatePlanService().getAppliedGuaranteeAmount(guaranteeToApply,
						BigDecimal.valueOf(totalEntryPrice));

				totalCartAmount = Double.sum(totalCartAmount, guaranteeAmount);
			}
			else
			{
				totalCartAmount = Double.sum(totalCartAmount, totalEntryPrice);
			}
		}

		return BigDecimal.valueOf(totalCartAmount - totalAmountPaid);
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the ratePlanService
	 */
	protected RatePlanService getRatePlanService()
	{
		return ratePlanService;
	}

	/**
	 * @param ratePlanService
	 *           the ratePlanService to set
	 */
	@Required
	public void setRatePlanService(final RatePlanService ratePlanService)
	{
		this.ratePlanService = ratePlanService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
