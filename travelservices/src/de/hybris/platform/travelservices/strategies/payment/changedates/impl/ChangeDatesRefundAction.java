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
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesPaymentActionStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link= ChangeDatesPaymentActionStrategy} in case the payment type is REFUND
 */
public class ChangeDatesRefundAction implements ChangeDatesPaymentActionStrategy
{
	private CartService cartService;
	private BookingService bookingService;
	private ModelService modelService;
	private OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy;

	@Override
	public boolean takeAction(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup,
			final List<Integer> entryNumbers)
	{
		if (accommodationOrderEntryGroup == null || CollectionUtils.isEmpty(entryNumbers))
		{
			return Boolean.FALSE;
		}

		final CartModel sessionCart = getCartService().getSessionCart();

		if (sessionCart == null)
		{
			return Boolean.FALSE;
		}

		final List<AbstractOrderEntryModel> changeDateEntries = new ArrayList<>(entryNumbers.size());
		for (final Integer entryNumber : entryNumbers)
		{
			final AbstractOrderEntryModel cartEntryModel = getCartService().getEntryForNumber(sessionCart, entryNumber);
			if (Objects.isNull(cartEntryModel))
			{
				return Boolean.FALSE;
			}
			if (Objects.isNull(cartEntryModel.getEntryGroup())
					|| accommodationOrderEntryGroup.equals(cartEntryModel.getEntryGroup()))
			{
				changeDateEntries.add(cartEntryModel);
			}
		}

		final double newEntryGroupPriceWithoutGaurantee = getChangeDatesEntryPriceWithoutGuarantees(changeDateEntries,
				accommodationOrderEntryGroup);
		final double oldEntryGroupPricePayed = getOrderTotalPaidForAccommodationGroupCalculationStrategy()
				.calculate(sessionCart.getOriginalOrder(), accommodationOrderEntryGroup).doubleValue();

		boolean isSuccess;

		final List<AbstractOrderEntryModel> entries = new ArrayList<>(accommodationOrderEntryGroup.getEntries());
		entries.addAll(changeDateEntries);
		accommodationOrderEntryGroup.setEntries(entries);
		getModelService().save(accommodationOrderEntryGroup);
		getModelService().saveAll(entries);
		getModelService().refresh(sessionCart);

		//When Amount Paid is greater than the new Total Amount of New Entries for the Group.
		if (oldEntryGroupPricePayed - newEntryGroupPriceWithoutGaurantee > 0)
		{
			isSuccess = getBookingService().createRefundPaymentTransaction(sessionCart,
					BigDecimal.valueOf(oldEntryGroupPricePayed - newEntryGroupPriceWithoutGaurantee),
					accommodationOrderEntryGroup.getEntries());
		}
		else
		{
			//When Amount Paid is either zero or equal to the new Total Amount of New Entries for the Group.
			//This is the scenario when there is one Free Cancellation and One No Refund Rate Plan Accommodation in order
			// OR
			//When Amount Paid is less than the new Total Amount of New Entries for the Group.
			isSuccess = getBookingService().linkEntriesToOldPaymentTransactions(accommodationOrderEntryGroup, changeDateEntries);
		}

		return isSuccess;
	}

	protected Double getChangeDatesEntryPriceWithoutGuarantees(final List<AbstractOrderEntryModel> entries,
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup)
	{
		double totalPayablePrice = 0d;
		for (final AbstractOrderEntryModel entry : entries)
		{
			totalPayablePrice = Double.sum(totalPayablePrice, getTotalEntryPrice(entry));
		}
		totalPayablePrice = Double.sum(totalPayablePrice,
				accommodationOrderEntryGroup.getEntries().stream()
						.filter(entry -> entry.getActive() && !(entry.getProduct() instanceof RoomRateProductModel))
						.mapToDouble(entry -> entry.getTotalPrice()).sum());
		return totalPayablePrice;
	}

	protected Double getTotalEntryPrice(final AbstractOrderEntryModel entry)
	{
		double totalPayablePrice = 0d;
		totalPayablePrice = Double.sum(totalPayablePrice, entry.getTotalPrice());
		totalPayablePrice = Double.sum(totalPayablePrice,
				entry.getTaxValues().stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum());
		return totalPayablePrice;
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
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the orderTotalPaidForAccommodationGroupCalculationStrategy
	 */
	protected OrderTotalPaidForEntryGroupCalculationStrategy getOrderTotalPaidForAccommodationGroupCalculationStrategy()
	{
		return orderTotalPaidForAccommodationGroupCalculationStrategy;
	}

	/**
	 * @param orderTotalPaidForAccommodationGroupCalculationStrategy
	 *           the orderTotalPaidForAccommodationGroupCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidForAccommodationGroupCalculationStrategy(
			final OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy)
	{
		this.orderTotalPaidForAccommodationGroupCalculationStrategy = orderTotalPaidForAccommodationGroupCalculationStrategy;
	}
}
