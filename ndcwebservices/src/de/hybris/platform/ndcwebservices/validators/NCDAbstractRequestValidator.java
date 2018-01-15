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
package de.hybris.platform.ndcwebservices.validators;

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Interface class for NDC valiators
 */
public abstract class NCDAbstractRequestValidator<N>
{

	private ConfigurationService configurationService;

	protected List<NDCRequestValidator<N>> requestValidators = new LinkedList<>();

	/**
	 * Validate an NDC request using a list of validators
	 *
	 * @param ndcRequest
	 * 		the generic NDC request
	 *
	 * @return the error during occurred during the validation
	 */
	protected ErrorsType validateNDCRequest(final N ndcRequest)
	{
		final ErrorsType errorsType = new ErrorsType();

		for (final NDCRequestValidator<N> requestValidator : requestValidators)
		{
			requestValidator.validate(ndcRequest, errorsType);

			if (CollectionUtils.isNotEmpty(errorsType.getError()))
			{
				return errorsType;
			}
		}

		return errorsType;
	}

	/**
	 * Gets request validators.
	 *
	 * @return the request validators
	 */
	protected List<NDCRequestValidator<N>> getRequestValidators()
	{
		return requestValidators;
	}

	/**
	 * Sets request validators.
	 *
	 * @param requestValidators
	 * 		the request validators
	 */
	public void setRequestValidators(final List<NDCRequestValidator<N>> requestValidators)
	{
		this.requestValidators = requestValidators;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
