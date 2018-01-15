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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * NDC {@link OrderCreateRQ} {@link OrderPaymentFormType} Validator This validation is skipped in case no payment information is
 * provided and the booking reference is not present. A PAY_LATER transaction will be attached to the order.
 */
public class NDCOrderCreatePaymentValidator extends NDCAbstractPaymentValidator<OrderCreateRQ>
{

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		/* skip validation if no payment information is provided and the booking reference is not present */
		if (Objects.nonNull(orderCreateRQ.getQuery().getBookingReferences()) && CollectionUtils
				.isNotEmpty(orderCreateRQ.getQuery().getBookingReferences().getBookingReference()))
		{
			if (Objects.isNull(orderCreateRQ.getQuery().getPayments()) || CollectionUtils
					.isEmpty(orderCreateRQ.getQuery().getPayments().getPayment()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
				return;
			}
		}
		else
		{
			if (Objects.isNull(orderCreateRQ.getQuery().getPayments()) || CollectionUtils
					.isEmpty(orderCreateRQ.getQuery().getPayments().getPayment()))
			{
				return;
			}
		}

		final Optional<OrderPaymentFormType> orderPaymentFormType = getCardPayment(
				orderCreateRQ.getQuery().getPayments().getPayment());

		if (!orderPaymentFormType.isPresent())
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_CARD_PAYMENT));
			return;
		}

		super.validate(orderPaymentFormType.get(), errorsType);
	}

	/**
	 * Checks is there is any {@link PaymentCardType} in the list of {@link OrderPaymentFormType}
	 *
	 * @param payments
	 * 		the payments
	 * @return card payment
	 */
	protected Optional<OrderPaymentFormType> getCardPayment(final List<OrderPaymentFormType> payments)
	{
		return payments.stream().filter(orderPayment -> Objects.nonNull(orderPayment.getMethod().getPaymentCard())).findFirst();
	}

}
