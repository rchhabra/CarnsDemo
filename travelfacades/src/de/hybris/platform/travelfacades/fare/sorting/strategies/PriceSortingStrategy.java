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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of {@link AbstractResultSortingStrategy} to sort the {@link FareSelectionData} based on the price
 */
public class PriceSortingStrategy extends AbstractResultSortingStrategy
{

	@Override
	public void sortFareSelectionData(final FareSelectionData fareSelectionData)
	{
		Collections.sort(fareSelectionData.getPricedItineraries(), (pIt1, pIt2) -> {

			final int compareResult = getMinimumPrice(pIt1).compareTo(getMinimumPrice(pIt2));
			return compareResult != 0 ? compareResult : comparePricedItineraryByDepartureDate(pIt1, pIt2);

		});
	}

	/**
	 * Method to get the minimumPrice for a specific pricedItineraryData. The minimumPrice for a pricedItineraryData
	 * corresponds to the price of the cheapest its bundles.
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @return the minumumPrice for the pricedItinerary
	 */
	protected BigDecimal getMinimumPrice(final PricedItineraryData pricedItinerary)
	{
		final List<BigDecimal> prices = pricedItinerary.getItineraryPricingInfos().stream().filter(item -> item.isAvailable())
				.map(item -> item.getTotalFare().getTotalPrice().getValue()).collect(Collectors.toList());
		final Optional<BigDecimal> opt = prices.stream().min((p1, p2) -> p1.compareTo(p2));
		return opt.isPresent() ? opt.get() : BigDecimal.valueOf(Double.MAX_VALUE);
	}

}
