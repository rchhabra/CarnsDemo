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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.strategies.ConsignmentsStatusUpdateStrategy;

import java.util.Set;


/**
 * Strategy to override the consignments status in new order.
 */
public class ConsignmentsStatusOverrideStrategy implements ConsignmentsStatusUpdateStrategy
{
	private ModelService modelService;

	@Override
	public void updateConsignmentsStatus(final OrderModel newOrder, final OrderModel originalOrder)
	{
		final Set<ConsignmentModel> newOrderConsignments = newOrder.getConsignments();

		// For all checked-in consignments from original order, copy the same status to new order
		for (final ConsignmentModel consignment : newOrderConsignments)
		{
			final String consignmentCode = consignment.getCode();
			for (final ConsignmentModel origConsignment : originalOrder.getConsignments())
			{
				if (origConsignment.getCode().equals(consignmentCode) && origConsignment.getStatus() == ConsignmentStatus.CHECKED_IN)
				{
					consignment.setStatus(origConsignment.getStatus());
					getModelService().save(consignment);
				}
			}
		}

		// For non checked-in consignments, update the consignment status
		for (final ConsignmentModel consignment : newOrderConsignments)
		{
			if (consignment.getStatus() == ConsignmentStatus.CHECKED_IN)
			{
				continue;
			}
			boolean allEntriesInActive = true;
			for (final ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries())
			{
				if (consignmentEntry.getOrderEntry().getActive())
				{
					allEntriesInActive = false;
					break;
				}
			}
			if (allEntriesInActive)
			{
				consignment.setStatus(ConsignmentStatus.CANCELLED);
				getModelService().save(consignment);
			}
		}
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
