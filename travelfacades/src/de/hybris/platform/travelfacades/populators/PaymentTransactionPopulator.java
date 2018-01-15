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
package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * Payment Transaction Populator
 */
public class PaymentTransactionPopulator implements Populator<PaymentTransactionModel, PaymentTransactionData>
{
	private Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> paymentTransactionEntryConverter;

	@Override
	public void populate(final PaymentTransactionModel source, final PaymentTransactionData target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setTransactionAmount(source.getPlannedAmount().doubleValue());

		final List<PaymentTransactionEntryData> paymentTransactionEntryDataList = new LinkedList<>();

		for (final PaymentTransactionEntryModel paymentTransactionEntryModel : source.getEntries())
		{
			final PaymentTransactionEntryData paymentTransactionEntryData = new PaymentTransactionEntryData();

			getPaymentTransactionEntryConverter().convert(paymentTransactionEntryModel, paymentTransactionEntryData);

			paymentTransactionEntryDataList.add(paymentTransactionEntryData);
		}

		target.setPaymentTransactionEntryData(paymentTransactionEntryDataList);
	}

	protected Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> getPaymentTransactionEntryConverter()
	{
		return paymentTransactionEntryConverter;
	}

	@Required
	public void setPaymentTransactionEntryConverter(
			final Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> paymentTransactionEntryConverter)
	{
		this.paymentTransactionEntryConverter = paymentTransactionEntryConverter;
	}
}
