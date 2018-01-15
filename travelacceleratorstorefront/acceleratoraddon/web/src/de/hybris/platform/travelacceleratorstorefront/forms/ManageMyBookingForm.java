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
/**
 * Form class to hold the details of ManageMyBookingForm.
 */
package de.hybris.platform.travelacceleratorstorefront.forms;

public class ManageMyBookingForm
{
	private String bookingReference;

	private String lastName;

	public String getBookingReference()
	{
		return bookingReference;
	}

	public void setBookingReference(final String bookingReference)
	{
		this.bookingReference = bookingReference;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

}
