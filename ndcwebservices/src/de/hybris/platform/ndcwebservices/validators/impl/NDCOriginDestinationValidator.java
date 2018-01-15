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
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class for NDC Origin Destination Validator, having common functionality that validates origin and
 * destination element for NDC requests.
 */
public abstract class NDCOriginDestinationValidator<N> implements NDCRequestValidator<N>
{

	private ConfigurationService configurationService;

	/**
	 * @param originDestinationsSize
	 * @param errorsType
	 * @return
	 */
	protected boolean validateFlightNumber(final int originDestinationsSize, final ErrorsType errorsType)
	{
		if (originDestinationsSize < NdcwebservicesConstants.MIN_ORIGINIDESTINATION)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MIN_ORIGINDESTINATION_EXCEEDED));
			return false;
		}

		if (originDestinationsSize > NdcwebservicesConstants.MAX_ORIGINIDESTINATION)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_ORIGINDESTINATION_EXCEEDED));
			return false;
		}

		return true;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
