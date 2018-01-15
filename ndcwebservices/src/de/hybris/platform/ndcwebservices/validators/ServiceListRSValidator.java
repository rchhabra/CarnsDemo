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
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link ServiceListRS}
 */
public class ServiceListRSValidator extends NCDAbstractRequestValidator<ServiceListRS>
{
	/**
	 * Validate an ServiceListRS
	 *
	 * @param serviceListRS
	 * 		the serviceListRS to validate
	 *
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateServiceListRS(final ServiceListRS serviceListRS)
	{
		final ErrorsType errorsType = validateNDCRequest(serviceListRS);

		if (CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			serviceListRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
