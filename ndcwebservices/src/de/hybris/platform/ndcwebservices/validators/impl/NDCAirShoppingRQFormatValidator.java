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

import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;


/**
 * NDC AirShoppingRQ Format Validator validate an AirShoppingRQ based on the constraints defined in its xsd
 * @deprecated since version 4.0 use {@link NDCRQFormatValidator}
 */
@Deprecated
public class NDCAirShoppingRQFormatValidator extends NDCRQFormatValidator<AirShoppingRQ>
{
	@Override
	public void validate(final AirShoppingRQ airShoppingRQ, final ErrorsType errorsType)
	{
		super.validate(airShoppingRQ, errorsType);
	}
}
