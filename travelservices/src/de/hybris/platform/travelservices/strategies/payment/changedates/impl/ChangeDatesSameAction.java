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
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesPaymentActionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link= ChangeDatesPaymentActionStrategy} in case the payment type is SAME
 */
public class ChangeDatesSameAction implements ChangeDatesPaymentActionStrategy
{
	private CartService cartService;
	private BookingService bookingService;
	private ModelService modelService;

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
			changeDateEntries.add(cartEntryModel);
		}

		final boolean isSuccess = getBookingService().linkEntriesToOldPaymentTransactions(accommodationOrderEntryGroup,
				changeDateEntries);
		if (!isSuccess)
		{
			return Boolean.FALSE;
		}

		final List<AbstractOrderEntryModel> entries = new ArrayList<>(accommodationOrderEntryGroup.getEntries());
		entries.addAll(changeDateEntries);
		accommodationOrderEntryGroup.setEntries(entries);
		getModelService().save(accommodationOrderEntryGroup);
		getModelService().refresh(sessionCart);
		return Boolean.TRUE;

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
}
