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
package de.hybris.platform.travelfacades.fare.search.handlers;


import java.util.List;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;


/**
 * Interface for handlers classes that will be updating the {@link FareSelectionData} based on the list of
 * {@link ScheduledRouteData} and {@link FareSearchRequestData}
 */
public interface FareSearchHandler
{

	/**
	 * Handle method.
	 *
	 * @param scheduledRoutes
	 * 		the scheduled routes
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	void handle(List<ScheduledRouteData> scheduledRoutes, FareSearchRequestData fareSearchRequestData, FareSelectionData
			fareSelectionData);
}
