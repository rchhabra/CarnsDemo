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

import java.util.List;

import javax.validation.Valid;


public class TravellerDetails
{
	@Valid
	private List<TravellerForm> travellerForms;

	private Boolean additionalSecurityActive;

	public List<TravellerForm> getTravellerForms()
	{
		return travellerForms;
	}

	public void setTravellerForms(final List<TravellerForm> travellerForms)
	{
		this.travellerForms = travellerForms;
	}

	public Boolean getAdditionalSecurityActive()
	{
		return additionalSecurityActive;
	}

	public void setAdditionalSecurityActive(final Boolean additionalSecurityActive)
	{
		this.additionalSecurityActive = additionalSecurityActive;
	}
}
