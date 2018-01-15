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

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component("accommodationAvailabilityValidator")
public class AccommodationAvailabilityValidator extends AbstractTravelValidator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AccommodationAvailabilityForm.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AccommodationAvailabilityForm accommodationAvailabilityForm = (AccommodationAvailabilityForm) object;

		setTargetForm(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM);
		validateDates(accommodationAvailabilityForm.getCheckInDateTime(),
				TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE, accommodationAvailabilityForm.getCheckOutDateTime(),
				TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE, errors);
		validateGuestsQuantity(accommodationAvailabilityForm.getNumberOfRooms(),
				accommodationAvailabilityForm.getRoomStayCandidates(), errors);
	}

}
