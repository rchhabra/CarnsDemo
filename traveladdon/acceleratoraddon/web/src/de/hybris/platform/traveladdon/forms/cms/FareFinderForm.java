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
package de.hybris.platform.traveladdon.forms.cms;

import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;

import java.util.List;


/**
 * FareFinderForm object used to bind with the FareFinderComponent.jsp and uses JSR303 validation.
 */

public class FareFinderForm
{

	private List<PassengerTypeQuantityData> passengerTypeQuantityList;
	private Boolean travellingWithChildren;
	private String tripType;
	private String cabinClass;
	private String arrivalLocation;
	private String departureLocation;
	private String arrivalLocationName;
	private String departureLocationName;
	private String arrivalLocationSuggestionType;
	private String departureLocationSuggestionType;
	private String departingDateTime;
	private String returnDateTime;

	public String getArrivalLocationName()
	{
		return arrivalLocationName;
	}

	public void setArrivalLocationName(final String arrivalLocationName)
	{
		this.arrivalLocationName = arrivalLocationName;
	}

	public String getDepartureLocationName()
	{
		return departureLocationName;
	}

	public void setDepartureLocationName(final String departureLocationName)
	{
		this.departureLocationName = departureLocationName;
	}

	public String getArrivalLocation()
	{
		return arrivalLocation;
	}

	public void setArrivalLocation(final String arrivalLocation)
	{
		this.arrivalLocation = arrivalLocation;
	}

	public String getDepartureLocation()
	{
		return departureLocation;
	}

	public void setDepartureLocation(final String departureLocation)
	{
		this.departureLocation = departureLocation;
	}

	public String getDepartingDateTime()
	{
		return departingDateTime;
	}

	public void setDepartingDateTime(final String departingDateTime)
	{
		this.departingDateTime = departingDateTime;
	}

	public String getReturnDateTime()
	{
		return returnDateTime;
	}

	public void setReturnDateTime(final String returnDateTime)
	{
		this.returnDateTime = returnDateTime;
	}

	public String getTripType()
	{
		return tripType;
	}

	public void setTripType(final String tripType)
	{
		this.tripType = tripType;
	}

	public List<PassengerTypeQuantityData> getPassengerTypeQuantityList()
	{
		return passengerTypeQuantityList;
	}

	public void setPassengerTypeQuantityList(final List<PassengerTypeQuantityData> passengerTypeQuantityList)
	{
		this.passengerTypeQuantityList = passengerTypeQuantityList;
	}

	public String getCabinClass()
	{
		return cabinClass;
	}

	public void setCabinClass(final String cabinClass)
	{
		this.cabinClass = cabinClass;
	}

	public Boolean getTravellingWithChildren()
	{
		return travellingWithChildren;
	}

	public void setTravellingWithChildren(final Boolean travellingWithChildren)
	{
		this.travellingWithChildren = travellingWithChildren;
	}

	public String getArrivalLocationSuggestionType()
	{
		return arrivalLocationSuggestionType;
	}

	public void setArrivalLocationSuggestionType(final String arrivalLocationSuggestionType)
	{
		this.arrivalLocationSuggestionType = arrivalLocationSuggestionType;
	}

	public String getDepartureLocationSuggestionType()
	{
		return departureLocationSuggestionType;
	}

	public void setDepartureLocationSuggestionType(final String departureLocationSuggestionType)
	{
		this.departureLocationSuggestionType = departureLocationSuggestionType;
	}

}
