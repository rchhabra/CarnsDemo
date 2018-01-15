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
package de.hybris.platform.travelservices.strategies.impl;


import de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.TravelCheckoutCustomerStrategy;

import org.apache.commons.lang.StringUtils;


public class DefaultTravelCheckoutCustomerStrategy extends DefaultCheckoutCustomerStrategy
		implements TravelCheckoutCustomerStrategy
{

	private static final String ANONYMOUS_CHECKOUT_GUID = "anonymous_checkout_guid";
	private static final String MANAGE_MY_BOOKING_GUEST_UID = "manage_my_booking_guest_uid";

	private BookingService bookingService;
	private SessionService sessionService;

	@Override
	public boolean isValidBookingForCurrentGuestUser(final String bookingReference)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		String guestBookingGuid = getSessionService().getAttribute(MANAGE_MY_BOOKING_GUEST_UID);

		if (guestBookingGuid != null && guestBookingGuid.contains("//|)"))
		{
			guestBookingGuid = StringUtils.substringAfter(guestBookingGuid, "|");
		}
		else
		{
			guestBookingGuid = StringUtils.substringAfter(orderModel.getUser().getUid(), "|");
		}
		final String currentGuestGuid = getSessionService().getAttribute(ANONYMOUS_CHECKOUT_GUID);

		return StringUtils.equalsIgnoreCase(guestBookingGuid, currentGuestGuid);
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService the bookingService to set
	 */
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
