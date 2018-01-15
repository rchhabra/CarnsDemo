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

import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;


public class TravelFinderForm
{
	private FareFinderForm fareFinderForm;
	private AccommodationFinderForm accommodationFinderForm;

	/**
	 * @return the fareFinderForm
	 */
	public FareFinderForm getFareFinderForm()
	{
		return fareFinderForm;
	}

	/**
	 * @param fareFinderForm
	 *           the fareFinderForm to set
	 */
	public void setFareFinderForm(final FareFinderForm fareFinderForm)
	{
		this.fareFinderForm = fareFinderForm;
	}

	/**
	 * @return the accommodationFinderForm
	 */
	public AccommodationFinderForm getAccommodationFinderForm()
	{
		return accommodationFinderForm;
	}

	/**
	 * @param accommodationFinderForm
	 *           the accommodationFinderForm to set
	 */
	public void setAccommodationFinderForm(final AccommodationFinderForm accommodationFinderForm)
	{
		this.accommodationFinderForm = accommodationFinderForm;
	}


}
