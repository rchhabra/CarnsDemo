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
package de.hybris.platform.travelbackofficeservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelbackofficeservices.dao.BackofficeScheduleConfigurationDao;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * The type Default backoffice schedule configuration dao.
 */
public class DefaultBackofficeScheduleConfigurationDao extends DefaultGenericDao<ScheduleConfigurationModel>
		implements BackofficeScheduleConfigurationDao
{
	public DefaultBackofficeScheduleConfigurationDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<ScheduleConfigurationModel> findScheduleConfiguration(final String number,
			final TravelProviderModel travelProvider)
	{
		validateParameterNotNull(number, "Transport Offering number must not be null!");
		validateParameterNotNull(travelProvider, "Travel Provider must not be null!");

		final Map<String, Object> params = new HashMap<>();
		params.put(ScheduleConfigurationModel.NUMBER, number);
		params.put(ScheduleConfigurationModel.TRAVELPROVIDER, travelProvider);

		final List<ScheduleConfigurationModel> searchResult = find(params);
		return CollectionUtils.isNotEmpty(searchResult) ? searchResult : Collections.emptyList();
	}

	@Override
	public List<ScheduleConfigurationModel> getAllScheduleConfigurations()
	{
		return find();
	}

	@Override
	public List<ScheduleConfigurationModel> findScheduleConfiguration(final String number,
			final TravelProviderModel travelProvider, final Date startDate, final Date endDate)
	{
		if (Objects.isNull(startDate) && Objects.isNull(endDate))
		{
			return findScheduleConfiguration(number, travelProvider);
		}

		final Map<String, Object> params = new HashMap<>();
		params.put(ScheduleConfigurationModel.NUMBER, number);
		params.put(ScheduleConfigurationModel.TRAVELPROVIDER, travelProvider);

		if (Objects.nonNull(startDate))
		{
			params.put(ScheduleConfigurationModel.STARTDATE, startDate);
		}

		if (Objects.nonNull(endDate))
		{
			params.put(ScheduleConfigurationModel.ENDDATE, endDate);
		}

		final List<ScheduleConfigurationModel> searchResult = find(params);
		return CollectionUtils.isNotEmpty(searchResult) ? searchResult : Collections.emptyList();
	}
}
