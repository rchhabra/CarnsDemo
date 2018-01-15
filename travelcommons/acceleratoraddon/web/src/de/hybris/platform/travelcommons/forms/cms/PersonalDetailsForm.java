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

package de.hybris.platform.travelcommons.forms.cms;

import de.hybris.platform.accommodationaddon.forms.LeadGuestDetailsForm;
import de.hybris.platform.traveladdon.forms.TravellerForm;

import java.util.List;

import javax.validation.Valid;


public class PersonalDetailsForm
{
	@Valid
	private List<TravellerForm> travellerForms;

	@Valid
	private List<LeadGuestDetailsForm> leadForms;

	private boolean useDiffLeadDetails;

	/**
	 * @return the travellerForms
	 */
	public List<TravellerForm> getTravellerForms()
	{
		return travellerForms;
	}

	/**
	 * @return the leadForms
	 */
	public List<LeadGuestDetailsForm> getLeadForms()
	{
		return leadForms;
	}

	/**
	 * @param travellerForms
	 *           the travellerForms to set
	 */
	public void setTravellerForms(final List<TravellerForm> travellerForms)
	{
		this.travellerForms = travellerForms;
	}

	/**
	 * @param leadForms
	 *           the leadForms to set
	 */
	public void setLeadForms(final List<LeadGuestDetailsForm> leadForms)
	{
		this.leadForms = leadForms;
	}

	/**
	 * @return the useDiffLeadDetails
	 */
	public boolean isUseDiffLeadDetails()
	{
		return useDiffLeadDetails;
	}

	/**
	 * @param useDiffLeadDetails
	 *           the useDiffLeadDetails to set
	 */
	public void setUseDiffLeadDetails(final boolean useDiffLeadDetails)
	{
		this.useDiffLeadDetails = useDiffLeadDetails;
	}

}
