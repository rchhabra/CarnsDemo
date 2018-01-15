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
 */
package de.hybris.platform.traveladdon.validators;

import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.LocationType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 * Custom validation to valid Fare Finder Form attributes which can't be validated using JSR303
 */

@Component("fareFinderValidator")
public class FareFinderValidator extends AbstractTravelValidator
{

	private static final Logger LOGGER = Logger.getLogger(FareFinderValidator.class);

	@Resource
	private CabinClassFacade cabinClassFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	private static final String FIELD_RETURN_DATE_TIME = "returnDateTime";
	private static final String FIELD_TRIP_TYPE = "tripType";
	private static final String FIELD_CABIN_CLASS = "cabinClass";
	private static final String FIELD_DEPARTING_DATE_TIME = "departingDateTime";
	private static final String FIELD_PASSENGER_TYPE_QUANTITY_LIST = "passengerTypeQuantityList";
	private static final String FIELD_DEPARTURE_LOCATION = "departureLocation";
	private static final String FIELD_DEPARTURE_LOCATION_NAME = "departureLocationName";
	private static final String FIELD_ARRIVAL_LOCATION = "arrivalLocation";
	private static final String FIELD_ARRIVAL_LOCATION_NAME = "arrivalLocationName";
	private static final String FIELD_ARRIVAL_LOCATION_TYPE = "arrivalLocationSuggestionType";
	private static final String FIELD_INVALID_NUMBER_OF_PASSENGERS = "InvalidNumberOfPassengers";

	private static final String ERROR_TYPE_INVALID_NUMBER_OF_ADULTS = "InvalidNumberOfAdults";
	private static final String ERROR_TYPE_NO_PASSENGER = "NoPassengerSelected";
	private static final String ERROR_TYPE_NOT_POPULATED = "NotPopulated";
	private static final String ERROR_TYPE_OUT_OF_RANGE = "OutOfRange";
	private static final String ERROR_TYPE_INVALID_TYPE = "InvalidType";
	private static final String ERROR_TYPE_INVALID_FIELD_FORMAT = "InvalidFormat";
	private static final String ERROR_TYPE_INVALID_ENUM_VALUE = "InvalidEnumValue";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return FareFinderValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final FareFinderForm fareFinderForm = (FareFinderForm) object;

		// first check if there is a valid trip type and cabin class set
		validateTripType(fareFinderForm, errors);
		validateCabinClass(fareFinderForm, errors);
		validateArrivalLocation(fareFinderForm, errors);
		validateDepartureLocation(fareFinderForm, errors);

		// if no errors exist then proceed with the following validations
		if (!errors.hasErrors())
		{
			validateDates(fareFinderForm, errors);
			validatePassengerTypeQuantity(fareFinderForm, errors);
		}
	}

	/**
	 * Method checks if the requested Trip Type is a valid type and sets an INVALID_TYPE error if invalid.
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateTripType(final FareFinderForm fareFinderForm, final Errors errors)
	{
		if (StringUtils.isBlank(fareFinderForm.getTripType()))
		{
			validateBlankField(FIELD_TRIP_TYPE, fareFinderForm.getTripType(), errors);
		}
		else
		{
			try
			{
				TripType.valueOf(fareFinderForm.getTripType());
			}
			catch (final IllegalArgumentException iae)
			{
				LOGGER.debug("No Trip Type found with code " + fareFinderForm.getTripType());
				rejectValue(errors, FIELD_TRIP_TYPE, ERROR_TYPE_INVALID_TYPE);
			}
		}
	}

	/**
	 * Method checks if the requested cabin class is a valid class and sets an INVALID_TYPE error if invalid.
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateCabinClass(final FareFinderForm fareFinderForm, final Errors errors)
	{

		if (StringUtils.isBlank(fareFinderForm.getCabinClass()))
		{
			validateBlankField(FIELD_CABIN_CLASS, fareFinderForm.getCabinClass(), errors);
		}
		else
		{
			final List<CabinClassData> cabinClasses = cabinClassFacade.getCabinClasses();

			final boolean isValid = cabinClasses.stream()
					.anyMatch(cc -> cc.getCode().equalsIgnoreCase(fareFinderForm.getCabinClass()));

			if (!isValid)
			{
				LOGGER.debug("No Cabin Class found with code " + fareFinderForm.getCabinClass());
				rejectValue(errors, FIELD_CABIN_CLASS, ERROR_TYPE_INVALID_TYPE);
			}
		}
	}

	/**
	 * Method responsible to ensure departure location value and departure location name value re not blank. Method also
	 * check to see if arrival location name contain only combinations the following:
	 * <ul>
	 * <li>Uppercase and lowercase characters from A to Z</li>
	 * <li>Spaces</li>
	 * <li>Dash/Hyphen</li>
	 * <li>Parenthesis</li>
	 * </ul>
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateDepartureLocation(final FareFinderForm fareFinderForm, final Errors errors)
	{
		final Boolean isNotBlank = validateBlankField(FIELD_DEPARTURE_LOCATION_NAME, fareFinderForm.getDepartureLocationName(),
				errors);
		if (isNotBlank)
		{
			validateBlankField(FIELD_DEPARTURE_LOCATION, fareFinderForm.getDepartureLocation(), errors);
		}

		boolean isValid = true;

		if (StringUtils.isNotBlank(fareFinderForm.getDepartureLocationName()))
		{
			isValid = validateFieldFormat(FIELD_DEPARTURE_LOCATION_NAME, fareFinderForm.getDepartureLocationName(),
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES, errors);
		}

		if (StringUtils.isNotBlank(fareFinderForm.getDepartureLocation()) && isValid)
		{
			validateFieldFormat(FIELD_DEPARTURE_LOCATION, fareFinderForm.getDepartureLocation(),
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES, errors);
		}
	}

	/**
	 * Method responsible to ensure arrival location value and arrival location name value re not blank. Method also
	 * check to see if arrival location name contain only combinations the following:
	 * <ul>
	 * <li>Uppercase and lowercase characters from A to Z</li>
	 * <li>Spaces</li>
	 * <li>Dash/Hyphen</li>
	 * <li>Parenthesis</li>
	 * </ul>
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateArrivalLocation(final FareFinderForm fareFinderForm, final Errors errors)
	{
		final Boolean isNotBlank = validateBlankField(FIELD_ARRIVAL_LOCATION_NAME, fareFinderForm.getArrivalLocationName(), errors);
		if (isNotBlank)
		{
			validateBlankField(FIELD_ARRIVAL_LOCATION, fareFinderForm.getArrivalLocation(), errors);
		}

		boolean isValid = true;

		if (StringUtils.isNotBlank(fareFinderForm.getArrivalLocationName()))
		{
			isValid = validateFieldFormat(FIELD_ARRIVAL_LOCATION_NAME, fareFinderForm.getArrivalLocationName(),
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES, errors);
		}

		if (StringUtils.isNotBlank(fareFinderForm.getArrivalLocation()) && isValid)
		{
			validateFieldFormat(FIELD_ARRIVAL_LOCATION, fareFinderForm.getArrivalLocation(),
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES, errors);
		}

		if (StringUtils.isNotBlank(fareFinderForm.getArrivalLocationSuggestionType()) && isValid)
		{
			validateSuggestionValue(FIELD_ARRIVAL_LOCATION_TYPE, fareFinderForm.getArrivalLocationSuggestionType(), errors);
		}

	}

	/**
	 * Validates if a value belongs to a given enumerator
	 *
	 * @param fieldLocationType
	 * 		the field location type
	 * @param locationSuggestionType
	 * 		the location suggestion type
	 * @param errors
	 * 		the errors
	 */
	protected void validateSuggestionValue(final String fieldLocationType, final String locationSuggestionType,
			final Errors errors)
	{
		if (Objects.isNull(enumerationService.getEnumerationValue(LocationType.class, locationSuggestionType)))
		{
			rejectValue(errors, fieldLocationType, ERROR_TYPE_INVALID_ENUM_VALUE);
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

	/**
	 * Method responsible for running the following validation on Departing and Arriving dates
	 * <ul>
	 * <li><b>validateDateFormat</b>(String date, Errors errors, String field)</li>
	 * <li><b>validateDateIsPopulated</b>(String date, Errors errors, String field)</li>
	 * <li><b>validateDepartureAndArrivalDateTimeRange</b>(FareFinderForm fareFinderForm, Errors errors)</li>
	 * </ul>
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateDates(final FareFinderForm fareFinderForm, final Errors errors)
	{

		final boolean isDepartingDatePopulated = validateDateIsPopulated(fareFinderForm.getDepartingDateTime(), errors,
				FIELD_DEPARTING_DATE_TIME);

		boolean isDepartingDateCorrectlyFormatted = false;
		boolean isArrivalDateCorrectlyFormatted = false;

		if (isDepartingDatePopulated)
		{
			isDepartingDateCorrectlyFormatted = validateDateFormat(fareFinderForm.getDepartingDateTime(), errors,
					FIELD_DEPARTING_DATE_TIME);

			if (isDepartingDateCorrectlyFormatted)
			{
				validateForPastDate(fareFinderForm.getDepartingDateTime(), errors, FIELD_DEPARTING_DATE_TIME);
			}
		}

		if (StringUtils.equalsIgnoreCase(fareFinderForm.getTripType(), TripType.RETURN.name()))
		{

			final boolean isReturnDatePopulated = validateDateIsPopulated(fareFinderForm.getReturnDateTime(), errors,
					FIELD_RETURN_DATE_TIME);

			if (isReturnDatePopulated)
			{
				isArrivalDateCorrectlyFormatted = validateDateFormat(fareFinderForm.getReturnDateTime(), errors,
						FIELD_RETURN_DATE_TIME);
			}
		}

		if (isDepartingDateCorrectlyFormatted && isArrivalDateCorrectlyFormatted)
		{
			validateDepartureAndArrivalDateTimeRange(fareFinderForm, errors);
		}
	}

	/**
	 * Method used to validate Departing and Arrival dates
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	protected void validateDepartureAndArrivalDateTimeRange(final FareFinderForm fareFinderForm, final Errors errors)
	{

		if (StringUtils.equalsIgnoreCase(fareFinderForm.getTripType(), TripType.RETURN.name()))
		{
			try
			{
				final SimpleDateFormat formatter = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);

				final Date departingDate = formatter.parse(fareFinderForm.getDepartingDateTime());
				final Date arrivalDate = formatter.parse(fareFinderForm.getReturnDateTime());

				if (departingDate.after(arrivalDate))
				{
					rejectValue(errors, FIELD_DEPARTING_DATE_TIME, ERROR_TYPE_OUT_OF_RANGE);
				}
			}
			catch (final ParseException e)
			{
				LOGGER.error("Unable to pharse String data to Date object:", e);
			}
		}
	}

	/**
	 * Method responsible for checking if date is populate
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	protected boolean validateDateIsPopulated(final String date, final Errors errors, final String field)
	{
		if (StringUtils.isNotEmpty(date))
		{
			return true;
		}

		rejectValue(errors, field, ERROR_TYPE_NOT_POPULATED);

		return false;
	}

	/**
	 * Method responsible for checking that at least one adult is present is there are any children or infants traveling
	 *
	 * @param fareFinderForm
	 */
	protected void validatePassengerTypeQuantity(final FareFinderForm fareFinderForm, final Errors errors)
	{
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION)
				|| StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_PACKAGE))
		{
			return;
		}

		int adultPassengers = 0;
		int nonAdultPassengers = 0;

		if (fareFinderForm.getPassengerTypeQuantityList() != null)
		{
			for (final PassengerTypeQuantityData passengerTypeQuantity : fareFinderForm.getPassengerTypeQuantityList())
			{
				if (!passengerTypeQuantity.getPassengerType().getCode()
						.matches(TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_ONLY))
				{
					rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST, "Invalid Passenger Type");
					continue;
				}

				if (TraveladdonWebConstants.PASSENGER_TYPE_ADULT.equals(passengerTypeQuantity.getPassengerType().getCode()))
				{
					if (passengerTypeQuantity.getQuantity() < 0)
					{
						rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST,
								"InvalidQuantity" + passengerTypeQuantity.getPassengerType().getCode());
					}
					adultPassengers = passengerTypeQuantity.getQuantity();
				}
				else
				{
					if (passengerTypeQuantity.getQuantity() < 0)
					{
						rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST,
								"InvalidQuantity" + passengerTypeQuantity.getPassengerType().getCode());
					}
					nonAdultPassengers += passengerTypeQuantity.getQuantity();
				}
			}

			if (nonAdultPassengers >= 0 && adultPassengers >= 0)
			{
				if (nonAdultPassengers > 0 && adultPassengers == 0)
				{
					rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST, ERROR_TYPE_INVALID_NUMBER_OF_ADULTS);
				}

				if (nonAdultPassengers + adultPassengers == 0)
				{
					rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST, ERROR_TYPE_NO_PASSENGER);
				}

				final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));

				if (nonAdultPassengers + adultPassengers > maxGuestQuantity)
				{
					rejectValue(errors, FIELD_PASSENGER_TYPE_QUANTITY_LIST, FIELD_INVALID_NUMBER_OF_PASSENGERS);
				}
			}
		}
	}
}
