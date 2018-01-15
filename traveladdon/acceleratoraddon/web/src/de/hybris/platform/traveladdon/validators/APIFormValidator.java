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

import de.hybris.platform.traveladdon.forms.APIForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("apiFormValidator")
public class APIFormValidator extends TravelFormValidator implements Validator
{
	private static final String FIELD_DOCUMENT_EXPIRY_DATE = "documentExpiryDate";
	private static final String FIELD_COUNTRY_OF_ISSUE = "countryOfIssue";
	private static final String FIELD_NATIONALITY = "nationality";
	private static final String FIELD_DOCUMENT_TYPE = "documentType";
	private static final String FIELD_DOCUMENT_NUMBER = "documentNumber";
	private static final String FIELD_DATE_OF_BIRTH = "dateOfBirth";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_FIRSTNAME = "firstname";
	private static final String FIELD_LASTNAME = "lastname";
	private static final String FIELD_GENDER = "gender";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return APIFormValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final APIForm apiForm = (APIForm) object;
		validateAPIData(apiForm, errors);
	}

	/**
	 * Validates the given apiForm and adding fields error to the list of validation errors
	 *
	 * @param apiForm
	 * @param errors
	 */
	private void validateAPIData(final APIForm apiForm, final Errors errors)
	{
		validateIsBlankField(apiForm.getCountryOfIssue(), errors, FIELD_COUNTRY_OF_ISSUE);
		validateIsBlankField(apiForm.getNationality(), errors, FIELD_NATIONALITY);
		validateIsBlankField(apiForm.getTitle(), errors, FIELD_TITLE);
		validateIsBlankField(apiForm.getGender(), errors, FIELD_GENDER);
		validateIsBlankField(apiForm.getDocumentType(), errors, FIELD_DOCUMENT_TYPE);
		validateDocumentType(apiForm.getDocumentType(), errors, FIELD_DOCUMENT_TYPE);

		if (!validateIsBlankField(apiForm.getFirstname().trim(), errors, FIELD_FIRSTNAME))
		{
			validateAgainstRegEx(apiForm.getFirstname(), errors, FIELD_FIRSTNAME,
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_SPACES_FIRST_LETTER);
		}

		if (!validateIsBlankField(apiForm.getLastname(), errors, FIELD_LASTNAME))
		{
			validateAgainstRegEx(apiForm.getLastname(), errors, FIELD_LASTNAME,
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_SPACES_FIRST_LETTER);

		}

		if (!validateIsBlankField(apiForm.getDateOfBirth(), errors, FIELD_DATE_OF_BIRTH))
		{
			if (validateDateFormat(apiForm.getDateOfBirth(), errors, FIELD_DATE_OF_BIRTH))
			{
				validateIsPastDate(apiForm.getDateOfBirth(), errors, FIELD_DATE_OF_BIRTH);
			}
		}

		if (!validateIsBlankField(apiForm.getDocumentExpiryDate(), errors, FIELD_DOCUMENT_EXPIRY_DATE))
		{
			if (validateDateFormat(apiForm.getDocumentExpiryDate(), errors, FIELD_DOCUMENT_EXPIRY_DATE))
			{
				validateIsFutureDate(apiForm.getDocumentExpiryDate(), errors, FIELD_DOCUMENT_EXPIRY_DATE);
			}
		}

		if (!validateIsBlankField(apiForm.getDocumentNumber(), errors, FIELD_DOCUMENT_NUMBER))
		{
			if (validateAgainstRegEx(apiForm.getDocumentNumber(), errors, FIELD_DOCUMENT_NUMBER,
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_NUMBERS_DASHES))
			{
				validateMaxLength(apiForm.getDocumentNumber(), errors, FIELD_DOCUMENT_NUMBER,
						TravelacceleratorstorefrontValidationConstants.MAX_LENGTH_30);
			}
		}
	}
}
