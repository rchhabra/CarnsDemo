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

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import de.hybris.platform.b2b.process.approval.actions.InformOfOrderRejection;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;


/**
 * Checks for order in process.
 */
public class B2BAcceleratorInformOfOrderApprovalRejection extends InformOfOrderRejection
{
	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = Logger.getLogger(InformOfOrderRejection.class);

	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		try
		{
			final OrderModel order = process.getOrder();
			Assert.notNull(order, String.format("Order of BusinessProcess %s should have be set for accelerator", process));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Process for accelerator: %s in step %s order: %s user: %s ", process.getCode(), getClass(),
						order.getUnit(), order.getUser().getUid()));
			}
		}
		catch (final IllegalArgumentException e)
		{
			LOG.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
