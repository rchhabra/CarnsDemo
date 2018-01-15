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

import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link BaggageAllowanceRS}
 */
public class BaggageAllowanceRSValidator extends NCDAbstractRequestValidator<BaggageAllowanceRS>
{
	/**
	 * Validate an BaggageAllowanceRS
	 *
	 * @param baggageAllowanceRS
	 * 		the BaggageAllowanceRS to validate
	 *
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateBaggageAllowanceRS(final BaggageAllowanceRS baggageAllowanceRS)
	{
		final ErrorsType errorsType = validateNDCRequest(baggageAllowanceRS);

		if (CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			baggageAllowanceRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
