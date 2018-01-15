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
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component("accommodationFinderValidator")
public class AccommodationFinderValidator extends AbstractTravelValidator
{

	private static final Logger LOGGER = Logger.getLogger(AccommodationFinderValidator.class);
	private static final String ERROR_INVALID_LOCATION = "InvalidLocation";
	private static final String ERROR_TYPE_INVALID_FIELD_FORMAT = "InvalidFormat";
	private static final String FIELD_DESTINATION_LOCATION = "destinationLocation";
	private static final String ERROR_INVALID_SUGGESTION_TYPE = "InvalidSuggestionType";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AccommodationFinderForm.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AccommodationFinderForm accommodationFinderForm = (AccommodationFinderForm) object;

		validateDestinationLocation(accommodationFinderForm, errors);
		validateDates(accommodationFinderForm.getCheckInDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE,
				accommodationFinderForm.getCheckOutDateTime(), TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE, errors);
		validateGuestsQuantity(accommodationFinderForm.getNumberOfRooms(), accommodationFinderForm.getRoomStayCandidates(), errors);
	}

	protected void validateDestinationLocation(final AccommodationFinderForm accommodationFinderForm, final Errors errors)
	{

		// destinationLocationName should always be populated
		if (validateBlankField(AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME,
				accommodationFinderForm.getDestinationLocationName(), errors) && validateFieldFormat(
				AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, accommodationFinderForm.getDestinationLocationName(),
				TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_COMMAS_SPACES_DASHES_PARENTHESIS, errors))
		{
			final String suggestionType = accommodationFinderForm.getSuggestionType();
			final String destinationLocation = accommodationFinderForm.getDestinationLocation();
			if(StringUtils.isNotBlank(destinationLocation) && StringUtils.isNotBlank(suggestionType))
			{
				validateCodeAndSuggestionType(accommodationFinderForm, errors);
			}
			else
			{

				if (StringUtils.isBlank(destinationLocation) && StringUtils.isBlank(suggestionType))
				{
					if (validateLatitudeLongitudeRadius(accommodationFinderForm))
					{
						validateCoordinates(accommodationFinderForm, errors);
					}
					else
					{
						rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION, ERROR_INVALID_LOCATION);
					}
				}
				else
				{
					rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION, ERROR_INVALID_LOCATION);
				}

			}
		}

	}

	protected boolean validateCodeAndSuggestionType(final AccommodationFinderForm accommodationFinderForm, final Errors errors)
	{
			final String suggestionType = accommodationFinderForm.getSuggestionType();
			try
			{
				SuggestionType.valueOf(suggestionType);
			}
			catch (final IllegalArgumentException e)
			{
				LOGGER.debug("No SuggestionType found with code " + suggestionType);
				rejectValue(errors, AccommodationaddonWebConstants.SUGGESTION_TYPE, ERROR_INVALID_SUGGESTION_TYPE);
				return false;
			}
		return validateFieldFormat(FIELD_DESTINATION_LOCATION, accommodationFinderForm.getDestinationLocation(),
				TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_PIPELINE, errors);
	}

	protected boolean validateLatitudeLongitudeRadius(final AccommodationFinderForm accommodationFinderForm)
	{
		final String latitude = accommodationFinderForm.getLatitude();
		final String longitude = accommodationFinderForm.getLongitude();
		final String radius = accommodationFinderForm.getRadius();

		if (StringUtils.isBlank(latitude) || StringUtils.isBlank(longitude) || StringUtils.isBlank(radius))
		{
			return false;
		}
		return true;
	}


	/**
	 * Method responsible for running the following validation on Latitude, Longitude, and Radius
	 *
	 * @param accommodationFinderForm
	 * @param errors
	 */
	protected void validateCoordinates(final AccommodationFinderForm accommodationFinderForm, final Errors errors)
	{
		if (StringUtils.isBlank(accommodationFinderForm.getLatitude())
				|| StringUtils.isBlank(accommodationFinderForm.getLongitude())
				|| StringUtils.isBlank(accommodationFinderForm.getRadius()))
		{
			rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, ERROR_INVALID_LOCATION);
		}
		else
		{
			try
			{
				final Double latitude = Double.parseDouble(accommodationFinderForm.getLatitude());
				final Double longitude = Double.parseDouble(accommodationFinderForm.getLongitude());
				final Double radius = Double.parseDouble(accommodationFinderForm.getRadius());

				if (latitude < TravelacceleratorstorefrontValidationConstants.MIN_LATITUDE
						|| latitude > TravelacceleratorstorefrontValidationConstants.MAX_LATITUDE)
				{
					rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, ERROR_INVALID_LOCATION);
					LOGGER.warn("Latitude not valid: its value " + latitude + " doesn't belong to the allowed range.");
				}
				if (longitude < TravelacceleratorstorefrontValidationConstants.MIN_LONGITUDE
						|| longitude > TravelacceleratorstorefrontValidationConstants.MAX_LONGITUDE)
				{
					rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, ERROR_INVALID_LOCATION);
					LOGGER.warn("Longitude not valid: its value " + longitude + " doesn't belong to the allowed range.");
				}
				if (radius < 0 || radius > TravelacceleratorstorefrontValidationConstants.MAX_RADIUS)
				{
					rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, ERROR_INVALID_LOCATION);
					LOGGER.warn("Radius not valid: its value " + radius + " doesn't belong to the allowed range.");
				}
			}
			catch (final NumberFormatException e)
			{
				rejectValue(errors, AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME, ERROR_INVALID_LOCATION);
			}
		}

	}

	/**
	 * Method runs the regex against the fieldValue and sets an InvalidFormat error against the field if the regex fails
	 *
	 * @param fieldName
	 * @param fieldValue
	 * @param regex
	 * @param errors
	 */
	protected Boolean validateFieldFormat(final String fieldName, final String fieldValue, final String regex, final Errors errors)
	{
		if (!Pattern.matches(regex, fieldValue))
		{
			rejectValue(errors, fieldName, ERROR_TYPE_INVALID_FIELD_FORMAT);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
