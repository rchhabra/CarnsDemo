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

import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.traveladdon.forms.TransportOfferingStatusSearchForm;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * Custom validation for TransportOfferingStatusSearchForm
 *
 */

@Component("transportOfferingStatusSearchValidator")
public class TransportOfferingStatusSearchValidator implements Validator
{

	@Resource
	private TimeService timeService;

   private static final String TRANSPORT_OFFERING_NUMBER = "transportOfferingNumber";
   private static final String DEPARTURE_DATE = "departureDate";
   private static final int NUMBER_OF_DAYS = -1;

   private static final String TRANSPORT_OFFERING_NUMBER_PATTERN = "[^a-z0-9-]";
   private static final String DEPARTURE_DATE_PATTERN = "\\d{2}\\/\\d{2}\\/\\d{4}";

   private static final String TRANSPORT_OFFERING_NUMBER_INVALID_CHARACTER = "transport.offering.number.invalid.character";
   private static final String DEPARTURE_DATE_IN_PAST = "departure.date.in.past";
   private static final String DEPARTURE_DATE_INCORRECT_DATE = "departure.date.incorrect.date";
   private static final String DEPARTURE_DATE_INCORRECT_FORMAT = "departure.date.incorrect.format";


   @Override
   public boolean supports(final Class<?> aClass)
   {
      return TransportOfferingStatusSearchForm.class.equals(aClass);
   }

   @Override
   public void validate(final Object object, final Errors errors)
   {
      final TransportOfferingStatusSearchForm form = (TransportOfferingStatusSearchForm) object;

      validateTransportOfferingNumberField(form.getTransportOfferingNumber(), TRANSPORT_OFFERING_NUMBER, errors);

      validateDateFieldPattern(form.getDepartureDate(), DEPARTURE_DATE, errors);
      validateDateField(form.getDepartureDate(), DEPARTURE_DATE, errors);

   }

   /**
    * Method used to validate the pattern of the departureDate attribute
    *
    * @param departureDate
    *           as the string value of the attribute of the TransportOfferingStatusForm to validate
    * @param elementName
    *           as the name of the attribute to validate
    * @param errors
    *           the validation errors
    */
   private void validateDateFieldPattern(final String departureDate, final String elementName, final Errors errors)
   {

      if (errors.getFieldErrorCount(elementName) > 0)
      {
         return;
      }

      if (!departureDate.matches(DEPARTURE_DATE_PATTERN))
      {
         errors.rejectValue(elementName, DEPARTURE_DATE_INCORRECT_FORMAT);
      } else
      {
         final DateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
         dateFormat.setLenient(false);
         try
         {
            dateFormat.parse(departureDate);
         } catch (final ParseException e)
         {
            errors.rejectValue(elementName, DEPARTURE_DATE_INCORRECT_DATE);
         }
      }

   }

   /**
    * Method used to validate the departureDate attribute: a date is valid if it is not before the current day's previous day
    *
    * @param departureDateString
    *           as the string value of the attribute of the TransportOfferingStatusForm to validate
    * @param elementName
    *           as the name of the attribute to validate
    * @param errors
    *           the validation errors
    */
   private void validateDateField(final String departureDateString, final String elementName, final Errors errors)
   {

      if (errors.getFieldErrorCount(elementName) > 0)
      {
         return;
      }

		final Date currentDate = DateUtils.truncate(timeService.getCurrentTime(), Calendar.DAY_OF_MONTH);
      final Date targetDate = DateUtils.addDays(currentDate, NUMBER_OF_DAYS);

      final Date departureDate = TravelDateUtils.convertStringDateToDate(departureDateString,
               TravelservicesConstants.DATE_PATTERN);

      if (departureDate == null)
      {
         errors.rejectValue(elementName, DEPARTURE_DATE_INCORRECT_DATE);
         return;
      }

      if (departureDate.before(targetDate))
      {
         errors.rejectValue(elementName, DEPARTURE_DATE_IN_PAST);
      }

   }

   /**
    * Method used to validate the pattern of the transportOfferingNumber attribute. It must contain just letters, numbers or the
    * dash symbol
    *
    * @param number
    *           as the string value of the attribute of the TransportOfferingStatusForm to validate
    * @param elementName
    *           as the name of the attribute to validate
    * @param errors
    *           the validation errors
    */
   private void validateTransportOfferingNumberField(final String number, final String elementName, final Errors errors)
   {

      if (errors.getFieldErrorCount(elementName) > 0)
      {
         return;
      }

      final Pattern pattern = Pattern.compile(TRANSPORT_OFFERING_NUMBER_PATTERN, Pattern.CASE_INSENSITIVE);
      final Matcher matcher = pattern.matcher(number);
      if (matcher.find())
      {
         errors.rejectValue(elementName, TRANSPORT_OFFERING_NUMBER_INVALID_CHARACTER);
      }

   }

}
