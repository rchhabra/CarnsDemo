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

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.warehouse.Process2WarehouseAdapter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Send consignment to warehouse action.
 */
public class SendConsignmentToWarehouseAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendConsignmentToWarehouseAction.class);

	private Process2WarehouseAdapter process2WarehouseAdapter;

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		getProcess2WarehouseAdapter().prepareConsignment(process.getConsignment());
		process.setWaitingForConsignment(true);
		getModelService().save(process);
		LOG.info("Setting waitForConsignment to true");
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

	/**
	 * Gets process 2 warehouse adapter.
	 *
	 * @return the process 2 warehouse adapter
	 */
	protected Process2WarehouseAdapter getProcess2WarehouseAdapter()
	{
		return process2WarehouseAdapter;
	}
}
