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
package de.hybris.platform.ndcfulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * The type Remove pay later entry action.
 */
public class RemovePayLaterEntryAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(RemovePayLaterEntryAction.class);

	@Override
	public void executeAction(final OrderProcessModel process) throws Exception
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing Pay Later transaction entries.");
		}

		removePayLaterTransaction(process.getOrder());

		setOrderStatus(process.getOrder(), OrderStatus.ORDER_SPLIT);
	}


	/**
	 * Remove pay later transaction.
	 *
	 * @param order
	 * 		the order
	 */
	protected void removePayLaterTransaction(final OrderModel order)
	{
		final List<PaymentTransactionModel> paymentTransactions = new LinkedList<>(order.getPaymentTransactions());
		final List<PaymentTransactionModel> toRemove = new LinkedList<>();

		for(PaymentTransactionModel paymentTransactionModel : paymentTransactions)
		{
			for(PaymentTransactionEntryModel paymentTransactionEntryModel : paymentTransactionModel.getEntries())
			{
				if(PaymentTransactionType.PAY_LATER.equals(paymentTransactionEntryModel.getType()))
				{
					toRemove.add(paymentTransactionModel);
				}
			}
		}

		paymentTransactions.removeAll(toRemove);

		order.setPaymentTransactions(paymentTransactions);
	}
}
