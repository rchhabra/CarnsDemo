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
package de.hybris.platform.travelbackofficeservices.dao;

import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;

import java.util.Date;
import java.util.List;


/**
 * The interface Backoffice schedule configuration dao.
 */
public interface BackofficeScheduleConfigurationDao
{
	/**
	 * Finds the ScheduleConfigurationModel for the number and travel Provider model.
	 *
	 * @param number
	 * 		the number
	 * @param travelProvider
	 * 		the travel provider
	 * @return the list
	 * @travelProvider
	 */
	List<ScheduleConfigurationModel> findScheduleConfiguration(String number, TravelProviderModel travelProvider);

	/**
	 * Returns all the {@link ScheduleConfigurationModel} in the system
	 * @return the list of schedule configurations
	 */
	List<ScheduleConfigurationModel> getAllScheduleConfigurations();

	/**
	 * Find schedule configuration.
	 *
	 * @param number
	 *           the number
	 * @param travelProvider
	 *           the travel provider
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the list
	 */
	List<ScheduleConfigurationModel> findScheduleConfiguration(String number, TravelProviderModel travelProvider, Date startDate,
			Date endDate);
}
