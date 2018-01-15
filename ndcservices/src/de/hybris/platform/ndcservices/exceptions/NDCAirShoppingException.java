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
package de.hybris.platform.ndcservices.exceptions;

/**
 * The type Ndc air shopping exception.
 */
public class NDCAirShoppingException extends NDCException
{
	/**
	 * Default constructor
	 */
	public NDCAirShoppingException()
	{
		super();
	}

	/**
	 * Instantiates a new Ndc air shopping exception.
	 *
	 * @param message
	 * 		the message
	 * @param cause
	 * 		the cause
	 */
	public NDCAirShoppingException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new Ndc air shopping exception.
	 *
	 * @param message
	 * 		the message
	 */
	public NDCAirShoppingException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new Ndc air shopping exception.
	 *
	 * @param cause
	 * 		the cause
	 */
	public NDCAirShoppingException(final Throwable cause)
	{
		super(cause);
	}
}
