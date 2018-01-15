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

import de.hybris.platform.ondemandcommon.backoffice.helpers.OrderInfo;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class TravelOrderInfo extends OrderInfo
{
	private List<TravelOrderEntryInfo> travelOrderEntryList = new ArrayList();

	public List<TravelOrderEntryInfo> getTravelOrderEntryList()
	{
		return this.travelOrderEntryList;
	}

	public void setTravelOrderEntryList(final List<TravelOrderEntryInfo> travelOrderEntryList)
	{
		this.travelOrderEntryList = travelOrderEntryList;
	}

}
