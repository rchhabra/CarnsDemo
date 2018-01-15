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
 *  
 */
package de.hybris.platform.travelfulfilmentprocess.actions.consignment;

import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.warehouse.Process2WarehouseAdapter;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Allow shipment action.
 */
public class AllowShipmentAction extends AbstractAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(AllowShipmentAction.class);

	private Process2WarehouseAdapter process2WarehouseAdapter;

	public enum Transition
	{
		DELIVERY, PICKUP, CANCEL, ERROR;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final ConsignmentProcessModel process)
	{
		final ConsignmentModel consignment = process.getConsignment();
		if (consignment != null)
		{
			try
			{
				// Check if the Order is Cancelled
				if (OrderStatus.CANCELLED.equals(consignment.getOrder().getStatus())
						|| OrderStatus.CANCELLING.equals(consignment.getOrder().getStatus()))
				{
					return Transition.CANCEL.toString();
				}
				else
				{
					getProcess2WarehouseAdapter().shipConsignment(process.getConsignment());
					return getTransitionForConsignment(consignment);
				}
			}
			catch (final Exception e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(e);
				}
				return Transition.ERROR.toString();
			}
		}
		return Transition.ERROR.toString();
	}

	/**
	 * Gets transition for consignment.
	 *
	 * @param consignment
	 * 		the consignment
	 * @return the transition for consignment
	 */
	protected String getTransitionForConsignment(final ConsignmentModel consignment) {
		if (consignment.getDeliveryMode() instanceof PickUpDeliveryModeModel)
		{
			return Transition.PICKUP.toString();
		}
		else
		{
			return Transition.DELIVERY.toString();
		}
	}

	/**
	 * Gets process 2 warehouse adapter.
	 *
	 * @return the process 2 warehouse adapter
	 */
	protected Process2WarehouseAdapter getProcess2WarehouseAdapter()
	{
		return process2WarehouseAdapter;
	}

	/**
	 * Sets process 2 warehouse adapter.
	 *
	 * @param process2WarehouseAdapter
	 * 		the process 2 warehouse adapter
	 */
	@Required
	public void setProcess2WarehouseAdapter(final Process2WarehouseAdapter process2WarehouseAdapter)
	{
		this.process2WarehouseAdapter = process2WarehouseAdapter;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}
}
