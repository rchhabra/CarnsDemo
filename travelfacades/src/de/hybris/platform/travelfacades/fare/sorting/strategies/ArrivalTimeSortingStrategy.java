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

package de.hybris.platform.travelfacades.fare.sorting.strategies;

import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Implementation of {@link AbstractResultSortingStrategy} to sort the {@link FareSelectionData} based on the
 * arrivalTime
 */
public class ArrivalTimeSortingStrategy extends AbstractResultSortingStrategy
{

	@Override
	public void sortFareSelectionData(final FareSelectionData fareSelectionData)
	{
		Collections.sort(fareSelectionData.getPricedItineraries(), (pIt1, pIt2) -> {

			final int compareResult = getArrivalTime(pIt1).compareTo(getArrivalTime(pIt2));
			return compareResult != 0 ? compareResult : comparePricedItineraryByDepartureDate(pIt1, pIt2);

		});
	}

	/**
	 * Method to get the arrivalTime of a specific pricedItineraryData. The arrivalTime of a pricedItineraryData
	 * corresponds to the arrivalTime of the latest of its transportOfferings.
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @return the arrivalTime of the latest of its transportOfferings
	 */
	protected Date getArrivalTime(final PricedItineraryData pricedItinerary)
	{
		final int originDestinationOptionsSize = pricedItinerary.getItinerary().getOriginDestinationOptions().size();
		final List<TransportOfferingData> transportOfferings = pricedItinerary.getItinerary().getOriginDestinationOptions()
				.get(originDestinationOptionsSize - 1).getTransportOfferings();
		return transportOfferings.get(transportOfferings.size() - 1).getArrivalTime();
	}

}
