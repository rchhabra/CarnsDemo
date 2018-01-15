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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.List;


/**
 * If is the check-in journey, starts the check-in-process
 */
public class StartCheckInProcessAction extends AbstractProceduralAction<OrderProcessModel>
{
	private BusinessProcessService businessProcessService;

	@Override
	public void executeAction(final OrderProcessModel orderProcess) throws RetryLaterException, Exception
	{

		final List<String> travellers = orderProcess.getTravellers();

		if (!travellers.isEmpty())
		{
			final OrderModel orderModel = orderProcess.getOrder();

			final CheckInProcessModel checkInProcessModel = (CheckInProcessModel) getBusinessProcessService()
					.createProcess("check-in-process-" + orderModel.getCode() + "-" + System.currentTimeMillis(), "check-in-process");
			checkInProcessModel.setOrder(orderModel);
			checkInProcessModel.setOriginDestinationRefNumber(orderProcess.getOriginDestinationRefNumber());
			checkInProcessModel.setTravellers(travellers);

			getModelService().save(checkInProcessModel);
			getBusinessProcessService().startProcess(checkInProcessModel);
		}

	}

	/**
	 * @return the businessProcessService
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
