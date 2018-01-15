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
package de.hybris.platform.travelfulfilmentprocess.actions.order;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.apache.log4j.Logger;


/**
 * The type Subprocesses completed action.
 */
public class SubprocessesCompletedAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SubprocessesCompletedAction.class);

	private static final String PROCESS_MSG = "Process: ";

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());

		LOG.info(PROCESS_MSG + process.getCode() + " is checking for  " + process.getConsignmentProcesses().size() + " subprocess results");

		for (final ConsignmentProcessModel subProcess : process.getConsignmentProcesses())
		{
			if (!subProcess.isDone())
			{
				LOG.info(PROCESS_MSG + process.getCode() + " found subprocess " + subProcess.getCode() + " incomplete -> wait again!");
				return Transition.NOK;
			}
			LOG.info(PROCESS_MSG + process.getCode() + " found subprocess " + subProcess.getCode() + " complete ...");
		}
		LOG.info(PROCESS_MSG + process.getCode() + " found all subprocesses complete");
		return Transition.OK;
	}
}
