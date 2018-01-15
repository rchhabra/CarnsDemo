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

import java.util.Collections;


/**
 * Implementation of {@link AbstractResultSortingStrategy} to sort the {@link FareSelectionData} based on the number of stops
 */
public class NumberOfStopsSortingStrategy extends AbstractResultSortingStrategy
{

	@Override
	public void sortFareSelectionData(final FareSelectionData fareSelectionData)
	{
		Collections.sort(fareSelectionData.getPricedItineraries(), (pIt1, pIt2) -> {
			final int pIt1Size = pIt1.getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().size();
			final int pIt2Size = pIt2.getItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().size();

			final int compareResult = Integer.compare(pIt1Size, pIt2Size);
			return compareResult != 0 ? compareResult : comparePricedItineraryByDepartureDate(pIt1, pIt2);
		});
	}

}
