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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.enums.TravellerType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.strategies.TravellerSortStrategy} interface.
 */
public class DefaultTravellerSortStrategy implements TravellerSortStrategy
{
	private List<String> sortedPassengerTypes;

	@Override
	public List<TravellerData> applyStrategy(final List<TravellerData> travellers)
	{
		final List<TravellerData> sortedTravellers = new ArrayList<TravellerData>(travellers.size());

		final List<TravellerData> passengers = travellers.stream()
				.filter(traveller -> traveller.getTravellerType().equalsIgnoreCase(TravellerType.PASSENGER.getCode()))
				.collect(Collectors.toList());

		for (final String passengerType : getSortedPassengerTypes())
		{
			final List<TravellerData> matchingTravellers = passengers.stream()
					.filter(pass -> passengerType
							.equalsIgnoreCase(((PassengerInformationData) pass.getTravellerInfo()).getPassengerType().getCode()))
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(matchingTravellers))
			{
				sortedTravellers.addAll(matchingTravellers);
			}
		}

		final List<TravellerData> undefinedTravellerTypes = getUndefinedTravellers(travellers, passengers);

		if (CollectionUtils.isNotEmpty(undefinedTravellerTypes))
		{
			sortedTravellers.addAll(undefinedTravellerTypes);
		}

		return sortedTravellers;
	}

	@Override
	public List<PassengerTypeData> sortPassengerTypes(final List<PassengerTypeData> passengerTypes)
	{
		final List<PassengerTypeData> passengerTypesSorted = new ArrayList<PassengerTypeData>(passengerTypes.size());
		for (final String sortedType : getSortedPassengerTypes())
		{
			final List<PassengerTypeData> filteredPassengerTypes = passengerTypes.stream()
					.filter(type -> sortedType.equalsIgnoreCase(type.getCode())).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(filteredPassengerTypes))
			{
				passengerTypesSorted.addAll(filteredPassengerTypes);
			}
		}

		for (final PassengerTypeData passengerType : passengerTypes)
		{
			if (!getSortedPassengerTypes().contains(passengerType.getCode()))
			{
				passengerTypesSorted.add(passengerType);
			}
		}

		return passengerTypesSorted;
	}

	/**
	 * Checks if there are any Travellers which are not defined by configured list
	 *
	 * @param travellers
	 * 		list of all travellers
	 * @param passengers
	 * 		list of filtered passengers from travellers
	 * @return list of undefined travellers
	 */
	protected List<TravellerData> getUndefinedTravellers(final List<TravellerData> travellers,
			final List<TravellerData> passengers)
	{
		final List<TravellerData> undefinedTravellerTypes = new ArrayList<TravellerData>();
		undefinedTravellerTypes.addAll(travellers.stream()
				.filter(traveller -> !traveller.getTravellerType().equalsIgnoreCase(TravellerType.PASSENGER.getCode()))
				.collect(Collectors.toList()));

		for (final TravellerData trav : passengers)
		{
			if (!getSortedPassengerTypes()
					.contains(((PassengerInformationData) trav.getTravellerInfo()).getPassengerType().getCode()))
			{
				undefinedTravellerTypes.add(trav);
			}
		}
		return undefinedTravellerTypes;
	}

	/**
	 * Gets sorted passenger types.
	 *
	 * @return the sortedPassengerTypes
	 */
	protected List<String> getSortedPassengerTypes()
	{
		return sortedPassengerTypes;
	}

	/**
	 * Sets sorted passenger types.
	 *
	 * @param sortedPassengerTypes
	 * 		the sortedPassengerTypes to set
	 */
	public void setSortedPassengerTypes(final List<String> sortedPassengerTypes)
	{
		this.sortedPassengerTypes = sortedPassengerTypes;
	}

}
