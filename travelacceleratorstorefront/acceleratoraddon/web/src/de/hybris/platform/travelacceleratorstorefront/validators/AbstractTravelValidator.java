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

package de.hybris.platform.travelacceleratorstorefront.validators;

import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public abstract class AbstractTravelValidator implements Validator
{

	private static final Logger LOGGER = Logger.getLogger(AbstractTravelValidator.class);

	private static final String FIELD_INVALID_NUMBER_OF_PASSENGERS = "InvalidNumberOfPassengers";
	private static final String FIELD_ROOMSTAYCANDIDATES = "roomStayCandidates";
	private static final String TRANSPORT_ACCOMMODATION_SUFFIX = ".transport.accommodation";
	private static final String ACCOMMODATION_SUFFIX = ".accommodation";
	private static final String PACKAGE_SUFFIX = ".package";

	@Resource(name = "timeService")
	private TimeService timeService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	private String targetForm;

	private String attributePrefix;

	/**
	 * Method to check if the value of the selected field either empty, null or contains only white spaces and if so it
	 * will set a EmptyField error against the field
	 *
	 * @param fieldName
	 * @param fieldValue
	 * @param errors
	 */
	protected Boolean validateBlankField(final String fieldName, final String fieldValue, final Errors errors)
	{
		if (StringUtils.isBlank(fieldValue))
		{
			rejectValue(errors, fieldName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_EMPTY_FIELD);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Method responsible for validating the date format
	 *
	 * @param date
	 * @param errors
	 * @param field
	 * @return
	 */
	protected Boolean validateDateFormat(final String date, final Errors errors, final String field)
	{

		if (StringUtils.isNotEmpty(date))
		{

			if (!date.matches(TravelacceleratorstorefrontValidationConstants.REG_EX_DD_MM_YYY_WITH_FORWARD_SLASH))
			{
				rejectValue(errors, field, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_DATE_FORMAT);
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
					rejectValue(errors, field, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_DATE);
				}
			}
		}
		return false;
	}

	/**
	 * Method check to see if date is a past date and if so sets a PastDate errors for the field
	 *
	 * @param date
	 * @param errors
	 * @param field
	 */
	protected void validateForPastDate(final String date, final Errors errors, final String field)
	{
		final Date selectedDate = TravelDateUtils.convertStringDateToDate(date, TravelservicesConstants.DATE_PATTERN);
		final Date currentDate = TravelDateUtils.convertStringDateToDate(
				TravelDateUtils.convertDateToStringDate(timeService.getCurrentTime(), TravelservicesConstants.DATE_PATTERN),
				TravelservicesConstants.DATE_PATTERN);

		final boolean pastDate = selectedDate.before(currentDate);

		if (pastDate)
		{
			rejectValue(errors, field, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_PAST_DATE);
		}
	}

	protected void validateDates(final String startDate, final String startDateFieldName, final String endDate,
			final String endDateFieldName, final Errors errors)
	{
		boolean isNotBlank = validateBlankField(startDateFieldName, startDate, errors);

		boolean isCheckInDateCorrectlyFormatted = false;
		boolean isCheckOutDateCorrectlyFormatted = false;

		if (isNotBlank)
		{
			isCheckInDateCorrectlyFormatted = validateDateFormat(startDate, errors, startDateFieldName);

			if (isCheckInDateCorrectlyFormatted)
			{
				validateForPastDate(startDate, errors, startDateFieldName);
			}
		}

		isNotBlank = validateBlankField(endDateFieldName, endDate, errors);
		if (isNotBlank)
		{
			isCheckOutDateCorrectlyFormatted = validateDateFormat(endDate, errors, endDateFieldName);
		}

		if (isCheckInDateCorrectlyFormatted && isCheckOutDateCorrectlyFormatted)
		{
			validateCheckInAndCheckOutDateTimeRange(startDate, startDateFieldName, endDate, endDateFieldName, errors);
		}

	}

	/**
	 * Method used to validate Check In and Check Out dates
	 *
	 * @param startDate
	 * @param startDateFieldName
	 * @param endDate
	 * @param endDateFieldName
	 * @param errors
	 */
	protected void validateCheckInAndCheckOutDateTimeRange(final String startDate, final String startDateFieldName,
			final String endDate, final String endDateFieldName, final Errors errors)
	{
		try
		{
			final SimpleDateFormat formatter = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);

			final Date CheckInDate = formatter.parse(startDate);
			final Date CheckOutDate = formatter.parse(endDate);
			final long dateDifference = TravelDateUtils.getDaysBetweenDates(CheckInDate, CheckOutDate);
			final long maxAllowedDateDifference = configurationService.getConfiguration()
					.getInt(TravelacceleratorstorefrontWebConstants.MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE);
			if (CheckInDate.after(CheckOutDate) || CheckInDate.equals(CheckOutDate) || dateDifference > maxAllowedDateDifference)
			{
				rejectValue(errors, startDateFieldName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_OUT_OF_RANGE);
			}
		}
		catch (final ParseException e)
		{
			LOGGER.error("Unable to parse String data to Date object:", e);
		}
	}

	/**
	 * @param numberOfRooms
	 * @param roomStayCandidates
	 * @param errors
	 */
	public void validateGuestsQuantity(final String numberOfRooms, final List<RoomStayCandidateData> roomStayCandidates,
			final Errors errors)
	{
		int roomNumber = 0;
		try
		{
			//first validate number of accommodations
			final int selectedAccommodationQuantity = Integer.parseInt(numberOfRooms);
			if (selectedAccommodationQuantity > configurationService.getConfiguration()
					.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY))
			{
				rejectValue(errors, TravelacceleratorstorefrontValidationConstants.NUMBER_OF_ROOMS,
						TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_QUANTITY);
				return;
			}

			final String sessionBookingJourney = sessionService
					.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
			final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
			int collectiveNumberOfGuests = 0;
			for (int i = 0; i < selectedAccommodationQuantity; i++)
			{
				if (CollectionUtils.isEmpty(roomStayCandidates))
				{
					return;
				}
				roomNumber = i;
				int adultPassengers = 0;
				int nonAdultPassengers = 0;
				String attributeName = null;
				for (int j = 0; j < roomStayCandidates.get(i).getPassengerTypeQuantityList().size(); j++)
				{
					attributeName = "roomStayCandidates[" + i + "].passengerTypeQuantityList[" + j + "].quantity";
					if (StringUtils.isNotEmpty(getAttributePrefix()))
					{
						attributeName = getAttributePrefix() + "." + attributeName;
					}
					final int selectedGuestCount = roomStayCandidates.get(i).getPassengerTypeQuantityList().get(j).getQuantity();
					final String selectedGuestCode = roomStayCandidates.get(i).getPassengerTypeQuantityList().get(j).getPassengerType()
							.getCode();

					if (selectedGuestCount > maxGuestQuantity)
					{
						errors.rejectValue(attributeName, "InvalidQuantity" + "." + getTargetForm() + "." + selectedGuestCode);
					}

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

					collectiveNumberOfGuests = collectiveNumberOfGuests + selectedGuestCount;

				}

				attributeName = "roomStayCandidates[" + i + "].passengerTypeQuantityList[" + 0 + "].quantity";
				if (StringUtils.isNotEmpty(getAttributePrefix()))
				{
					attributeName = getAttributePrefix() + "." + attributeName;
				}
				if (nonAdultPassengers >= 0 && adultPassengers >= 0)
				{
					if (nonAdultPassengers > 0 && adultPassengers == 0)
					{
						errors.rejectValue(attributeName,
								TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_NUMBER_OF_ADULTS);
						continue;
					}

					if (nonAdultPassengers + adultPassengers == 0)
					{
						errors.rejectValue(attributeName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_NO_PASSENGER);
						continue;
					}

					if (collectiveNumberOfGuests > maxGuestQuantity)
					{
						if (StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION))
						{
							rejectValue(errors, FIELD_ROOMSTAYCANDIDATES, FIELD_INVALID_NUMBER_OF_PASSENGERS + TRANSPORT_ACCOMMODATION_SUFFIX);
						}
						else if (StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY))
						{
							rejectValue(errors, FIELD_ROOMSTAYCANDIDATES, FIELD_INVALID_NUMBER_OF_PASSENGERS + ACCOMMODATION_SUFFIX);
						}
						else
						{
							rejectValue(errors, FIELD_ROOMSTAYCANDIDATES, FIELD_INVALID_NUMBER_OF_PASSENGERS + PACKAGE_SUFFIX);
						}
					}

				}
			}
		}
		catch (final IndexOutOfBoundsException | NumberFormatException e)
		{
			LOGGER.debug("No Guest List Found For Room Number " + roomNumber);
			rejectValue(errors, TravelacceleratorstorefrontValidationConstants.NUMBER_OF_ROOMS,
					TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_INVALID_QUANTITY);
		}
	}

	/**
	 * Method to check if the value of the selected field either empty, null and if so it will set a EmptyField error
	 * against the field
	 *
	 * @param fieldName
	 * @param listObject
	 * @param errors
	 */
	protected Boolean validateEmptyField(final String fieldName, final Collection listObject, final Errors errors)
	{
		if (CollectionUtils.isEmpty(listObject))
		{
			rejectValue(errors, fieldName, TravelacceleratorstorefrontValidationConstants.ERROR_TYPE_EMPTY_FIELD);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}


	/**
	 * Method to add errors
	 *
	 * @param errors
	 * @param attributeName
	 * @param errorCode
	 */
	protected void rejectValue(final Errors errors, String attributeName, final String errorCode)
	{
		final String errorMessageCode = errorCode + "." + getTargetForm() + "." + attributeName;
		if (StringUtils.isNotEmpty(getAttributePrefix()))
		{
			attributeName = getAttributePrefix() + "." + attributeName;
		}
		errors.rejectValue(attributeName, errorMessageCode);
	}

	protected String getTargetForm()
	{
		return targetForm;
	}

	public void setTargetForm(final String targetForm)
	{
		this.targetForm = targetForm;
	}

	/**
	 * @return the attributePrefix
	 */
	public String getAttributePrefix()
	{
		return attributePrefix;
	}

	/**
	 * @param attributePrefix
	 * 		the attributePrefix to set
	 */
	public void setAttributePrefix(final String attributePrefix)
	{
		this.attributePrefix = attributePrefix;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 * 		the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
