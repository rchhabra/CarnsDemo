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
package de.hybris.platform.traveladdon.forms.validation;

import de.hybris.platform.traveladdon.forms.PassengerInformationForm;
import de.hybris.platform.traveladdon.forms.TravellerForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Custom validation for PassengerInformationForm
 */

@Component("travellerFormValidator")
public class TravellerFormValidator implements Validator
{
	private static final String TRAVELLER_FORMS = "travellerForms";

	private static final String FIELD_FREQUENT_FLYER_MEMBERSHIP_NUMBER = "passengerInformation.frequentFlyerMembershipNumber";
	private static final String FIELD_FIRSTNAME = "passengerInformation.firstname";
	private static final String FIELD_LASTNAME = "passengerInformation.lastname";
	private static final String FIELD_BOOKER = "booker";
	private static final String FIELD_EMAIL = "passengerInformation.email";
	private static final String FIELD_TITLE = "passengerInformation.title";
	private static final String FIELD_GENDER = "passengerInformation.gender";
	private static final String FIELD_CONTACTNUMBER = "passengerInformation.contactNumber";
	private static final String FIRST_TRAVELLER_FORM = "0";

	private static final String ERROR_TYPE_PATTERN = "Pattern";
	private static final String ERROR_TYPE_NOT_EMPTY = "NotEmpty";

	private static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

	@Override
	public boolean supports(final Class<?> cls)
	{
		return TravellerFormValidator.class.equals(cls);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final List<TravellerForm> travellerForms = (List<TravellerForm>) object;

		travellerForms.forEach(tf -> {

			final PassengerInformationForm passengerInformation = tf.getPassengerInformation();

			// valid Frequent Flyer Membership Number
			if (passengerInformation.isFrequentFlyer())
			{
				validateFrequentFlyerNumber(passengerInformation.getFrequentFlyerMembershipNumber(), tf.getFormId(), errors);
			}

			// we only need to check this for the first form as this is where the booker will enter their details if travelling
			if (StringUtils.equals(tf.getFormId(), FIRST_TRAVELLER_FORM))
			{
				validateBookerIsTravelling(tf.getBooker(), tf.getFormId(), errors);
				if (passengerInformation.isValidateContactNumber())
				{
					validateContactNumber(errors, passengerInformation.getContactNumber(), tf.getFormId());
				}
			}
			validateTitle(errors, passengerInformation.getTitle(), tf.getFormId());
			validateGender(errors, passengerInformation.getGender(), tf.getFormId());
			validatePassengerName(passengerInformation, errors, tf.getFormId());

			validateEmail(errors, passengerInformation.getEmail(), tf.getFormId());

		});

	}

	protected void validateFrequentFlyerNumber(final String frequentFlyerNumber, final String formId, final Errors errors)
	{
		if (StringUtils.isBlank(frequentFlyerNumber))
		{
			rejectValue(errors, FIELD_FREQUENT_FLYER_MEMBERSHIP_NUMBER, formId, ERROR_TYPE_NOT_EMPTY);
		}
		else if (StringUtils.length(frequentFlyerNumber) > 50)
		{
			rejectValue(errors, FIELD_FREQUENT_FLYER_MEMBERSHIP_NUMBER, formId, ERROR_TYPE_PATTERN);
		}
		else
		{
			if (!frequentFlyerNumber.matches(TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_NUMBERS_DASHES))
			{
				rejectValue(errors, FIELD_FREQUENT_FLYER_MEMBERSHIP_NUMBER, formId, ERROR_TYPE_PATTERN);
			}
		}
	}

	protected void validatePassengerName(final PassengerInformationForm passengerInformation, final Errors errors,
			final String formId)
	{
		validateName(passengerInformation.getFirstname(), FIELD_FIRSTNAME, formId, errors);
		validateName(passengerInformation.getLastname(), FIELD_LASTNAME, formId, errors);
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

	protected void validateBookerIsTravelling(final Boolean booker, final String formId, final Errors errors)
	{
		if (booker == null)
		{
			rejectValue(errors, FIELD_BOOKER, formId, ERROR_TYPE_NOT_EMPTY);
		}
	}

	protected void validateEmail(final Errors errors, final String email, final String formId)
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

	protected void validateTitle(final Errors errors, final String title, final String formId)
	{
		if (StringUtils.isEmpty(title))
		{
			rejectValue(errors, FIELD_TITLE, formId, ERROR_TYPE_NOT_EMPTY);
		}
	}

	protected void validateGender(final Errors errors, final String gender, final String formId)
	{
		if (StringUtils.isEmpty(gender))
		{
			rejectValue(errors, FIELD_GENDER, formId, ERROR_TYPE_NOT_EMPTY);
		}
	}

	protected void validateContactNumber(final Errors errors, final String contactNumber, final String formId)
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

	/**
	 * Method to add errors
	 *
	 * @param errors
	 * @param attributeName
	 * @param errorCode
	 */
	protected void rejectValue(final Errors errors, final String attributeName, final String formId, final String errorCode)
	{
		final String attribute = TRAVELLER_FORMS + "[" + formId + "]." + attributeName;
		final String errorMessageCode = errorCode + "." + TRAVELLER_FORMS + "." + attributeName;
		errors.rejectValue(attribute, errorMessageCode);
	}

}
