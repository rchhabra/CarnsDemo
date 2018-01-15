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
package de.hybris.platform.travelbackofficeservices.services.impl;

import de.hybris.platform.travelbackofficeservices.dao.BackofficeScheduleConfigurationDao;
import de.hybris.platform.travelbackofficeservices.services.BackofficeScheduleConfigurationService;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default backoffice schedule configuration service.
 */
public class DefaultBackofficeScheduleConfigurationService implements BackofficeScheduleConfigurationService
{

	private BackofficeScheduleConfigurationDao backofficeScheduleConfigurationDao;

	@Override
	public List<ScheduleConfigurationModel> getScheduleConfigurationModel(final String number,
			final TravelProviderModel travelProvider)
	{
		return getBackofficeScheduleConfigurationDao().findScheduleConfiguration(number, travelProvider);
	}

	@Override
	public List<ScheduleConfigurationModel> getAllScheduleConfigurations()
	{
		return getBackofficeScheduleConfigurationDao().getAllScheduleConfigurations();
	}

	@Override
	public List<ScheduleConfigurationModel> getScheduleConfigurationModel(final String number,
			final TravelProviderModel travelProvider, final Date startDate, final Date endDate)
	{
		return getBackofficeScheduleConfigurationDao().findScheduleConfiguration(number, travelProvider, startDate, endDate);
	}

	/**
	 * Gets backoffice schedule configuration dao.
	 *
	 * @return the backoffice schedule configuration dao
	 */
	public BackofficeScheduleConfigurationDao getBackofficeScheduleConfigurationDao()
	{
		return backofficeScheduleConfigurationDao;
	}

	/**
	 * Sets backoffice schedule configuration dao.
	 *
	 * @param backofficeScheduleConfigurationDao
	 * 		the backoffice schedule configuration dao
	 */
	@Required
	public void setBackofficeScheduleConfigurationDao(
			final BackofficeScheduleConfigurationDao backofficeScheduleConfigurationDao)
	{
		this.backofficeScheduleConfigurationDao = backofficeScheduleConfigurationDao;
	}
}
