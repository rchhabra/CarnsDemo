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
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.Objects;


/**
 * Concrete class to validate passenger data for {@link ServicePriceRQ}
 */
public class NDCServicePricePassengerTypeValidator extends NDCAbstractPassengerTypeValidator<ServicePriceRQ>
{

	@Override
	public void validate(final ServicePriceRQ servicePriceRQ, final ErrorsType errorsType)
	{
		if (Objects.isNull(servicePriceRQ.getDataLists()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.DATALISTS_UNAVAILABLE));
			return;
		}

		if (Objects.isNull(servicePriceRQ.getDataLists().getPassengerList()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_DATALISTS_PASSENGERLIST));
			return;
		}

		super.validate(servicePriceRQ.getDataLists().getPassengerList().getPassenger(), errorsType);
	}

}
