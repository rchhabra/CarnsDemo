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

package de.hybris.platform.accommodationaddon.forms;

import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;

import java.util.List;


public class LeadGuestDetailsForm
{
	private GuestData guestData;
	private List<PassengerTypeQuantityData> passengerTypeQuantityData;
	private String arrivalTime;
	private String formId;
	private String roomStayRefNumber;
	private List<String> roomPreferenceCodes;
	private boolean notRemoved;

	public GuestData getGuestData()
	{
		return guestData;
	}

	public void setGuestData(final GuestData guestData)
	{
		this.guestData = guestData;
	}

	public List<PassengerTypeQuantityData> getPassengerTypeQuantityData()
	{
		return passengerTypeQuantityData;
	}

	public void setPassengerTypeQuantityData(final List<PassengerTypeQuantityData> passengerTypeQuantityData)
	{
		this.passengerTypeQuantityData = passengerTypeQuantityData;
	}

	public String getArrivalTime()
	{
		return arrivalTime;
	}

	public void setArrivalTime(final String arrivalTime)
	{
		this.arrivalTime = arrivalTime;
	}

	public String getFormId()
	{
		return formId;
	}

	public void setFormId(final String formId)
	{
		this.formId = formId;
	}

	/**
	 * @return the roomStayRefNumber
	 */
	public String getRoomStayRefNumber()
	{
		return roomStayRefNumber;
	}

	/**
	 * @param roomStayRefNumber
	 *           the roomStayRefNumber to set
	 */
	public void setRoomStayRefNumber(final String roomStayRefNumber)
	{
		this.roomStayRefNumber = roomStayRefNumber;
	}

	/**
	 * @return the roomPreferenceCodes
	 */
	public List<String> getRoomPreferenceCodes()
	{
		return roomPreferenceCodes;
	}

	/**
	 * @param roomPreferenceCodes
	 *           the roomPreferenceCodes to set
	 */
	public void setRoomPreferenceCodes(final List<String> roomPreferenceCodes)
	{
		this.roomPreferenceCodes = roomPreferenceCodes;
	}

	/**
	 * @return the notRemoved
	 */
	public boolean isNotRemoved()
	{
		return notRemoved;
	}

	/**
	 * @param notRemoved
	 *           the notRemoved to set
	 */
	public void setNotRemoved(final boolean notRemoved)
	{
		this.notRemoved = notRemoved;
	}
}
