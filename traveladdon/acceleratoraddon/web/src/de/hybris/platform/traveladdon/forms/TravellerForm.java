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

package de.hybris.platform.traveladdon.forms;

import javax.validation.Valid;


public class TravellerForm implements Comparable<TravellerForm>
{
	private String formId;
	private String selectedSavedTravellerUId;
	private String uid;
	private String label;
	private String travellerType;
	private Boolean booker;
	private boolean specialAssistance;

	@Valid
	private PassengerInformationForm passengerInformation;

	public String getUid()
	{
		return uid;
	}

	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	public String getTravellerType()
	{
		return travellerType;
	}

	public void setTravellerType(final String travellerType)
	{
		this.travellerType = travellerType;
	}

	public PassengerInformationForm getPassengerInformation()
	{
		return passengerInformation;
	}

	public void setPassengerInformation(final PassengerInformationForm passengerInformation)
	{
		this.passengerInformation = passengerInformation;
	}

	@Override
	public int compareTo(final TravellerForm o)
	{
		return this.getLabel().compareTo(o.getLabel());
	}

	public boolean isSpecialAssistance()
	{
		return specialAssistance;
	}

	public void setSpecialAssistance(final boolean specialAssistance)
	{
		this.specialAssistance = specialAssistance;
	}

	public String getSelectedSavedTravellerUId()
	{
		return selectedSavedTravellerUId;
	}

	public void setSelectedSavedTravellerUId(final String selectedSavedTravellerUId)
	{
		this.selectedSavedTravellerUId = selectedSavedTravellerUId;
	}

	public String getFormId()
	{
		return formId;
	}

	public void setFormId(final String formId)
	{
		this.formId = formId;
	}

	public Boolean getBooker()
	{
		return booker;
	}

	public void setBooker(final Boolean booker)
	{
		this.booker = booker;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(final String label)
	{
		this.label = label;
	}

}
