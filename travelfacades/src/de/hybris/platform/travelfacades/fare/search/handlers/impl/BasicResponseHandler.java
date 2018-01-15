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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible to initialize
 * the list of {@link PricedItineraryData} on the {@link FareSelectionData}
 */
public class BasicResponseHandler implements FareSearchHandler
{

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		final List<PricedItineraryData> pricedItineraries = new ArrayList<>(scheduledRoutes.size());
		fareSelectionData.setPricedItineraries(pricedItineraries);
	}

}
