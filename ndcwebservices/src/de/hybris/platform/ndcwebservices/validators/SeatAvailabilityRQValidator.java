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
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;

import org.apache.commons.collections.CollectionUtils;


/**
 * Validator class for SeatAvailabilityRQ
 */
public class SeatAvailabilityRQValidator extends NCDAbstractRequestValidator<SeatAvailabilityRQ>
{
	/**
	 * Validate an SeatAvailabilityRQ request using a list of validators defined in it's bean
	 *
	 * @param seatAvailabilityRQ
	 * 		the SeatAvailabilityRQ that needs to be validated
	 * @param seatAvailabilityRS
	 * 		the SeatAvailabilityRS to collect the errors
	 *
	 * @return true if no error otherwise false
	 */
	public boolean validateSeatAvailabilityRQ(final SeatAvailabilityRQ seatAvailabilityRQ,
			final SeatAvailabilityRS seatAvailabilityRS)
	{
		final ErrorsType errorType = validateNDCRequest(seatAvailabilityRQ);

		if (CollectionUtils.isNotEmpty(errorType.getError()))
		{
			seatAvailabilityRS.setErrors(errorType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
