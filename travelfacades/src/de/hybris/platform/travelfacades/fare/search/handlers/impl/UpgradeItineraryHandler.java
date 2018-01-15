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
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.List;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for populating the
 * {@link List} of {@link PricedItineraryData} on {@link FareSelectionData}
 */
public class UpgradeItineraryHandler implements FareSearchHandler
{

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{

		fareSelectionData
				.setPricedItineraries(populateItineraryInformations(fareSelectionData.getPricedItineraries(), fareSearchRequestData));
	}

	/**
	 * Method takes FareSearchRequestData and creates a list of Priced Itineraries.
	 *
	 * @param pricedItineraries
	 *           the priced itineraries
	 * @param fareSearchRequestData
	 *           the fare search request data
	 * @return List<PricedItineraryData> list
	 */
	protected List<PricedItineraryData> populateItineraryInformations(final List<PricedItineraryData> pricedItineraries,
			final FareSearchRequestData fareSearchRequestData)
	{

		int pricedItineraryId = 0;

		for (final OriginDestinationInfoData originDestinationInfoData : fareSearchRequestData.getOriginDestinationInfo())
		{
			pricedItineraries.add(createPricedItineraryData(pricedItineraryId, originDestinationInfoData));
			pricedItineraryId++;
		}

		return pricedItineraries;
	}

	/**
	 * Method sets the parameters on a new PricedItineraryData object and returns it
	 *
	 * @param pricedItineraryId
	 *           the priced itinerary id
	 * @param originDestinationInfoData
	 *           the originDestinationInfoData
	 *
	 * @return PricedItineraryData priced itinerary data
	 */
	protected PricedItineraryData createPricedItineraryData(final int pricedItineraryId,
			final OriginDestinationInfoData originDestinationInfoData)
	{
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraryData.setId(pricedItineraryId);
		pricedItineraryData
				.setItinerary(originDestinationInfoData.getItinerary());
		pricedItineraryData.setOriginDestinationRefNumber(originDestinationInfoData.getReferenceNumber());
		pricedItineraryData.setAvailable(true);

		return pricedItineraryData;
	}
}
