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
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;

import java.util.Collections;


/**
 * Implementation of {@link AbstractResultSortingStrategy} to sort the {@link FareSelectionData} based on the travelTime
 */
public class TravelTimeSortingStrategy extends AbstractResultSortingStrategy
{

	@Override
	public void sortFareSelectionData(final FareSelectionData fareSelectionData)
	{
		Collections.sort(fareSelectionData.getPricedItineraries(), (pIt1, pIt2) -> {
			final Long duration1 = TransportOfferingUtils.getDuration(pIt1.getItinerary().getDuration());
			final Long duration2 = TransportOfferingUtils.getDuration(pIt2.getItinerary().getDuration());

			final int compareResult = duration1.compareTo(duration2);
			return compareResult != 0 ? compareResult : comparePricedItineraryByDepartureDate(pIt1, pIt2);
		});
	}

}
