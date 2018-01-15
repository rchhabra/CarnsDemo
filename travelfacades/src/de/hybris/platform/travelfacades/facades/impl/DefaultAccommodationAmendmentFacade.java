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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.facades.AccommodationAmendmentFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelservices.order.TravelCartService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationAmendmentFacade}
 *
 */
public class DefaultAccommodationAmendmentFacade implements AccommodationAmendmentFacade
{
	private TravelCartService travelCartService;
	private BookingFacade bookingFacade;

	@Override
	public Boolean startAmendment(final String orderCode)
	{
		final CartModel cart = getTravelCartService().createCartFromOrder(orderCode, bookingFacade.getCurrentUserUid());
		if (cart != null)
		{
			getTravelCartService().setSessionCart(cart);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * @param bookingFacade
	 *           the bookingFacade to set
	 */
	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}

}
