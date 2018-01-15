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

import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component("accommodationAddToCartValidator")
public class AccommodationAddToCartValidator extends AbstractTravelValidator
{
	private static String ERROR_ROOM_RATE_CODES_EMPTY = "emptyRoomRateCodes";
	private static String ERROR_ROOM_RATE_DATES = "errorRoomRateDates";
	private static String ERROR_NUMBER_OF_ROOMS_CODE = "invalidNumberOfRooms";
	private static String ERROR_ROOM_STAY_REFERENCE_NUMBER = "invalidRoomPreferenceNumber";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AccommodationAddToCartValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AccommodationAddToCartForm form = (AccommodationAddToCartForm) object;

		validateBlankField("accommodationCode", form.getAccommodationCode(), errors);
		validateBlankField("accommodationOfferingCode", form.getAccommodationOfferingCode(), errors);
		validateBlankField("ratePlanCode", form.getRatePlanCode(), errors);
		validateDates(form.getCheckInDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE,
				form.getCheckOutDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE, errors);
		validateRoomStayReferenceNumber(form.getRoomStayRefNumber(), errors);
		validateNumberOfRooms(form.getNumberOfRooms(), errors);
		validateRoomRateCodes(form.getRoomRateCodes(), errors);
		validateRoomRateDates(form, errors);
	}

	protected void validateRoomRateCodes(final List<String> roomRateCodes, final Errors errors)
	{
		if (CollectionUtils.isEmpty(roomRateCodes))
		{
			rejectValue(errors, "roomRateCodes", ERROR_ROOM_RATE_CODES_EMPTY);
		}
	}

	protected void validateRoomRateDates(final AccommodationAddToCartForm form, final Errors errors)
	{
		final boolean isSizeSame = form.getRoomRateCodes().size() == form.getRoomRateDates().size();

		if (CollectionUtils.isEmpty(form.getRoomRateDates()) || !isSizeSame)
		{
			rejectValue(errors, "roomRateDates", ERROR_ROOM_RATE_DATES);
		}

		if (CollectionUtils.isNotEmpty(form.getRoomRateDates()))
		{
			form.getRoomRateDates().forEach(date -> validateDateFormat(date, errors, "roomRateDates"));
		}
	}

	protected void validateNumberOfRooms(final int numberOfRooms, final Errors errors)
	{
		final int maxBookingAllowed = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		if (numberOfRooms == 0 && numberOfRooms <= maxBookingAllowed)
		{
			rejectValue(errors, "numberOfRooms", ERROR_NUMBER_OF_ROOMS_CODE);
		}
	}

	protected void validateRoomStayReferenceNumber(final Integer roomPreferenceNumber, final Errors errors)
	{
		if (roomPreferenceNumber < 0)
		{
			rejectValue(errors, "roomStayRefNumber", ERROR_ROOM_STAY_REFERENCE_NUMBER);
		}
	}
}
