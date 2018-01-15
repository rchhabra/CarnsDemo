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

package de.hybris.platform.accommodationaddon.validators;

import de.hybris.platform.accommodationaddon.forms.AccommodationBookingChangeDateForm;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component("accommodationBookingChangeDateValidator")
public class AccommodationBookingChangeDateValidator extends AbstractTravelValidator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AccommodationBookingChangeDateValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AccommodationBookingChangeDateForm form = (AccommodationBookingChangeDateForm) object;

		validateBlankField("bookingReference", form.getBookingReference(), errors);
		validateDates(form.getCheckInDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE,
				form.getCheckOutDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE, errors);
	}
}
