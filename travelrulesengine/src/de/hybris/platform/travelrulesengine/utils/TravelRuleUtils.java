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

package de.hybris.platform.travelrulesengine.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;


/**
 * Utility funcitons to help evaluating some rule related functions.
 */
public class TravelRuleUtils
{

	/**
	 * The constant DATE_PATTERN.
	 */
	public static final String DATE_PATTERN = "dd/MM/yyyy";

	private TravelRuleUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Method to find the difference in days between two dates
	 *
	 * @param startDate
	 * 		the start date
	 * @param endDate
	 * 		the end date
	 * @return days between dates
	 */
	public static long getDaysBetweenDates(final Date startDate, final Date endDate)
	{
		final LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(localStartDate, localEndDate);
	}

	/**
	 * Method to find if the input date is between start and end dates
	 *
	 * @param date
	 * 		the date
	 * @param startDate
	 * 		the start date
	 * @param endDate
	 * 		the end date
	 * @return boolean
	 */
	public static boolean isDateBetweenDates(final Date date, final String startDate, final String endDate)
	{
		final Date formattedStartDate = Date
				.from(LocalDate.parse(startDate, DateTimeFormatter.ofPattern(DATE_PATTERN)).atStartOfDay(ZoneId.systemDefault())
						.toInstant());
		final Date formattedEndDate = Date
				.from(LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DATE_PATTERN)).atTime(LocalTime.MAX)
						.atZone(ZoneId.systemDefault()).toInstant());
		return (date.after(formattedStartDate) && date.before(formattedEndDate));
	}

	/**
	 * Method to find if the given date falls on weekend
	 *
	 * @param date
	 * 		the date
	 * @return boolean
	 */
	public static boolean isDateAWeekend(final Date date)
	{
		final LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY)
		{
			return true;
		}
		return false;
	}

	/**
	 * Gets hour.
	 *
	 * @param date
	 * 		the date
	 * @return the hour
	 */
	public static long getHour(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

}
