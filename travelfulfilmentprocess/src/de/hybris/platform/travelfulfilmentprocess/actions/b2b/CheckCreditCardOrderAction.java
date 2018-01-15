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

import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


/**
 * The type Check credit card order action.
 */
public class CheckCreditCardOrderAction extends AbstractSimpleB2BApproveOrderDecisionAction
{
	private static final Logger LOG = Logger.getLogger(CheckCreditCardOrderAction.class);

	/**
	 * Returns Transition.NOK if the order has any entries with inactive cost centers otherwise returns Transition.OK
	 */
	@Override
	public AbstractSimpleDecisionAction.Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		AbstractSimpleDecisionAction.Transition transition = AbstractSimpleDecisionAction.Transition.NOK;
		try
		{
			order = process.getOrder();
			final PaymentInfoModel paymentInfo = order.getPaymentInfo();

			if (CheckoutPaymentType.CARD.equals(order.getPaymentType()) && paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				// this is a credit card payment, approval is not required
				transition = AbstractSimpleDecisionAction.Transition.OK;
			}
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
		}
		return transition;
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
