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

import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link BaggageChargesRQ}
 */
public class BaggageChargesRQValidator extends NCDAbstractRequestValidator<BaggageChargesRQ>
{
	/**
	 * Validate an {@link BaggageChargesRQ} request using a list of validators defined in it's bean
	 *
	 * @param baggageChargesRQ
	 * 		the BaggageChargesRQ that needs to be validated
	 * @param baggageChargesRS
	 * 		the BaggageChargesRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateBaggageChargesRQ(final BaggageChargesRQ baggageChargesRQ, final BaggageChargesRS baggageChargesRS)
	{
		final ErrorsType errorType = validateNDCRequest(baggageChargesRQ);
		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			baggageChargesRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
