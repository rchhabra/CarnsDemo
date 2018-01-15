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
 */
package de.hybris.platform.ndcservices.constants;

/**
 * Global class for all Ndcservices constants. You can add global constants for your extension into this class.
 */
public final class NdcservicesConstants extends GeneratedNdcservicesConstants
{
	public static final String EXTENSIONNAME = "ndcservices";

	private NdcservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String PAST_DATE = "ndcservices.error.pastdate";

	public static final int MIN_BOOKING_ADVANCE_TIME = 2;

	public static final int OUTBOUND_FLIGHT_REF_NUMBER = 0;
	public static final int INBOUND_FLIGHT_REF_NUMBER = 1;

	public static final int RETURN_FLIGHT_LEG_NUMBER = 2;

	public static final int ONE_WAY_FLIGHT = 1;
	public static final int RETURN_FLIGHT = 2;
}
