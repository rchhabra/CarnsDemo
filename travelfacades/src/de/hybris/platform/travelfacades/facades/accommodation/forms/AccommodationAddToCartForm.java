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

package de.hybris.platform.travelfacades.facades.accommodation.forms;

import java.util.List;

import javax.validation.constraints.NotNull;


public class AccommodationAddToCartForm
{
	@NotNull
	private int numberOfRooms;

	@NotNull
	private String checkInDateTime;

	@NotNull
	private String checkOutDateTime;

	@NotNull
	private String accommodationOfferingCode;

	@NotNull
	private String accommodationCode;

	@NotNull
	private String ratePlanCode;

	private Integer roomStayRefNumber;

	private List<String> roomRateCodes;

	private List<String> roomRateDates;

	/**
	 * @return the numberOfRooms
	 */
	public int getNumberOfRooms()
	{
		return numberOfRooms;
	}

	/**
	 * @param numberOfRooms
	 *           the numberOfRooms to set
	 */
	public void setNumberOfRooms(final int numberOfRooms)
	{
		this.numberOfRooms = numberOfRooms;
	}

	/**
	 * @return the checkInDateTime
	 */
	public String getCheckInDateTime()
	{
		return checkInDateTime;
	}

	/**
	 * @param checkInDateTime
	 *           the checkInDateTime to set
	 */
	public void setCheckInDateTime(final String checkInDateTime)
	{
		this.checkInDateTime = checkInDateTime;
	}

	/**
	 * @return the checkOutDateTime
	 */
	public String getCheckOutDateTime()
	{
		return checkOutDateTime;
	}

	/**
	 * @param checkOutDateTime
	 *           the checkOutDateTime to set
	 */
	public void setCheckOutDateTime(final String checkOutDateTime)
	{
		this.checkOutDateTime = checkOutDateTime;
	}

	/**
	 * @return the accommodationOfferingCode
	 */
	public String getAccommodationOfferingCode()
	{
		return accommodationOfferingCode;
	}

	/**
	 * @param accommodationOfferingCode
	 *           the accommodationOfferingCode to set
	 */
	public void setAccommodationOfferingCode(final String accommodationOfferingCode)
	{
		this.accommodationOfferingCode = accommodationOfferingCode;
	}

	/**
	 * @return the accommodationCode
	 */
	public String getAccommodationCode()
	{
		return accommodationCode;
	}

	/**
	 * @param accommodationCode
	 *           the accommodationCode to set
	 */
	public void setAccommodationCode(final String accommodationCode)
	{
		this.accommodationCode = accommodationCode;
	}

	/**
	 * @return the ratePlanCode
	 */
	public String getRatePlanCode()
	{
		return ratePlanCode;
	}

	/**
	 * @param ratePlanCode
	 *           the ratePlanCode to set
	 */
	public void setRatePlanCode(final String ratePlanCode)
	{
		this.ratePlanCode = ratePlanCode;
	}

	/**
	 * @return the roomStayRefNumber
	 */
	public Integer getRoomStayRefNumber()
	{
		return roomStayRefNumber;
	}

	/**
	 * @param roomStayRefNumber
	 *           the roomStayRefNumber to set
	 */
	public void setRoomStayRefNumber(final Integer roomStayRefNumber)
	{
		this.roomStayRefNumber = roomStayRefNumber;
	}

	/**
	 * @return the roomRateCodes
	 */
	public List<String> getRoomRateCodes()
	{
		return roomRateCodes;
	}

	/**
	 * @param roomRateCodes
	 *           the roomRateCodes to set
	 */
	public void setRoomRateCodes(final List<String> roomRateCodes)
	{
		this.roomRateCodes = roomRateCodes;
	}

	/**
	 * @return the roomRateDates
	 */
	public List<String> getRoomRateDates()
	{
		return roomRateDates;
	}

	/**
	 * @param roomRateDates
	 *           the roomRateDates to set
	 */
	public void setRoomRateDates(final List<String> roomRateDates)
	{
		this.roomRateDates = roomRateDates;
	}

}
