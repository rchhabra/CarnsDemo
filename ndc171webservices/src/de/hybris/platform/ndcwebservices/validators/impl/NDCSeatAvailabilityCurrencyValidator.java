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
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

//TODO This (or part of this class) has been commented out due to 17.1 upgrade and needs to be revisited
/**
 * The concrete class to validates currency for {@link SeatAvailabilityRQ}
 */
public class NDCSeatAvailabilityCurrencyValidator extends NDCOffersCurrencyValidator<SeatAvailabilityRQ>
{
	@Override
	public void validate(final SeatAvailabilityRQ seatAvailabilityRQ, final ErrorsType errorsType)
	{
		/*if (Objects.nonNull(seatAvailabilityRQ.getParameters())
				&& Objects.nonNull(seatAvailabilityRQ.getParameters().getCurrCodes())
				&& CollectionUtils.isNotEmpty(seatAvailabilityRQ.getParameters().getCurrCodes().getCurrCode()))
		{
			isValidIsoCode(seatAvailabilityRQ.getParameters().getCurrCodes().getCurrCode().get(0).getValue(), errorsType);
			return;
		}
		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_OFFERS_CURRENCY));*/
	}
}
