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
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments.Payment;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * NDC {@link OrderChangeRQ} {@link OrderPaymentFormType} Validator
 * This validation is skipped in case no payment information is provided.
 */
public class NDCOrderChangePaymentValidator extends NDCAbstractPaymentValidator<OrderChangeRQ>
{
	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		/* skip validation if no payment information is provided */
		if (Objects.isNull(orderChangeRQ.getQuery().getPayments()) || CollectionUtils
				.isEmpty(orderChangeRQ.getQuery().getPayments().getPayment()))
		{
			return;
		}

		final Optional<Payment> orderPaymentFormType = getCardPayment(orderChangeRQ.getQuery().getPayments().getPayment());

		if(!orderPaymentFormType.isPresent())
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_CARD_PAYMENT));
			return;
		}

		super.validate(orderPaymentFormType.get(), errorsType);
	}

	/**
	 * Checks is there is any {@link PaymentCardType} in the list of {@link Payment}
	 *
	 * @param payments
	 * 		the payments
	 *
	 * @return card payment
	 */
	protected Optional<Payment> getCardPayment(final List<Payment> payments)
	{
		return payments.stream().filter(orderPayment -> Objects.nonNull(orderPayment.getMethod().getPaymentCard())).findFirst();
	}

}
