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

package de.hybris.platform.travelservices.event.listener;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.event.SubmitOrderEventListener;

import org.apache.log4j.Logger;


/**
 * Listener for {@link SubmitOrderEvent}.
 */
public class TravelSubmitOrderEventListener extends SubmitOrderEventListener
{
	private static final Logger LOG = Logger.getLogger(TravelSubmitOrderEventListener.class);

	@Override
	protected void onSiteEvent(final SubmitOrderEvent event)
	{
		final OrderModel order = event.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);

		// Try the store set on the Order first, then fallback to the session
		BaseStoreModel store = order.getStore();
		if (store == null)
		{
			store = getBaseStoreService().getCurrentBaseStore();
		}

		if (store == null)
		{
			LOG.warn("Unable to start fulfilment process for order [" + order.getCode()
					+ "]. Store not set on Order and no current base store defined in session.");
		}
		else
		{
			final String fulfilmentProcessDefinitionName = store.getSubmitOrderProcessCode();
			if (fulfilmentProcessDefinitionName == null || fulfilmentProcessDefinitionName.isEmpty())
			{
				LOG.warn("Unable to start fulfilment process for order [" + order.getCode() + "]. Store [" + store.getUid()
						+ "] has missing SubmitOrderProcessCode");
			}
			else
			{
				final String processCode = fulfilmentProcessDefinitionName + "-" + order.getCode() + "-" + System.currentTimeMillis();
				final OrderProcessModel businessProcessModel = getBusinessProcessService().createProcess(processCode,
						fulfilmentProcessDefinitionName);
				businessProcessModel.setOrder(order);
				businessProcessModel.setTravellers(event.getTravellers());
				businessProcessModel.setOriginDestinationRefNumber(event.getOriginDestinationRefNumber());

				getModelService().save(businessProcessModel);
				getBusinessProcessService().startProcess(businessProcessModel);
				if (LOG.isInfoEnabled())
				{
					LOG.info(String.format("Started the process %s", processCode));
				}
			}
		}
	}

}
