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

import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;

import java.util.List;


public class AccommodationAvailabilityForm
{
	private String checkInDateTime;
	private String checkOutDateTime;
	private String numberOfRooms;
	private List<RoomStayCandidateData> roomStayCandidates;


	public String getCheckInDateTime()
	{
		return checkInDateTime;
	}

	public void setCheckInDateTime(final String checkInDateTime)
	{
		this.checkInDateTime = checkInDateTime;
	}

	public String getCheckOutDateTime()
	{
		return checkOutDateTime;
	}

	public void setCheckOutDateTime(final String checkOutDateTime)
	{
		this.checkOutDateTime = checkOutDateTime;
	}

	public String getNumberOfRooms()
	{
		return numberOfRooms;
	}

	public void setNumberOfRooms(final String numberOfRooms)
	{
		this.numberOfRooms = numberOfRooms;
	}

	public List<RoomStayCandidateData> getRoomStayCandidates()
	{
		return roomStayCandidates;
	}

	public void setRoomStayCandidates(final List<RoomStayCandidateData> roomStayCandidates)
	{
		this.roomStayCandidates = roomStayCandidates;
	}

}
