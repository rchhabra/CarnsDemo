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

import de.hybris.platform.accommodationaddon.forms.LeadGuestDetailsForm;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("leadGuestDetailsFormsValidator")
public class GuestDetailsValidator implements Validator
{

	private static final Logger LOGGER = Logger.getLogger(GuestDetailsValidator.class);

	private static final String FIELD_FIRSTNAME = "guestData.profile.firstName";
	private static final String FIELD_LASTNAME = "guestData.profile.lastName";
	private static final String FIELD_CONTACTNUMBER = "guestData.profile.contactNumber";
	private static final String FIELD_EMAIL = "guestData.profile.email";
	private static final String FIELD_ARRIVAL_TIME = "arrivalTime";
	private static final String ERROR_TYPE_PATTERN = "Pattern";
	private static final String ERROR_TYPE_INVALID_VALUE = "InvalidValue";
	private static final String FIRST_LEAD_GUEST_FORM = "0";

	private static final String LEAD_GUEST_FORMS = "leadForms";

	private static final String ERROR_TYPE_NOT_EMPTY = "NotEmpty";

	private static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");


	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return LeadGuestDetailsForm.class.equals(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final List<LeadGuestDetailsForm> leadForms = (List<LeadGuestDetailsForm>) object;

		leadForms.forEach(lf ->
		{

			validateGuestName(lf.getGuestData().getProfile().getFirstName(), lf.getGuestData().getProfile().getLastName(),
					lf.getFormId(), errors);
			if (StringUtils.equals(lf.getRoomStayRefNumber(), FIRST_LEAD_GUEST_FORM))
			{
				validateContactNumber(lf.getGuestData().getProfile().getContactNumber(), lf.getFormId(), errors);
			}
			validateGuests(lf.getPassengerTypeQuantityData(), lf.getFormId(), lf.getRoomStayRefNumber(), errors);
			validateArrivalTime(lf.getArrivalTime(), lf.getFormId(), errors);
			validateEmail(lf.getGuestData().getProfile().getEmail(), errors, lf.getFormId());

		});

	}

	private void validateContactNumber(final String contactNumber, final String formId, final Errors errors)
	{
		if (StringUtils.isNotEmpty(contactNumber.trim()))
		{
			if (!contactNumber.matches(TravelacceleratorstorefrontValidationConstants.REGEX_AT_LEAST_ONE_NUMBER)
					|| !contactNumber.matches(TravelacceleratorstorefrontValidationConstants.REGEX_NUMBER_AND_SPECIAL_CHARS))
			{
				rejectValue(errors, FIELD_CONTACTNUMBER, formId, ERROR_TYPE_PATTERN);
			}
		}
		else if (StringUtils.isEmpty(contactNumber.trim()))
		{
			rejectValue(errors, FIELD_CONTACTNUMBER, formId, ERROR_TYPE_NOT_EMPTY);
		}
	}

	private void validateGuestName(final String firstName, final String lastName, final String formId, final Errors errors)
	{
		validateName(firstName, FIELD_FIRSTNAME, formId, errors);
		validateName(lastName, FIELD_LASTNAME, formId, errors);
	}

	protected void validateName(final String name, final String attributeName, final String formId, final Errors errors)
	{
		if (StringUtils.isNotBlank(name.trim())
				&& !name.matches(TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_SPACES_FIRST_LETTER))
		{
			rejectValue(errors, attributeName, formId, ERROR_TYPE_PATTERN);
		}
		else if (StringUtils.isEmpty(name.trim()))
		{
			rejectValue(errors, attributeName, formId, ERROR_TYPE_NOT_EMPTY);
		}
	}


	private void validateGuests(final List<PassengerTypeQuantityData> passengerTypeQuantityData, final String formId,
			final String roomStayRefNumber, final Errors errors)
	{
		int adultPassengers = 0;
		int nonAdultPassengers = 0;
		String attributeName = null;

		final List<GuestOccupancyData> guestOccupancies = createGuestOccupancy(roomStayRefNumber);

		for (int j = 0; j < passengerTypeQuantityData.size(); j++)
		{
			attributeName = LEAD_GUEST_FORMS + "[" + formId + "]" + "." + "passengerTypeQuantityData" + "[" + j + "]" + "."
					+ "quantity";
			final int selectedGuestCount = passengerTypeQuantityData.get(j).getQuantity();
			final String selectedGuestCode = passengerTypeQuantityData.get(j).getPassengerType().getCode();

			validateGuestQuanityAsPerGuestOccupancies(guestOccupancies, attributeName, selectedGuestCount, selectedGuestCode, formId,
					errors);

			if (TravelacceleratorstorefrontValidationConstants.PASSENGER_TYPE_ADULT.equals(selectedGuestCode))
			{
				if (selectedGuestCount < 0)
				{
					errors.rejectValue(attributeName, "InvalidQuantity" + "."
							+ TravelacceleratorstorefrontValidationConstants.ACCOMMODATION_FORM + "." + selectedGuestCode);
				}
				adultPassengers = selectedGuestCount;
			}
			else
			{
				if (selectedGuestCount < 0)
				{
					errors.rejectValue(attributeName, "InvalidQuantity" + "."
							+ TravelacceleratorstorefrontValidationConstants.ACCOMMODATION_FORM + "." + selectedGuestCode);
				}
				nonAdultPassengers += selectedGuestCount;
			}
		}

		attributeName = LEAD_GUEST_FORMS + "[" + formId + "]" + "." + "passengerTypeQuantityData" + "[" + 0 + "]" + "."
				+ "quantity";
		if (nonAdultPassengers >= 0 && adultPassengers >= 0)
		{
			if (nonAdultPassengers > 0 && adultPassengers == 0)
			{
				errors.rejectValue(attributeName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_NUMBER_OF_ADULTS);
				return;
			}

			if (nonAdultPassengers + adultPassengers == 0)
			{
				errors.rejectValue(attributeName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_NO_PASSENGER);
				return;
			}
		}

	}

	protected List<GuestOccupancyData> createGuestOccupancy(final String roomStayRefNumber)
	{
		final List<GuestOccupancyData> guestOccupancies = bookingFacade
				.getGuestOccupanciesFromCart(Integer.parseInt(roomStayRefNumber));
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
		if (CollectionUtils.isNotEmpty(guestOccupancies))
		{
			final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();
			if (passengerTypes.size() != guestOccupancies.size())
			{
				final List<GuestOccupancyData> modifiableGuestOccupancies = new ArrayList<>();
				final List<String> passengerCodes = new ArrayList<>();
				guestOccupancies.forEach(go -> passengerCodes.add(go.getPassengerType().getCode()));
				final List<PassengerTypeData> passengerTypeList = passengerTypes.stream()
						.filter(pt -> !passengerCodes.contains(pt.getCode())).collect(Collectors.toList());
				passengerTypeList.forEach(pt ->
				{
					final GuestOccupancyData guestOccupancy = new GuestOccupancyData();
					guestOccupancy.setPassengerType(pt);
					guestOccupancy.setQuantityMin(
							StringUtils.equals(TravelacceleratorstorefrontValidationConstants.PASSENGER_TYPE_ADULT, pt.getCode()) ? 1
									: 0);
					guestOccupancy.setQuantityMax(maxGuestQuantity);
					modifiableGuestOccupancies.add(guestOccupancy);
				});
				guestOccupancies.addAll(modifiableGuestOccupancies);
			}
		}
		return guestOccupancies;
	}

	private void validateGuestQuanityAsPerGuestOccupancies(final List<GuestOccupancyData> guestOccupancies,
			final String attributeName, final int selectedGuestCount, final String selectedGuestCode, final String formId,
			final Errors errors)
	{
		if (CollectionUtils.isEmpty(guestOccupancies))
		{
			final String sessionBookingJourney = sessionService
					.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
			final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
			if (selectedGuestCount > maxGuestQuantity)
			{
				errors.rejectValue(attributeName, "InvalidQuantity" + "."
						+ TravelacceleratorstorefrontValidationConstants.ACCOMMODATION_FORM + "." + selectedGuestCode);
			}
		}
		else
		{
			final GuestOccupancyData guestOccupancy = guestOccupancies.stream()
					.filter(go -> go.getPassengerType().getCode().equalsIgnoreCase(selectedGuestCode)).findFirst().get();
			if (selectedGuestCount > guestOccupancy.getQuantityMax())
			{
				errors.rejectValue(attributeName, "InvalidQuantity" + "."
						+ TravelacceleratorstorefrontValidationConstants.ACCOMMODATION_FORM + "." + selectedGuestCode);
			}
		}
	}

	private void validateArrivalTime(final String arrivalTime, final String formId, final Errors errors)
	{
		final Date date = TravelDateUtils.convertStringDateToDate(arrivalTime, TravelservicesConstants.DATE_TIME_PATTERN);
		if (date == null)
		{
			rejectValue(errors, FIELD_ARRIVAL_TIME, formId, ERROR_TYPE_INVALID_VALUE);
		}

	}

	protected void validateEmail(final String email, final Errors errors, final String formId)
	{
		if (StringUtils.isNotBlank(email) && (StringUtils.length(email) > 255 || !validateEmailAddress(email)))
		{
			rejectValue(errors, FIELD_EMAIL, formId, ERROR_TYPE_PATTERN);
		}
	}

	protected boolean validateEmailAddress(final String email)
	{
		final Matcher matcher = EMAIL_REGEX.matcher(email);
		return matcher.matches();
	}

	protected void rejectValue(final Errors errors, final String attributeName, final String formId, final String errorCode)
	{
		final String attribute = LEAD_GUEST_FORMS + "[" + formId + "]" + "." + attributeName;
		final String errorMessageCode = errorCode + "." + attributeName;
		errors.rejectValue(attribute, errorMessageCode);
	}

}
