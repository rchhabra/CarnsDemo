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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.OrderSplittingService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Split order action.
 */
public class SplitOrderAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SplitOrderAction.class);

	private OrderSplittingService orderSplittingService;
	private BusinessProcessService businessProcessService;

	@Override
	public void executeAction(final OrderProcessModel process) throws Exception
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}

		// find the order's entries that are not already allocated to consignments
		final List<AbstractOrderEntryModel> entriesToSplit = new ArrayList<AbstractOrderEntryModel>();
		for (final AbstractOrderEntryModel entry : process.getOrder().getEntries())
		{
			if (entry.getConsignmentEntries() == null || entry.getConsignmentEntries().isEmpty())
			{
				entriesToSplit.add(entry);
			}
		}

		final List<ConsignmentModel> consignments = getOrderSplittingService().splitOrderForConsignment(process.getOrder(),
				entriesToSplit);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Splitting order into " + consignments.size() + " consignments.");
		}

		int index = 0;
		for (final ConsignmentModel consignment : consignments)
		{
			final ConsignmentProcessModel subProcess = getBusinessProcessService().<ConsignmentProcessModel> createProcess(

					process.getCode() + "_" + (++index), TravelfulfilmentprocessConstants.CONSIGNMENT_SUBPROCESS_NAME);

			subProcess.setParentProcess(process);
			subProcess.setConsignment(consignment);
			save(subProcess);

			getBusinessProcessService().startProcess(subProcess);

		}
		setOrderStatus(process.getOrder(), OrderStatus.ORDER_SPLIT);
	}

	/**
	 * Gets order splitting service.
	 *
	 * @return the order splitting service
	 */
	protected OrderSplittingService getOrderSplittingService()
	{
		return orderSplittingService;
	}

	/**
	 * Sets order splitting service.
	 *
	 * @param orderSplittingService
	 * 		the order splitting service
	 */
	@Required
	public void setOrderSplittingService(final OrderSplittingService orderSplittingService)
	{
		this.orderSplittingService = orderSplittingService;
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
	 * 		the business process service
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
