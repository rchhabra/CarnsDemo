/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.travelfulfilmentprocess.actions.b2b;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;


/**
 * The type Check order entry cost centers active.
 */
public class CheckOrderEntryCostCentersActive extends AbstractSimpleB2BApproveOrderDecisionAction
{
	private static final Logger LOG = Logger.getLogger(CheckOrderEntryCostCentersActive.class);

	/*
	 * Returns Transition.NOK if the order has any entries with inactive cost centers otherwise returns Transition.OK
	 */
	@Override
	public AbstractSimpleDecisionAction.Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel processedOrder = null;
		AbstractSimpleDecisionAction.Transition transition = AbstractSimpleDecisionAction.Transition.NOK;
		try
		{
			processedOrder = process.getOrder();

			if (CollectionUtils.isNotEmpty(getExpiredCostCenterEntries(processedOrder)))
			{
				transition = AbstractSimpleDecisionAction.Transition.NOK;
				setOrderStatus(processedOrder, OrderStatus.B2B_PROCESSING_ERROR);

				if (LOG.isInfoEnabled())
				{
					LOG.info(String
							.format(
									"Replenishment order has entries with inactive cost centers.  Order code [%s] failed from replenishment code [%s]",
									processedOrder.getCode(), processedOrder.getSchedulingCronJob().getCode()));
				}
			}
			else
			{
				transition = AbstractSimpleDecisionAction.Transition.OK;
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			this.handleError(processedOrder, e);

		}
		return transition;
	}

	/**
	 * Gets expired cost center entries.
	 *
	 * @param cart
	 * 		the cart
	 * @return the expired cost center entries
	 */
	protected Collection<AbstractOrderEntryModel> getExpiredCostCenterEntries(final OrderModel cart)
	{
		return CollectionUtils.select(cart.getEntries(), object -> {
            final B2BCostCenterModel costCenter = ((AbstractOrderEntryModel) object).getCostCenter();
            if (costCenter != null && BooleanUtils.isFalse(costCenter.getActive()))
            {
                return true;
            }
            return false;
        });

	}

	/**
	 * Handle error.
	 *
	 * @param order
	 * 		the order
	 * @param exception
	 * 		the exception
	 */
	protected void handleError(final OrderModel order, final Exception exception)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(exception.getMessage(), exception);
	}

}
