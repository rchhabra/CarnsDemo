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


public class AccommodationFinderForm
{
	private String destinationLocation;
	private String destinationLocationName;
	private String suggestionType;
	private String checkInDateTime;
	private String checkOutDateTime;
	private String latitude;
	private String longitude;
	private String radius;
	private String numberOfRooms;
	private List<RoomStayCandidateData> roomStayCandidates;
	private Boolean partHotelStay;

	public String getDestinationLocation()
	{
		return destinationLocation;
	}

	public void setDestinationLocation(final String destinationLocation)
	{
		this.destinationLocation = destinationLocation;
	}

	public String getDestinationLocationName()
	{
		return destinationLocationName;
	}

	public void setDestinationLocationName(final String destinationLocationName)
	{
		this.destinationLocationName = destinationLocationName;
	}

	public String getSuggestionType()
	{
		return suggestionType;
	}

	public void setSuggestionType(final String suggestionType)
	{
		this.suggestionType = suggestionType;
	}

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

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(final String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(final String longitude)
	{
		this.longitude = longitude;
	}

	public String getRadius()
	{
		return radius;
	}

	public void setRadius(final String radius)
	{
		this.radius = radius;
	}

	public List<RoomStayCandidateData> getRoomStayCandidates()
	{
		return roomStayCandidates;
	}

	public void setRoomStayCandidates(final List<RoomStayCandidateData> roomStayCandidates)
	{
		this.roomStayCandidates = roomStayCandidates;
	}

	/**
	 * @return the partHotelStay
	 */
	public Boolean getPartHotelStay()
	{
		return partHotelStay;
	}

	/**
	 * @param partHotelStay
	 *           the partHotelStay to set
	 */
	public void setPartHotelStay(final Boolean partHotelStay)
	{
		this.partHotelStay = partHotelStay;
	}

}
