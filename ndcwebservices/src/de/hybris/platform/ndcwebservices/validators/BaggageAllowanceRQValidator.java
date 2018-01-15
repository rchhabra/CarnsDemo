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

import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link BaggageAllowanceRQ}
 */
public class BaggageAllowanceRQValidator extends NCDAbstractRequestValidator<BaggageAllowanceRQ>
{
	/**
	 * Validate an {@link BaggageAllowanceRQ} request using a list of validators defined in it's bean
	 *
	 * @param baggageAllowanceRQ
	 * 		the BaggageAllowanceRQ that needs to be validated
	 * @param baggageAllowanceRS
	 * 		the BaggageAllowanceRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateBaggageAllowanceRQ(final BaggageAllowanceRQ baggageAllowanceRQ,
			final BaggageAllowanceRS baggageAllowanceRS)
	{
		final ErrorsType errorType = validateNDCRequest(baggageAllowanceRQ);
		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			baggageAllowanceRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
