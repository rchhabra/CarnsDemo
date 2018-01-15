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
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation for {@link NCDAbstractRequestValidator} that validates {@link SeatAvailabilityRS}
 */
public class SeatAvailabilityRSValidator extends NCDAbstractRequestValidator<SeatAvailabilityRS>
{
	/**
	 * Validate an SeatAvailabilityRS
	 *
	 * @param seatAvailabilityRS
	 * 		the seatAvailabilityRS to validate
	 *
	 * @return boolean that indicates the presence of any error
	 */
	public boolean validateSeatAvailabilityRS(final SeatAvailabilityRS seatAvailabilityRS)
	{
		final ErrorsType errorsType = validateNDCRequest(seatAvailabilityRS);

		if (CollectionUtils.isNotEmpty(errorsType.getError()))
		{
			seatAvailabilityRS.setErrors(errorsType);
			return false;
		}
		return true;
	}
}
