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

package de.hybris.platform.travelfulfilmentprocess.strategy.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.List;


/**
 * Strategy Class to split and group the OrderEntryGroup based on Travellers
 */
public class SplitByTraveller implements SplittingStrategy
{

	private TravellerService travellerService;

	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		for (final OrderEntryGroup orderEntry : orderEntryGroup)
		{
			result.addAll(splitByTraveller(orderEntry));
		}
		return result;
	}

	/**
	 * Split by traveller list.
	 *
	 * @param orderEntryList
	 * 		the order entry list
	 * @return the list
	 */
	protected List<OrderEntryGroup> splitByTraveller(final OrderEntryGroup orderEntryList)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		final List<TravellerModel> currentPossibleTravellers = getTravellerService().getTravellers(orderEntryList);

		currentPossibleTravellers.forEach(currentTraveller -> {
			final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();
			orderEntryList.stream().filter(orderEntry -> orderEntry.getTravelOrderEntryInfo() != null).forEach(orderEntry -> {
				for (final TravellerModel traveller : orderEntry.getTravelOrderEntryInfo().getTravellers())
				{
					if (traveller.getLabel().equals(currentTraveller.getLabel()))
					{
						orderEntries.add(orderEntry);
						break;
					}
				}
			});

			final OrderEntryGroup tmpOrderEntryList = new OrderEntryGroup();
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.TRAVELLER, currentTraveller);
			tmpOrderEntryList.addAll(orderEntries);
			result.add(tmpOrderEntryList);
		});
		return result;
	}

	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		// Do nothing
	}

	/**
	 * Gets traveller service.
	 *
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 * 		the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

}
