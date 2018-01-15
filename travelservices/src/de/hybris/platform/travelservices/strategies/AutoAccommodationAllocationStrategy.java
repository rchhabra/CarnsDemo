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

package de.hybris.platform.travelservices.strategies;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;


/**
 * Strategy to auto allocate seat to traveller if not already selected.
 */
public interface AutoAccommodationAllocationStrategy
{
	/**
	 * This strategy follows one rule that is to allocate the first available seat.
	 * Accommodation types are represented in the following hierarchy
	 * Decks -> Cabin -> Rows -> Columns -> Seats.
	 * So, finding the first seat available, resembles the traditional DFS(Depth first search) algorithm.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @param legNumber
	 * 		the leg number
	 * @param travellerReferences
	 * 		the traveller references
	 */
	void autoAllocateSeat(AbstractOrderModel abstractOrderModel, Integer legNumber, List<String> travellerReferences);
}
