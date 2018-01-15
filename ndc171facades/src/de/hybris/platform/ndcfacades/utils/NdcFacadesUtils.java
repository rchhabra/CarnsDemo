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
package de.hybris.platform.ndcfacades.utils;

import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Utility class for NDC Facades
 */
public class NdcFacadesUtils
{
	private static final Logger LOG = Logger.getLogger(NdcFacadesUtils.class);

	private static ConfigurationService configurationService;

	private NdcFacadesUtils()
	{

	}

	/**
	 * Date to time string string.
	 *
	 * @param arrivalTime
	 * 		the arrival time
	 *
	 * @return string
	 */
	public static String dateToTimeString(final Date arrivalTime)
	{
		final Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(arrivalTime);
		return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.format("%02d", calendar.get(Calendar.MINUTE));
	}

	/**
	 * Date to xml gregorian calendar xml gregorian calendar.
	 *
	 * @param date
	 * 		the date
	 *
	 * @return xml gregorian calendar
	 */
	public static XMLGregorianCalendar dateToXMLGregorianCalendar(final Date date)
	{
		final XMLGregorianCalendar xmlGregorianCalendar;

		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);

		try
		{
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.debug(e);
			throw new ConversionException(
					configurationService.getConfiguration().getString(NdcfacadesConstants.DATE_CONVERSION_ERROR));
		}

		return xmlGregorianCalendar;
	}

	/**
	 * Date to xml gregorian calendar xml gregorian calendar.
	 *
	 * @param date
	 *           the date
	 * @param zoneId
	 *           the zone id
	 * @return xml gregorian calendar
	 */
	public static XMLGregorianCalendar dateToXMLGregorianCalendar(final Date date, final ZoneId zoneId)
	{
		final XMLGregorianCalendar xmlGregorianCalendar;

		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		gregorianCalendar.setTimeZone(TimeZone.getTimeZone(zoneId));

		try
		{
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.debug(e);
			throw new ConversionException(
					configurationService.getConfiguration().getString(NdcfacadesConstants.DATE_CONVERSION_ERROR));
		}

		return xmlGregorianCalendar;
	}

	/**
	 * This method returns seat number from given {@link ListOfSeatType} by appending column and row number.
	 *
	 * @param seatType
	 *           the seat type
	 *
	 * @return the seat num
	 */
	public static String getSeatNum(final ListOfSeatType seatType)
	{
		return new StringBuilder(seatType.getLocation().getRow().getNumber().getValue()).append(seatType.getLocation().getColumn())
				.toString();
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
