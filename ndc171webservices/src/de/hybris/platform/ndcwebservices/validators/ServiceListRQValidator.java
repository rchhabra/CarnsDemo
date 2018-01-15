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
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;

import org.apache.commons.collections.CollectionUtils;



/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link ServiceListRQ}
 */
public class ServiceListRQValidator extends NCDAbstractRequestValidator<ServiceListRQ>
{
	/**
	 * Validate an ServiceListRQ request using a list of validators defined in it's bean
	 *
	 * @param serviceListRQ
	 * 		the ServiceListRQ that needs to be validated
	 * @param serviceListRS
	 * 		the ServiceListRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateServiceListRQ(final ServiceListRQ serviceListRQ, final ServiceListRS serviceListRS)
	{
		final ErrorsType errorType = validateNDCRequest(serviceListRQ);

		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			serviceListRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
