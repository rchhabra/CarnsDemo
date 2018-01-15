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

package de.hybris.platform.travelfulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


/**
 * The type Move order to active action.
 */
public class MoveOrderToActiveAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(MoveOrderToActiveAction.class);

	@Override
	public void executeAction(final OrderProcessModel orderProcess) throws RetryLaterException, Exception
	{
		final OrderModel order = orderProcess.getOrder();
		LOG.info("Setting Order Status to Active for order : " + order.getCode());
		setOrderStatus(order, OrderStatus.ACTIVE);
	}


}
