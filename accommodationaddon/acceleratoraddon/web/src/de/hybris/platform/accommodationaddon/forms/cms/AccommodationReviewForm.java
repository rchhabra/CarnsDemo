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

package de.hybris.platform.accommodationaddon.forms.cms;

import de.hybris.platform.validation.annotations.NotEmpty;

import javax.validation.constraints.NotNull;


public class AccommodationReviewForm
{
	@NotEmpty
	private String headline;
	@NotEmpty
	private String comment;
	@NotNull
	private double rating;
	@NotNull
	private int roomStayRefNumber;
	@NotEmpty
	private String accommodationCode;
	@NotEmpty
	private String accommodationOfferingCode;


	public String getHeadline()
	{
		return headline;
	}

	public void setHeadline(final String headline)
	{
		this.headline = headline;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(final double rating)
	{
		this.rating = rating;
	}

	public int getRoomStayRefNumber()
	{
		return roomStayRefNumber;
	}

	public void setRoomStayRefNumber(final int roomStayRefNumber)
	{
		this.roomStayRefNumber = roomStayRefNumber;
	}

	public String getAccommodationCode()
	{
		return accommodationCode;
	}

	public void setAccommodationCode(final String accommodationCode)
	{
		this.accommodationCode = accommodationCode;
	}

	public String getAccommodationOfferingCode()
	{
		return accommodationOfferingCode;
	}

	public void setAccommodationOfferingCode(final String accommodationOfferingCode)
	{
		this.accommodationOfferingCode = accommodationOfferingCode;
	}


}
