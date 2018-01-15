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
 * Exception is thrown when an attempt to create, pay retrieve or amend an order through NDC
 */
public class NDCOrderException extends NDCException
{

	/**
	 * Default constructor
	 */
	public NDCOrderException()
	{
		super();
	}

	/**
	 * Instantiates a new Ndc order exception.
	 *
	 * @param message
	 * 		the message
	 * @param cause
	 * 		the cause
	 */
	public NDCOrderException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new Ndc order exception.
	 *
	 * @param message
	 * 		the message
	 */
	public NDCOrderException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new Ndc order exception.
	 *
	 * @param cause
	 * 		the cause
	 */
	public NDCOrderException(final Throwable cause)
	{
		super(cause);
	}

}
