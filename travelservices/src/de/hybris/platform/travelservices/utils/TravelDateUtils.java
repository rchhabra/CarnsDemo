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
*/

package de.hybris.platform.travelservices.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.support.CronSequenceGenerator;


/**
 * Class which provides Date utility methods
 */
public final class TravelDateUtils
{
	private static final Logger LOG = Logger.getLogger(TravelDateUtils.class);

	private static Map<String, DateTimeFormatter> formatterCache = new HashMap<>();
	private static Map<String, SimpleDateFormat> simpleDateformatterCache = new HashMap<>();
	private static Map<String, CronSequenceGenerator> cronSequenceGeneratorCache = new HashMap<>();

	private TravelDateUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Method takes a {@link String} dateTime and pattern and returns a {@link Date} object. The method will return null if parsing
	 * fails.
	 *
	 * @param date
	 * 		the {@link String} dateTime to be converted
	 * @param pattern
	 * 		the pattern
	 * @return {@link Date}
	 */
	public static Date convertStringDateToDate(final String date, final String pattern)
	{
		try
		{
			return Date.from(LocalDate.parse(date, getFormatter(pattern)).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		catch (final DateTimeParseException e)
		{
			LOG.error("Unable to parse String " + date + " to Date.");
			LOG.debug(e);
		}

		return null;
	}

	/**
	 * Method takes a {@link Date} and return a string representation based on the pattern.
	 *
	 * @param date
	 * 		the {@link Date} to be converted
	 * @param pattern
	 * 		the pattern
	 * @return {@link String}
	 */
	public static String convertDateToStringDate(final Date date, final String pattern)
	{
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(getFormatter(pattern));
	}

	/**
	 * Increase the hours in the {@link Date} by the specified amount
	 *
	 * @param date
	 * 		the {@link Date} object to increment by x hours
	 * @param hours
	 * 		the number of hours to increment by
	 * @return Date date
	 */
	public static Date addHours(final Date date, final int hours)
	{
		return org.apache.commons.lang.time.DateUtils.addHours(date, hours);
	}

	/**
	 * Check to see if two dates are the same
	 *
	 * @param date1
	 * 		the date to compare
	 * @param date2
	 * 		the date to be compared with
	 * @return true if dates are the same
	 */
	public static boolean isSameDate(final Date date1, final Date date2)
	{
		return org.apache.commons.lang.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * Method to increment the date by the given number of days
	 *
	 * @param date
	 * 		the date to increment
	 * @param amount
	 * 		the number of days to increment by
	 * @return incremented date
	 */
	public static Date addDays(final Date date, final int amount)
	{
		return org.apache.commons.lang.time.DateUtils.addDays(date, amount);
	}

	/**
	 * Method to increment the date by the given number of months
	 *
	 * @param date
	 * 		the date to increment
	 * @param amount
	 * 		the number of months to increment by
	 * @return incremented date
	 */
	public static Date addMonths(final Date date, final int amount)
	{
		return org.apache.commons.lang.time.DateUtils.addMonths(date, amount);
	}

	/**
	 * Method to find the difference in years between two dates
	 *
	 * @param startDate
	 * 		the start date
	 * @param endDate
	 * 		the end date
	 * @return difference in years
	 */
	public static long getYearsBetweenDates(final Date startDate, final Date endDate)
	{
		final LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.YEARS.between(localStartDate, localEndDate);
	}

	/**
	 * Method to get the ZoneDateTime with the UTC time-zone given a Date and its ZoneId.
	 *
	 * @param date
	 * 		the date to be converted in the UTC ZonedDateTime
	 * @param zoneId
	 * 		the zoneId of the date
	 * @return the ZonedDateTime with the UTC time-zone.
	 */
	public static ZonedDateTime getUtcZonedDateTime(final Date date, final ZoneId zoneId)
	{
		return getZonedDateTime(date, zoneId).withZoneSameInstant(ZoneOffset.UTC);
	}

	/**
	 * Method to get the ZonedDateTime object from a date and its zoneId.
	 *
	 * @param date
	 * 		the date to be converted in a ZonedDateTime
	 * @param zoneId
	 * 		the zoneId of the date
	 * @return the ZonedDateTime calculated from the date and its zoneId.
	 */
	public static ZonedDateTime getZonedDateTime(final Date date, final ZoneId zoneId)
	{
		final LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return ZonedDateTime.of(localDateTime, zoneId);
	}

	/**
	 * Check if date1 is before date2 based on the their UTC time.
	 *
	 * @param date1
	 * 		the date to compare
	 * @param zoneId1
	 * 		the zoneId of date1
	 * @param date2
	 * 		the date to be compared with
	 * @param zoneId2
	 * 		the zoneId of date2
	 * @return true if date1 is before date2 based on their UTC time, false otherwise.
	 */
	public static boolean isBefore(final Date date1, final ZoneId zoneId1, final Date date2, final ZoneId zoneId2)
	{
		final ZonedDateTime date1UtcTime = TravelDateUtils.getUtcZonedDateTime(date1, zoneId1);
		final ZonedDateTime date2UtcTime = TravelDateUtils.getUtcZonedDateTime(date2, zoneId2);
		return date1UtcTime.isBefore(date2UtcTime);
	}

	/**
	 * Check if date1 is after date2 based on the their UTC time.
	 *
	 * @param date1
	 * 		the date to compare
	 * @param zoneId1
	 * 		the zoneId of date1
	 * @param date2
	 * 		the date to be compared with
	 * @param zoneId2
	 * 		the zoneId of date2
	 * @return true if date1 is after date2 based on their UTC time, false otherwise.
	 */
	public static boolean isAfter(final Date date1, final ZoneId zoneId1, final Date date2, final ZoneId zoneId2)
	{
		final ZonedDateTime date1UtcTime = TravelDateUtils.getUtcZonedDateTime(date1, zoneId1);
		final ZonedDateTime date2UtcTime = TravelDateUtils.getUtcZonedDateTime(date2, zoneId2);
		return date1UtcTime.isAfter(date2UtcTime);
	}

	/**
	 * This method returns a cached DateTimeFormatter from formatters cache based on the given pattern if exists, if not a new
	 * DateTimeFormatter will be created, added to the cache and then returned.
	 *
	 * @param pattern
	 * 		the date pattern
	 * @return a DateTimeFormatter
	 */
	private static DateTimeFormatter getFormatter(final String pattern)
	{

		if (!formatterCache.containsKey(pattern))
		{
			formatterCache.put(pattern, DateTimeFormatter.ofPattern(pattern));
		}
		return formatterCache.get(pattern);
	}

	/**
	 * This method returns a cached SimpleDateFormat from formatters cache based on the given pattern if exists, if not a new
	 * SimpleDateFormat will be created, added to the cache and then returned.
	 *
	 * @param pattern
	 * 		the date pattern
	 * @return a SimpleDateFormat
	 */
	private static SimpleDateFormat getSimpleDateFormatter(final String pattern)
	{

		if (!simpleDateformatterCache.containsKey(pattern))
		{
			simpleDateformatterCache.put(pattern, new SimpleDateFormat(pattern));
		}
		return simpleDateformatterCache.get(pattern);
	}

	/**
	 * This method returns a cached CronSequenceGenerator from cronSequenceGenerators cache based on the given cronJobExpression if
	 * exists, if not a new CronSequenceGenerator will be created, added to the cache and then returned.
	 *
	 * @param cronJobExpression
	 * 		the cronJobExpression
	 * @return a CronSequenceGenerator
	 */

	private static CronSequenceGenerator getCronSequenceGenerator(final String cronJobExpression)
	{
		if (!cronSequenceGeneratorCache.containsKey(cronJobExpression))
		{
			try
			{
				final CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronJobExpression);
				cronSequenceGeneratorCache.put(cronJobExpression, cronSequenceGenerator);
			}
			catch (final IllegalArgumentException ex)
			{
				LOG.error("Invalid CronJobExpression\t:\t" + cronJobExpression);
				LOG.debug(ex);
				return null;
			}
		}

		return cronSequenceGeneratorCache.get(cronJobExpression);
	}


	/**
	 * Gets days between dates.
	 *
	 * @param startDate
	 * 		the start date
	 * @param endDate
	 * 		the end date
	 * @return the days between dates
	 */
	public static long getDaysBetweenDates(final Date startDate, final Date endDate)
	{
		final LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(localStartDate, localEndDate);
	}


	/**
	 * Sets hours, minutes and period in a date and returns the updated date
	 *
	 * @param date
	 * 		the date
	 * @param time
	 * 		the time
	 * @param pattern
	 * 		the pattern
	 * @return date with time
	 */
	public static Date getDateWithTime(final Date date, final String time, final String pattern)
	{
		final LocalDateTime targetDate = LocalDateTime.of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				LocalDateTime.parse(time, getFormatter(pattern)).toLocalTime());
		return Date.from(ZonedDateTime.of(targetDate, ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Returns a date object based the String, formatted according to the pattern.
	 *
	 * @param time
	 * 		the time
	 * @param pattern
	 * 		the pattern
	 * @return date
	 */
	public static Date getDate(final String date, final String pattern)
	{
		final SimpleDateFormat df = getSimpleDateFormatter(pattern);
		try
		{
			return df.parse(date);
		}
		catch (final ParseException e)
		{
			LOG.error("Unable to parse String " + date + " to Date");
			LOG.debug(e);
		}
		return null;

	}

	/**
	 * Returns a time based on Date, formatted according to the pattern.
	 *
	 * @param date
	 * 		the date with time
	 * @param pattern
	 * 		the pattern
	 * @return time
	 */
	public static String getTimeForDate(final Date date, final String pattern)
	{
		return getSimpleDateFormatter(pattern).format(date);
	}

	/**
	 * Returns a list of dates from 'fromDate' to 'toDate'(both dates excluded) based on cronJobExpression.
	 *
	 * @param cronJobExpression
	 * 		the cronJobExpression
	 * @param fromDate
	 * 		the date to be used as a starting point reference for calculation of valid dates
	 * @param toDate
	 * 		the date to be used as a ending point reference for calculation of valid dates
	 * @return time
	 */
	public static List<Date> getValidDates(final String cronJobExpression, final Date fromDate, final Date toDate)
	{
		Date startingDate = fromDate;
		if (StringUtils.isEmpty(cronJobExpression) || Objects.isNull(startingDate) || Objects.isNull(toDate)
				|| toDate.before(startingDate))
		{
			return Collections.emptyList();
		}

		final CronSequenceGenerator cronSequenceGenerator = getCronSequenceGenerator(cronJobExpression);

		if (Objects.isNull(cronSequenceGenerator))
		{
			return Collections.emptyList();
		}

		final List<Date> validDates = new ArrayList<>();
		Date validDate = cronSequenceGenerator.next(startingDate);
		while (validDate.before(toDate))
		{
			validDates.add(validDate);
			startingDate = validDate;
			validDate = cronSequenceGenerator.next(startingDate);
		}

		return validDates;
	}

	/**
	 * Returns true if the time component of two date corresponds, false otherwise
	 *
	 * @param departureTime1
	 * 		the first date to compare
	 * @param departureTime2
	 * 		the second date to compare
	 * @return
	 */
	public static boolean isSameTime(final Date departureTime1, final Date departureTime2)
	{
		final LocalTime localTime1 = LocalDateTime.ofInstant(departureTime1.toInstant(), ZoneId.systemDefault()).toLocalTime();
		final LocalTime localTime2 = LocalDateTime.ofInstant(departureTime2.toInstant(), ZoneId.systemDefault()).toLocalTime();

		return localTime1.equals(localTime2);
	}

	/**
	 * Returns true if date belongs to the interval (startDate, endDate)
	 *
	 * @param dateToCheck
	 * 		date to check
	 * @param startDate
	 * 		lower limit
	 * @param endDate
	 * 		upper limit
	 * @return
	 */
	public static boolean isBetweenDates(final Date dateToCheck, final Date startDate, final Date endDate)
	{
		final ZoneId currentZone = ZoneId.systemDefault();
		return isAfter(dateToCheck, currentZone, startDate, currentZone) && isBefore(dateToCheck, currentZone, endDate,
				currentZone);
	}

}
