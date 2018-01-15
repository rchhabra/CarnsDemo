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
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.ArrayList;
import java.util.List;


/**
 * Strategy Class to separate out the OrderEntryGroup based on Transport Offering
 */
public class SplitByTransportOffering implements SplittingStrategy
{
	private TransportOfferingService transportOfferingService;

	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		for (final OrderEntryGroup orderEntry : orderEntryGroup)
		{
			result.addAll(splitByTransportOffering(orderEntry));
		}
		return result;
	}

	/**
	 * Split by transport offering list.
	 *
	 * @param orderEntryList
	 * 		the order entry list
	 * @return the list
	 */
	protected List<OrderEntryGroup> splitByTransportOffering(final OrderEntryGroup orderEntryList)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		final List<TransportOfferingModel> currentPossibleTransportOfferings = getTransportOfferingService()
				.getTransportOfferingsFromOrderEntries(orderEntryList);

		currentPossibleTransportOfferings.forEach(currentTransportOffering -> {
			final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();
			orderEntryList.stream().filter(orderEntry -> orderEntry.getTravelOrderEntryInfo() != null).forEach(orderEntry -> {
				for (final TransportOfferingModel transportOffering : orderEntry.getTravelOrderEntryInfo().getTransportOfferings())
				{
					if (transportOffering.getCode().equals(currentTransportOffering.getCode()))
					{
						orderEntries.add(orderEntry);
						break;
					}
				}
			});

			final OrderEntryGroup tmpOrderEntryList = new OrderEntryGroup();
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.TRAVELLER,
					orderEntryList.getParameter(TravelfulfilmentprocessConstants.TRAVELLER));
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.TRANSPORT_OFFERING, currentTransportOffering);
			tmpOrderEntryList.addAll(orderEntries);
			result.add(tmpOrderEntryList);
		});
		return result;
	}

	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		final TransportOfferingModel transportOffering = (TransportOfferingModel) group
				.getParameter(TravelfulfilmentprocessConstants.TRANSPORT_OFFERING);
		final TravellerModel traveller = (TravellerModel) group.getParameter(TravelfulfilmentprocessConstants.TRAVELLER);
		createdOne.setWarehouse(transportOffering);
		createdOne.setCode(transportOffering.getCode() + "-" + traveller.getLabel());
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transportOfferingService to set
	 */
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

}
