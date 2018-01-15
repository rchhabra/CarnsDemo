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
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * The concrete class to validates currency for {@link ServiceListRQ}
 */
public class NDCServiceListCurrencyValidator extends NDCOffersCurrencyValidator<ServiceListRQ>
{
	@Override
	public void validate(final ServiceListRQ serviceListRQ, final ErrorsType errorsType)
	{
		if (Objects.nonNull(serviceListRQ.getParameters()) && Objects.nonNull(serviceListRQ.getParameters().getCurrCodes())
				&& CollectionUtils.isNotEmpty(serviceListRQ.getParameters().getCurrCodes().getCurrCode()))
		{
			isValidIsoCode(serviceListRQ.getParameters().getCurrCodes().getCurrCode().get(0).getValue(), errorsType);
			return;
		}
		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_OFFERS_CURRENCY));
	}
}
