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

package de.hybris.platform.travelfacades.strategies;

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;

import java.util.List;


/**
 * Strategy responsible for sorting travellers in a configurable way
 */
public interface TravellerSortStrategy
{
	/**
	 * Applies the sorting strategy for Travellers based on configured ordered list
	 *
	 * @param travellers
	 * 		the travellers
	 * @return sorted list of travellers
	 */
	List<TravellerData> applyStrategy(List<TravellerData> travellers);

	/**
	 * Sorts passenger types based on configured ordered list
	 *
	 * @param passengerTypes
	 * 		the passenger types
	 * @return sorted list of passenger types
	 */
	List<PassengerTypeData> sortPassengerTypes(List<PassengerTypeData> passengerTypes);

}
