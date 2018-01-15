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
 */

package de.hybris.platform.traveladdon.forms;

import javax.validation.constraints.NotNull;


/**
 * The AddRemoveAccommodation is used for adding or removing accommodation from the ancillary page.
 */
public class AddRemoveAccommodation
{
	private String accommodationUid;
	private String previousSelectedAccommodationUid;
	@NotNull
	private String transportOfferingCode;
	@NotNull
	private String travellerCode;
	@NotNull
	private String originDestinationRefNo;
	@NotNull
	private String travelRoute;


	/**
	 * @return the accommodationUid
	 */
	public String getAccommodationUid()
	{
		return accommodationUid;
	}

	/**
	 * @param accommodationUid
	 *           the accommodationUid to set
	 */
	public void setAccommodationUid(final String accommodationUid)
	{
		this.accommodationUid = accommodationUid;
	}

	/**
	 * @return the previousSelectedAccommodationUid
	 */
	public String getPreviousSelectedAccommodationUid()
	{
		return previousSelectedAccommodationUid;
	}

	/**
	 * @param previousSelectedAccommodationUid
	 *           the previousSelectedAccommodationUid to set
	 */
	public void setPreviousSelectedAccommodationUid(final String previousSelectedAccommodationUid)
	{
		this.previousSelectedAccommodationUid = previousSelectedAccommodationUid;
	}

	/**
	 * @return the transportOfferingCode
	 */
	public String getTransportOfferingCode()
	{
		return transportOfferingCode;
	}

	/**
	 * @param transportOfferingCode
	 *           the transportOfferingCode to set
	 */
	public void setTransportOfferingCode(final String transportOfferingCode)
	{
		this.transportOfferingCode = transportOfferingCode;
	}

	/**
	 * @return the travellerCode
	 */
	public String getTravellerCode()
	{
		return travellerCode;
	}

	/**
	 * @param travellerCode
	 *           the travellerCode to set
	 */
	public void setTravellerCode(final String travellerCode)
	{
		this.travellerCode = travellerCode;
	}

	/**
	 * @return the originDestinationRefNo
	 */
	public String getOriginDestinationRefNo()
	{
		return originDestinationRefNo;
	}

	/**
	 * @param originDestinationRefNo
	 *           the originDestinationRefNo to set
	 */
	public void setOriginDestinationRefNo(final String originDestinationRefNo)
	{
		this.originDestinationRefNo = originDestinationRefNo;
	}

	/**
	 * @return the travelRoute
	 */
	public String getTravelRoute()
	{
		return travelRoute;
	}

	/**
	 * @param travelRoute
	 *           the travelRoute to set
	 */
	public void setTravelRoute(final String travelRoute)
	{
		this.travelRoute = travelRoute;
	}
}
