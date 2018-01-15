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
package de.hybris.platform.travelbackofficeservices.services;

import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;

import java.util.Date;
import java.util.List;


/**
 * The interface Backoffice schedule configuration service.
 */
public interface BackofficeScheduleConfigurationService
{
	/**
	 * Returns the ScheduleConfigurationModel for the number and travelProvider model.
	 *
	 * @param number
	 * 		the number
	 * @param travelProvider
	 * 		the travel provider
	 * @return the schedule configuration model
	 * @travelProvider
	 */
	List<ScheduleConfigurationModel> getScheduleConfigurationModel(String number, TravelProviderModel travelProvider);

	/**
	 * Returns all the {@link ScheduleConfigurationModel} in the system
	 * @return the list of schedule configurations
	 */
	List<ScheduleConfigurationModel> getAllScheduleConfigurations();

	/**
	 * Gets the schedule configuration model.
	 *
	 * @param number
	 *           the number
	 * @param travelProvider
	 *           the travel provider
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the schedule configuration model
	 */
	List<ScheduleConfigurationModel> getScheduleConfigurationModel(String number, TravelProviderModel travelProvider,
			Date startDate, Date endDate);

}
