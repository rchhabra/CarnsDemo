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
package de.hybris.platform.traveladdon.validators;


import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.DocumentType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;

/**
 * Abstract form validator class containing common methods to validate travel specific forms
 *
 * */
public abstract class TravelFormValidator
{
	private static final Logger LOGGER = Logger.getLogger(APIFormValidator.class);

	private static final String ERROR_TYPE_INVALID_DATE_FORMAT = "InvalidDateFormat";
	private static final String ERROR_TYPE_INVALID_DATE = "InvalidDate";
	private static final String ERROR_TYPE_EMPTY_FIELD = "EmptyField";
	private static final String ERROR_INVALID_DOCUMENT_TYPE = "InvalidDocumentType";
	private static final String ERROR_PATTERN = "Pattern";
	private static final String ERROR_MAX_LENGTH = "Max";

	/**
	 * Method responsible for validating the date format
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	public boolean validateDateFormat(final String date, final Errors errors, final String field)
	{

		if (StringUtils.isNotEmpty(date))
		{
			if (!date.matches(TravelacceleratorstorefrontValidationConstants.REG_EX_DD_MM_YYY_WITH_FORWARD_SLASH))
			{
				rejectValue(errors, field, ERROR_TYPE_INVALID_DATE_FORMAT);
			}
			else
			{
				final DateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
				dateFormat.setLenient(false);
				try
				{
					dateFormat.parse(date);
					return true;
				}
				catch (final ParseException e)
				{
					rejectValue(errors, field, ERROR_TYPE_INVALID_DATE);
				}
			}
		}

		return false;
	}

	/**
	 * Method returns true if the date is in the past. Date is evaluated on the current date.
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	public boolean validateIsPastDate(final String date, final Errors errors, final String field)
	{
		final Date d = TravelDateUtils.convertStringDateToDate(date, TravelservicesConstants.DATE_PATTERN);

		if (d.before(Calendar.getInstance().getTime()))
		{
			return true;
		}

		rejectValue(errors, field, ERROR_TYPE_INVALID_DATE);
		return false;
	}

	/**
	 * Method returns true if the date is in the future. Date is evaluated on the current date.
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	public boolean validateIsFutureDate(final String date, final Errors errors, final String field)
	{
		final Date d = TravelDateUtils.convertStringDateToDate(date, TravelservicesConstants.DATE_PATTERN);

		if (d.after(Calendar.getInstance().getTime()))
		{
			return true;
		}

		rejectValue(errors, field, ERROR_TYPE_INVALID_DATE);
		return false;
	}

	/**
	 * Method to check if the value of the selected field either empty, null or contains only white spaces and if so it
	 * will set a EmptyField error against the field
	 *
	 * @param fieldValue
	 * @param errors
	 * @param fieldName
	 * @return
	 */
	public boolean validateIsBlankField(final String fieldValue, final Errors errors, final String fieldName)
	{
		if (StringUtils.isBlank(fieldValue))
		{
			rejectValue(errors, fieldName, ERROR_TYPE_EMPTY_FIELD);
			return true;
		}
		return false;
	}

	/**
	 * Validates the documentType provided with the DocumentType ENUM. If the documentType provided is not a valid
	 * DocumentType, an error will be added to the list of validation errors
	 *
	 * @param documentType
	 * @param errors
	 * @param attributeName
	 * @return
	 */
	public boolean validateDocumentType(final String documentType, final Errors errors, final String attributeName)
	{
		if (StringUtils.isNotBlank(documentType))
		{
			try
			{
				DocumentType.valueOf(documentType);
				return true;
			}
			catch (final IllegalArgumentException e)
			{
				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("DocumentType value provided: " + documentType + " is not a valid DocumentType ENUM code");
				}
				rejectValue(errors, attributeName, ERROR_INVALID_DOCUMENT_TYPE);
				return false;
			}
		}
		return false;
	}

	/**
	 * Method to validate value against a given expression. Method will return false if regex fails and true if regex
	 * passes.
	 *
	 * @param value
	 * @param errors
	 * @param attributeName
	 * @param expression
	 * @return
	 */
	public boolean validateAgainstRegEx(final String value, final Errors errors, final String attributeName,
			final String expression)
	{
		if (!value.matches(expression))
		{
			rejectValue(errors, attributeName, ERROR_PATTERN);
			return false;
		}
		return true;
	}

	/**
	 * Method checks to see if the value exceeds the maxLength. Method returns true if it does and false if it doesn't.
	 *
	 * @param value
	 * @param errors
	 * @param attributeName
	 * @param maxLength
	 * @return
	 */
	public boolean validateMaxLength(final String value, final Errors errors, final String attributeName,
			final int maxLength)
	{
		if (StringUtils.length(value) > maxLength)
		{
			rejectValue(errors, attributeName, ERROR_MAX_LENGTH, new String[]
					{ String.valueOf(maxLength) });
			return true;
		}
		return false;
	}

	/**
	 * Method to add errors
	 *
	 * @param errors
	 * @param attributeName
	 * @param errorCode
	 */
	public void rejectValue(final Errors errors, final String attributeName, final String errorCode)
	{
		errors.rejectValue(attributeName, errorCode);
	}

	/**
	 * Method to add errors with parameters
	 *
	 * @param errors
	 * @param attributeName
	 * @param errorCode
	 */
	public void rejectValue(final Errors errors, final String attributeName, final String errorCode, final String[] params)
	{
		errors.rejectValue(attributeName, errorCode, params, null);
	}
}
