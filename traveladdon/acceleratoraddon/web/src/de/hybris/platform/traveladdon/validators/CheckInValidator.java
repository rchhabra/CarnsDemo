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
import de.hybris.platform.traveladdon.forms.CheckInForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("checkInValidator")
public class CheckInValidator extends TravelFormValidator implements Validator
{
	private static final String FIELD_INDEX_PREFIX = "apiFormList";
	private static final String FIELD_INDEX_OPEN_BRACKET = "[";
	private static final String FIELD_INDEX_CLOSE_BRACKET = "].";

	private static final String FIELD_DATE_OF_BIRTH = "dateOfBirth";
	private static final String FIELD_DOCUMENT_EXPIRY_DATE = "documentExpiryDate";
	private static final String FIELD_DOCUMENT_TYPE = "documentType";
	private static final String FIELD_COUNTRY_OF_ISSUE = "countryOfIssue";
	private static final String FIELD_DOCUMENT_NUMBER = "documentNumber";
	private static final String FIELD_NATIONALITY = "nationality";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CheckInValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{

		final CheckInForm checkInForm = (CheckInForm) object;

		for (int i = 0; i < checkInForm.getApiFormList().size(); i++)
		{
			validateAPIData(checkInForm.getApiFormList().get(i), errors, i);
		}
	}

	/**
	 * Validates the given apiForm and adding fields error to the list of validation errors
	 *
	 * @param apiForm
	 * @param errors
	 */
	protected void validateAPIData(final APIForm apiForm, final Errors errors, final int position)
	{
		if (!validateIsBlankField(apiForm.getDocumentType(), errors,
				getIndexedFieldName(position, FIELD_DOCUMENT_TYPE)))
		{
			validateDocumentType(apiForm.getDocumentType(), errors,
					getIndexedFieldName(position, FIELD_DOCUMENT_TYPE));

		}

		if (!validateIsBlankField(apiForm.getDocumentNumber(), errors,
				getIndexedFieldName(position, FIELD_DOCUMENT_NUMBER)))
		{
			if (validateAgainstRegEx(apiForm.getDocumentNumber(), errors,
					getIndexedFieldName(position, FIELD_DOCUMENT_NUMBER),
					TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_NUMBERS_DASHES))
			{
				validateMaxLength(apiForm.getDocumentNumber(), errors,
						getIndexedFieldName(position, FIELD_DOCUMENT_NUMBER),
						TravelacceleratorstorefrontValidationConstants.MAX_LENGTH_30);
			}
		}

		if (!validateIsBlankField(apiForm.getDateOfBirth(), errors,
				getIndexedFieldName(position, FIELD_DATE_OF_BIRTH)))
		{
			if (validateDateFormat(apiForm.getDateOfBirth(), errors,
					getIndexedFieldName(position, FIELD_DATE_OF_BIRTH)))
			{
				validateIsPastDate(apiForm.getDateOfBirth(), errors,
						getIndexedFieldName(position, FIELD_DATE_OF_BIRTH));
			}
		}

		if (!validateIsBlankField(apiForm.getDocumentExpiryDate(), errors,
				getIndexedFieldName(position, FIELD_DOCUMENT_EXPIRY_DATE)))
		{
			if (validateDateFormat(apiForm.getDocumentExpiryDate(), errors,
					getIndexedFieldName(position, FIELD_DOCUMENT_EXPIRY_DATE)))
			{
				validateIsFutureDate(apiForm.getDocumentExpiryDate(), errors,
						getIndexedFieldName(position, FIELD_DOCUMENT_EXPIRY_DATE));
			}
		}

		validateIsBlankField(apiForm.getNationality(), errors, getIndexedFieldName(position, FIELD_NATIONALITY));

		validateIsBlankField(apiForm.getCountryOfIssue(), errors,
				getIndexedFieldName(position, FIELD_COUNTRY_OF_ISSUE));

	}

	protected String getIndexedFieldName(final int arrayPosition, final String fieldName)
	{
		return FIELD_INDEX_PREFIX + FIELD_INDEX_OPEN_BRACKET + arrayPosition + FIELD_INDEX_CLOSE_BRACKET + fieldName;
	}

}
