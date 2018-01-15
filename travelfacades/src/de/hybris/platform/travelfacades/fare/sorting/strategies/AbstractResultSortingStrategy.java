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
package de.hybris.platform.travelfacades.fare.sorting.strategies;

import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.travelservices.utils.TravelDateUtils;


/**
 * Abstract strategy to sort the FareSelectionData
 */
public abstract class AbstractResultSortingStrategy
{

	/**
	 * Method to sort the FareSelectionData
	 *
	 * @param fareSelectionData
	 * 		as the FareSelectionData to be sorted
	 */
	public abstract void sortFareSelectionData(final FareSelectionData fareSelectionData);


	/**
	 * Common method used to compare two PricedItineraryData based on the departureDate
	 *
	 * @param pIt1
	 * 		as the first PricedItineraryData
	 * @param pIt2
	 * 		as the second PricedItineraryData
	 * @return int if pIt1 is equals to pIt2 0 is returned, if pIt1 greater than pIt2 an int > 0 is returned, if pIt1         less
	 * than pIt2 an int < 0 is returned
	 */
	protected int comparePricedItineraryByDepartureDate(final PricedItineraryData pIt1, final PricedItineraryData pIt2)
	{
		final TransportOfferingData to1 = pIt1.getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0);
		final TransportOfferingData to2 = pIt2.getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0);

		return TravelDateUtils.getUtcZonedDateTime(to1.getDepartureTime(), to1.getDepartureTimeZoneId())
				.compareTo(TravelDateUtils.getUtcZonedDateTime(to2.getDepartureTime(), to2.getDepartureTimeZoneId()));
	}

}
