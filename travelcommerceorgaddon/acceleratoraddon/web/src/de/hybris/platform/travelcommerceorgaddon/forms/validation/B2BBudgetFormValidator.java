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
package de.hybris.platform.travelcommerceorgaddon.forms.validation;

import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.travelcommerceorgaddon.forms.B2BBudgetForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("b2BBudgetFormValidator")
@Scope("tenant")
public class B2BBudgetFormValidator implements Validator
{
	@Resource(name = "formatFactory")
	private FormatFactory formatFactory;

	private static final String BUDGET = "budget";
	private static final String STARTDATE = "startDate";
	private static final String ENDDATE = "endDate";
	private static final String REQUIRED = "general.required";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return B2BBudgetForm.class.equals(aClass);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final B2BBudgetForm form = (B2BBudgetForm) object;

		final String budget = form.getBudget();
		if (StringUtils.isBlank(budget))
		{
			errors.rejectValue(BUDGET, REQUIRED);
		}
		else
		{
			final Number budgetNumber;
			try
			{
				budgetNumber = getFormatFactory().createNumberFormat().parse(budget);
				if (budgetNumber.doubleValue() < 0D)
				{
					errors.rejectValue(BUDGET, "text.company.manageBudgets.budget.invalid");
				}
			}
			catch (final ParseException e)
			{
				errors.rejectValue(BUDGET, "text.company.manageBudgets.budget.invalid");
			}
		}

		validateDateFormat(form.getStartDate(), errors, STARTDATE);
		validateDateFormat(form.getEndDate(), errors, ENDDATE);
	}

	/**
	 * Method responsible for validating the date format
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	protected void validateDateFormat(final String date, final Errors errors, final String field)
	{
		if (StringUtils.isNotEmpty(date))
		{
			if (!date.matches(TravelacceleratorstorefrontValidationConstants.REG_EX_DD_MM_YYY_WITH_FORWARD_SLASH))
			{
				errors.rejectValue(field, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_DATE_FORMAT);
			}
			else
			{
				final DateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
				dateFormat.setLenient(false);
				try
				{
					dateFormat.parse(date);
				}
				catch (final ParseException e)
				{
					errors.rejectValue(field, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_DATE);
				}
			}
		}
		else
		{
			errors.rejectValue(field, REQUIRED);
		}
	}

	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

	public void setFormatFactory(final FormatFactory formatFactory)
	{
		this.formatFactory = formatFactory;
	}
}
