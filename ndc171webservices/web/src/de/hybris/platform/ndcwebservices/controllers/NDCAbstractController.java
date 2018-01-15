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

package de.hybris.platform.ndcwebservices.controllers;

import de.hybris.platform.ndcfacades.ndc.ErrorType;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;


/**
 * An abstract class for NDC request controllers to handle common operations.
 */
public abstract class NDCAbstractController
{
	@Resource(name = "configurationService")
	protected ConfigurationService configurationService;

	/**
	 * This method creates an instance of {@link ErrorType} for given parameters.
	 *
	 * @param errorsType
	 * @param shortText
	 */
	protected void addError(final ErrorsType errorsType, final String shortText)
	{
		final ErrorType errorType = new ErrorType();
		if (StringUtils.length(shortText) > NdcwebservicesConstants.SHORT_TEXT_MAX_CHARACTERS)
		{
			errorType.setShortText(shortText.substring(0, NdcwebservicesConstants.SHORT_TEXT_MAX_CHARACTERS - 1));
		}
		else
		{
			errorType.setShortText(shortText);
		}
		errorsType.getError().add(errorType);
	}
}
