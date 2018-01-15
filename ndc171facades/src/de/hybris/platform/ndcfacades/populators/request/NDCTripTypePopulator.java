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
package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;


/**
 * The NDC Trip Type Populator for {@link AirShoppingRQ}
 * Set the TripType based on the number of OriginDestinations objects
 */
public class NDCTripTypePopulator implements Populator<AirShoppingRQ,FareSearchRequestData>
{

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		final int flightCount = airShoppingRQ.getCoreQuery().getOriginDestinations().getOriginDestination().size();

		setTripType(flightCount, fareSearchRequestData);
	}

	/**
	 * Sets trip type.
	 *
	 * @param flightCount
	 * 		the flight count
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 */
	protected void setTripType(final int flightCount, final FareSearchRequestData fareSearchRequestData)
	{
		if(flightCount == 1)
		{
			fareSearchRequestData.setTripType(TripType.SINGLE);
		}

		if(flightCount == 2)
		{
			fareSearchRequestData.setTripType(TripType.RETURN);
		}
	}

}
