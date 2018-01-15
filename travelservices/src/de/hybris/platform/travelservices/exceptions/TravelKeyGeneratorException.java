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
*/
package de.hybris.platform.travelservices.exceptions;

public class TravelKeyGeneratorException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public TravelKeyGeneratorException(final String message, final Throwable nested)
	{
		super(message, nested);
	}

	public TravelKeyGeneratorException(final String message)
	{
		super(message);
	}
}
