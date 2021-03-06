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

package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;


/**
 * This class validates the ServiceListRQ based on the constraints defined in its xsd
 * @deprecated since version 4.0 use {@link NDCRQFormatValidator}
 */
@Deprecated
public class NDCServiceListRQFormatValidator extends NDCRQFormatValidator<ServiceListRQ>
{
	@Override
	public void validate(final ServiceListRQ serviceListRQ, final ErrorsType errorsType)
	{
		super.validate(serviceListRQ, errorsType);
	}
}
