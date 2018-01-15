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

import de.hybris.platform.commercefacades.travel.order.PaymentTransactionEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;


/**
 * Payment Transaction Entry Populator
 */
public class PaymentTransactionEntryPopulator implements Populator<PaymentTransactionEntryModel, PaymentTransactionEntryData>
{
	@Override
	public void populate(final PaymentTransactionEntryModel source,
			final PaymentTransactionEntryData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setAmount(source.getAmount());
		target.setType(source.getType());
		target.setTransactionStatus(source.getTransactionStatus());

	}
}
