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
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;

import org.apache.commons.collections.CollectionUtils;



/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link ServicePriceRQ}
 */
public class ServicePriceRQValidator extends NCDAbstractRequestValidator<ServicePriceRQ>
{
	/**
	 * Validate an ServicePriceRQ request using a list of validators defined in it's bean
	 *
	 * @param servicePriceRQ
	 * 		the ServicePriceRQ that needs to be validated
	 * @param servicePriceRS
	 * 		the ServicePriceRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateServicePriceRQ(final ServicePriceRQ servicePriceRQ, final ServicePriceRS servicePriceRS)
	{
		final ErrorsType errorType = validateNDCRequest(servicePriceRQ);

		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			servicePriceRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
