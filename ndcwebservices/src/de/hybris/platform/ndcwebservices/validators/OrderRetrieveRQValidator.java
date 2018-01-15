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
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;

import org.apache.commons.collections.CollectionUtils;


/**
 * Validator class for OrderRetrieveRQValidator
 */
public class OrderRetrieveRQValidator extends NCDAbstractRequestValidator
{
	/**
	 * Validate an orderRetrieveRQ request using a list of validators
	 *
	 * @param orderRetrieveRQ
	 * 		the orderRetrieveRQ that needs to be validated
	 * @param orderViewRS
	 * 		the orderViewRS to collect the errors
	 *
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateOrderRetrieveRQ(final OrderRetrieveRQ orderRetrieveRQ, final OrderViewRS orderViewRS)
	{
		final ErrorsType errorsType = validateNDCRequest(orderRetrieveRQ);

		if (CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			orderViewRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
