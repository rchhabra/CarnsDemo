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
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The TakePayment step captures the payment transaction.
 */
public class NDCTakePaymentAction extends AbstractNDCTakePaymentAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(NDCTakePaymentAction.class);

	private PaymentService paymentService;


	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		if (order.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			for (final PaymentTransactionModel txn : order.getPaymentTransactions())
			{
				if (txn.getEntries().stream().anyMatch(entry -> PaymentTransactionType.CAPTURE.equals(entry.getType())
						|| PaymentTransactionType.REFUND_STANDALONE.equals(entry.getType())
						|| PaymentTransactionType.PAY_LATER.equals(entry.getType())))
				{
					continue;
				}

				final PaymentTransactionEntryModel txnEntry = getPaymentService().capture(txn);

				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("The payment transaction has been captured. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					}
					setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
				}
				else
				{
					LOG.error("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
					return Transition.NOK;
				}
			}
		}
		return isPayLater(order) ? Transition.REMOVE_PAY_LATER : Transition.OK;
	}

	/**
	 * Is pay later boolean.
	 *
	 * @param order
	 * 		the order
	 *
	 * @return the boolean
	 */
	protected boolean isPayLater(final OrderModel order)
	{
		final List<PaymentTransactionModel> paymentTransactions = order.getPaymentTransactions();

		return paymentTransactions.stream()
				.flatMap(paymentTransactionModel -> paymentTransactionModel.getEntries().stream()).anyMatch(
						paymentTransactionEntryModels -> PaymentTransactionType.PAY_LATER
								.equals(paymentTransactionEntryModels.getType()));
	}

	/**
	 * Gets payment service.
	 *
	 * @return the payment service
	 */
	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	/**
	 * Sets payment service.
	 *
	 * @param paymentService
	 * 		the payment service
	 */
	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
