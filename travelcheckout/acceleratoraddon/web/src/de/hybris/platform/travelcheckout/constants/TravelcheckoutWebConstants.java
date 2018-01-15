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
package de.hybris.platform.travelcheckout.constants;

/**
 * Global class for all Travelcheckout web constants. You can add global constants for your extension into this class.
 */
public final class TravelcheckoutWebConstants
{
	private TravelcheckoutWebConstants()
	{
		// empty to avoid instantiating this constant class
	}

	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String NEXT_URL = "nextURL";
	public static final String HIDE_CONTINUE = "hideContinue";
	public static final String RESERVATION = "reservation";
	public static final String HIDE = "HIDE";
	public static final String REDIRECT_URL_BOOKING_CONFIRMATION = REDIRECT_PREFIX + "/checkout/bookingConfirmation/";
	public static final String AMEND = "amend";

}
