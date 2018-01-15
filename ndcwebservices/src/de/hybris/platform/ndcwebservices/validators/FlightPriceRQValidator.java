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
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS;
import org.apache.commons.collections.CollectionUtils;


/**
 * Validator class for FlightPriceRQ
 */
public class FlightPriceRQValidator extends NCDAbstractRequestValidator
{
	/**
	 * Validate an flightPriceRQ request using a list of validators
	 *
	 * @param flightPriceRQ
	 * 		the flightPriceRQ that needs to be validated
	 * @param flightPriceRS
	 * 		the flightPriceRS to collect the errors
	 *
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateFlightPriceRQ(final FlightPriceRQ flightPriceRQ, final FlightPriceRS flightPriceRS)
	{
		final ErrorsType errorsType = validateNDCRequest(flightPriceRQ);

		if (CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			flightPriceRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
