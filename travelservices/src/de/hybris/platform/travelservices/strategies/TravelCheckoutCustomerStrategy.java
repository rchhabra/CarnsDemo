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
package de.hybris.platform.travelservices.strategies;


import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;


/**
 * The interface Travel checkout customer strategy.
 */
public interface TravelCheckoutCustomerStrategy extends CheckoutCustomerStrategy
{

	/**
	 * Checks if the given booking reference is valid booking for the current guest user
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return boolean boolean
	 */
	boolean isValidBookingForCurrentGuestUser(String bookingReference);
}
