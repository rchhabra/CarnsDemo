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

import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link BaggageListRQ}
 */
public class BaggageListRQValidator extends NCDAbstractRequestValidator<BaggageListRQ>
{
	/**
	 * Validate an BaggageListRQ request using a list of validators defined in it's bean
	 *
	 * @param baggageListRQ
	 * 		the baggageListRQ that needs to be validated
	 * @param baggageListRS
	 * 		the baggageListRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateBaggageListRQ(final BaggageListRQ baggageListRQ, final BaggageListRS baggageListRS)
	{
		final ErrorsType errorType = validateNDCRequest(baggageListRQ);
		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			baggageListRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
