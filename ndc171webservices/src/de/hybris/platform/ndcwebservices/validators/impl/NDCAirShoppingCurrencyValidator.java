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

import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.MessageParamsBaseType.CurrCodes.FiledInCurrency;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * The concrete class to validates currency for {@link AirShoppingRQ}
 */
public class NDCAirShoppingCurrencyValidator extends NDCOffersCurrencyValidator<AirShoppingRQ>
{
	@Override
	public void validate(final AirShoppingRQ airShoppingRQ, final ErrorsType errorsType)
	{
		if (Objects.nonNull(airShoppingRQ.getParameters()) && Objects.nonNull(airShoppingRQ.getParameters().getCurrCodes())
				&& CollectionUtils.isNotEmpty(airShoppingRQ.getParameters().getCurrCodes().getFiledInCurrency()))
		{
			final FiledInCurrency filledInCurrency = airShoppingRQ.getParameters().getCurrCodes().getFiledInCurrency().stream()
					.findFirst().get();
			if (Objects.nonNull(filledInCurrency.getCurrCode()))
			{
				isValidIsoCode(filledInCurrency.getCurrCode().getValue(), errorsType);
				return;
			}
		}
		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_OFFERS_CURRENCY));
	}
}
