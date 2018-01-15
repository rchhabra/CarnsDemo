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
package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import org.springframework.beans.factory.annotation.Required;

import java.util.LinkedList;
import java.util.List;


/**
 * This handler is responsible for populating the Payment Transaction in the {@link ReservationData}
 */
public class ReservationPaymentTransactionHandler implements ReservationHandler
{
	private Converter<PaymentTransactionModel, PaymentTransactionData> paymentTransactionConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		final List<PaymentTransactionData> paymentTransactionDataList = new LinkedList<>();
		for(final PaymentTransactionModel paymentTransactionModel : abstractOrderModel.getPaymentTransactions())
		{
			final PaymentTransactionData paymentTransactionData = new PaymentTransactionData();
			getPaymentTransactionConverter().convert(paymentTransactionModel, paymentTransactionData);
			paymentTransactionDataList.add(paymentTransactionData);
		}
		reservationData.setPaymentTransaction(paymentTransactionDataList);
	}

	protected Converter<PaymentTransactionModel, PaymentTransactionData> getPaymentTransactionConverter()
	{
		return paymentTransactionConverter;
	}

	@Required
	public void setPaymentTransactionConverter(
			final Converter<PaymentTransactionModel, PaymentTransactionData> paymentTransactionConverter)
	{
		this.paymentTransactionConverter = paymentTransactionConverter;
	}
}
