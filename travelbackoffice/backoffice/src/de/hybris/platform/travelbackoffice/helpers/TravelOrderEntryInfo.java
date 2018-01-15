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

package de.hybris.platform.travelbackoffice.helpers;

import de.hybris.platform.ondemandcommon.backoffice.helpers.OrderEntryInfo;


/**
 *
 */
public class TravelOrderEntryInfo extends OrderEntryInfo
{
	private String originDestinationRefNumber;

	private String travelRoute;

	private String transportOfferings;

	private String travellers;

	/**
	 * @return the originDestinationRefNumber
	 */
	public String getOriginDestinationRefNumber()
	{
		return originDestinationRefNumber;
	}

	/**
	 * @param originDestinationRefNumber
	 *           the originDestinationRefNumber to set
	 */
	public void setOriginDestinationRefNumber(final String originDestinationRefNumber)
	{
		this.originDestinationRefNumber = originDestinationRefNumber;
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

	/**
	 * @return the transportOfferings
	 */
	public String getTransportOfferings()
	{
		return transportOfferings;
	}

	/**
	 * @param transportOfferings
	 *           the transportOfferings to set
	 */
	public void setTransportOfferings(final String transportOfferings)
	{
		this.transportOfferings = transportOfferings;
	}

	/**
	 * @return the travellers
	 */
	public String getTravellers()
	{
		return travellers;
	}

	/**
	 * @param travellers
	 *           the travellers to set
	 */
	public void setTravellers(final String travellers)
	{
		this.travellers = travellers;
	}


}
