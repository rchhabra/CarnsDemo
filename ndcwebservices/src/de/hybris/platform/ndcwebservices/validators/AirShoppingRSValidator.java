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

import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import org.apache.commons.collections.CollectionUtils;


/**
 * Validator class for Air Shopping Response
 */
public class AirShoppingRSValidator extends NCDAbstractRequestValidator
{
	/**
	 * Validate an AirShoppingRS
	 *
	 * @param airShoppingRS the airShoppingRS to validate
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateAirShoppingRS(final AirShoppingRS airShoppingRS)
	{
		final ErrorsType errorsType = validateNDCRequest(airShoppingRS);

		if(CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			airShoppingRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
