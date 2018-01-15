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
package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates base information in {@link SearchParamsRAO} from {@link FareSearchRequestData}
 */
public class SearchParamsRaoBasicPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{
	private TimeService timeService;

	@Override
	public void populate(final FareSearchRequestData source, final SearchParamsRAO target)
			throws ConversionException
	{
		target.setTripType(source.getTripType());

		target.setSalesApplication(source.getSalesApplication().getCode());

		target.setSearchDate(getCurrentDay());
	}

	/**
	 * Gets current day removing the time.
	 *
	 * @return the current day
	 */
	protected Date getCurrentDay()
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getTimeService().getCurrentTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
