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

import de.hybris.platform.ndcfacades.ndc.ErrorType;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import org.apache.commons.lang.StringUtils;


/**
 * Interface for the NDC Request Validator
 */
public interface NDCRequestValidator<N>
{
	/**
	 * Validate a generic ndcRequest through a list of validators
	 *
	 * @param ndcRequest
	 * 		the NDC Request object that needs to be validated
	 * @param errorsType
	 * 		the errors in the validation process
	 */
	void validate(N ndcRequest, ErrorsType errorsType);

	/**
	 * Creates an error object base on the information provided by the validator
	 *
	 * @param errorsType
	 * 		the object containing a list of errors
	 * @param shortText
	 * 		the error short description
	 */
	default void addError(final ErrorsType errorsType, final String shortText)
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

	/**
	 * Gets sanitize error message.
	 *
	 * @param error
	 * 		the error
	 *
	 * @return the sanitize error message
	 */
	default String getSanitizeErrorMessage(final String error)
	{
		return error.substring(error.indexOf(':') + 1);
	}
}
