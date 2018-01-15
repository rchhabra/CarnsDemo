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
package de.hybris.platform.travelfulfilmentprocess.warehouse;

import de.hybris.platform.commerceservices.enums.WarehouseConsignmentState;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.warehouse.Warehouse2ProcessAdapter;
import de.hybris.platform.warehouse.WarehouseConsignmentStatus;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default warehouse 2 process adapter.
 */
public class DefaultWarehouse2ProcessAdapter implements Warehouse2ProcessAdapter
{
	private Map<WarehouseConsignmentStatus, WarehouseConsignmentState> statusMap;
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Override
	public void receiveConsignmentStatus(final ConsignmentModel consignment, final WarehouseConsignmentStatus status)
	{
		for (final ConsignmentProcessModel process : consignment.getConsignmentProcesses())
		{
			final WarehouseConsignmentState state = getStatusMap().get(status);

			if (Objects.isNull(state))
			{
				throw new IllegalStateException("No mapping for WarehouseConsignmentStatus: " + status);
			}
			process.setWarehouseConsignmentState(state);
			getModelService().save(process);
			getBusinessProcessService().triggerEvent(process.getCode() + "_" + TravelfulfilmentprocessConstants.WAIT_FOR_WAREHOUSE);
		}
	}

	/**
	 * Gets status map.
	 *
	 * @return the status map
	 */
	protected Map<WarehouseConsignmentStatus, WarehouseConsignmentState> getStatusMap()
	{
		return statusMap;
	}

	/**
	 * Sets status map.
	 *
	 * @param statusMap
	 *           the status map
	 */
	@Required
	public void setStatusMap(final Map<WarehouseConsignmentStatus, WarehouseConsignmentState> statusMap)
	{
		this.statusMap = statusMap;
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
	 *           the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets business process service.
	 *
	 * @return the business process service
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * Sets business process service.
	 *
	 * @param businessProcessService
	 *           the business process service
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
