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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for
 * populating the {@link List} of {@link PricedItineraryData} on {@link FareSelectionData}
 */
public class ItineraryHandler implements FareSearchHandler
{

	private static final Logger LOGGER = Logger.getLogger(ItineraryHandler.class);

	/**
	 * Method takes {@link List} of {@link ScheduledRouteData} and generates a {@link List} of
	 * {@link PricedItineraryData} for each {@link ScheduledRouteData}. The method does not require the
	 * {@link FareSearchRequestData} object and so null reference can be used instead.
	 * 
	 * @param scheduledRoutes
	 *           the {@link List} of {@link ScheduledRouteData} object which will be used to create a {@link List} of
	 *           {@link PricedItineraryData}.
	 * @param fareSearchRequestData
	 *           not required for this populator.
	 * @param fareSelectionData
	 *           the {@link FareSearchRequestData} object where the {@link List} of {@link PricedItineraryData} will be
	 *           set.
	 * 
	 */
	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{

		fareSelectionData.setPricedItineraries(
				populateItineraryInformations(scheduledRoutes, fareSelectionData.getPricedItineraries(), fareSearchRequestData));
	}

	/**
	 * Method takes a list of Scheduled Routes and creates a list of Priced Itineraries.
	 *
	 * @param scheduledRoutes
	 * 		the scheduled routes
	 * @param pricedItineraries
	 * 		the priced itineraries
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @return List<PricedItineraryData> list
	 */
	protected List<PricedItineraryData> populateItineraryInformations(final List<ScheduledRouteData> scheduledRoutes,
			final List<PricedItineraryData> pricedItineraries, final FareSearchRequestData fareSearchRequestData)
	{

		LOGGER.debug("Processing " + scheduledRoutes.size() + " Scheduled Routes");

		int pricedItineraryId = 0;

		for (final ScheduledRouteData scheduledRoute : scheduledRoutes)
		{
			pricedItineraries.add(createPricedItineraryData(fareSearchRequestData, pricedItineraryId, scheduledRoute));
			pricedItineraryId++;
		}

		LOGGER.debug("Created " + pricedItineraries.size() + " Priced Itineraries");

		return pricedItineraries;
	}

	/**
	 * Method sets the parameters on a new PricedItineraryData object and returns it
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param pricedItineraryId
	 * 		the priced itinerary id
	 * @param scheduledRoute
	 * 		the scheduled route
	 * @return PricedItineraryData priced itinerary data
	 */
	protected PricedItineraryData createPricedItineraryData(final FareSearchRequestData fareSearchRequestData,
			final int pricedItineraryId, final ScheduledRouteData scheduledRoute)
	{
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraryData.setId(pricedItineraryId);
		pricedItineraryData.setItinerary(createItineraryData(fareSearchRequestData, scheduledRoute));
		pricedItineraryData.setOriginDestinationRefNumber(scheduledRoute.getReferenceNumber());
		pricedItineraryData.setAvailable(true);

		return pricedItineraryData;
	}

	/**
	 * Method sets the parameters on a new ItineraryData object and returns it
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param scheduledRoute
	 * 		the scheduled route
	 * @return ItineraryData itinerary data
	 */
	protected ItineraryData createItineraryData(final FareSearchRequestData fareSearchRequestData,
			final ScheduledRouteData scheduledRoute)
	{
		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setRoute(scheduledRoute.getRoute());
		itineraryData.setOriginDestinationOptions(createOriginDestinationOptionData(scheduledRoute));
		itineraryData.setDuration(TransportOfferingUtils.calculateJourneyDuration(scheduledRoute.getTransportOfferings()));
		itineraryData.setTripType(fareSearchRequestData.getTripType());
		return itineraryData;
	}



	/**
	 * Method takes the Transport Offering from the Scheduled Route and sets it on a new instance of Origin Destination
	 * Option. The method then creates a list of Origin Destination Options and adds the newly created
	 * OriginDestinationOption to the list before return the list.
	 *
	 * @param scheduledRoute
	 * 		the scheduled route
	 * @return List<OriginDestinationOptionData> list
	 */
	protected List<OriginDestinationOptionData> createOriginDestinationOptionData(final ScheduledRouteData scheduledRoute)
	{
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<OriginDestinationOptionData>();

		final OriginDestinationOptionData originDestinationOptionData = new OriginDestinationOptionData();
		originDestinationOptionData.setTransportOfferings(scheduledRoute.getTransportOfferings());

		originDestinationOptions.add(originDestinationOptionData);

		return originDestinationOptions;
	}
}
