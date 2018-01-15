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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link PropertyHandler} interface. Handler is responsible to set ratePlanconfigs on
 * the {@link PropertyData}
 */
public class PropertyDataRatePlanConfigsHandler extends AbstractDefaultPropertyHandler implements PropertyHandler
{
	private ConfigurationService configurationService;

	@Override
	public void handle(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationSearchRequest, final PropertyData propertyData)
	{
		if (CollectionUtils.size(accommodationSearchRequest.getCriterion().getRoomStayCandidates()) != CollectionUtils
				.size(dayRatesForRoomStayCandidate.entrySet()))
		{
			return;
		}

		handlingAttributes(dayRatesForRoomStayCandidate, propertyData);
	}

	@Override
	protected void handlingAttributes(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final PropertyData propertyData)
	{
		final List<String> ratePlanConfigs = new ArrayList<>();
		int count = 0;
		for (final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> entry : dayRatesForRoomStayCandidate.entrySet())
		{
			if (CollectionUtils.isEmpty(entry.getValue().get(0).getRatePlanConfigs()))
			{
				return;
			}
			for (final String ratePlanConfig : entry.getValue().get(0).getRatePlanConfigs())
			{
				final int quantity = Integer.parseInt(ratePlanConfig.split("\\|", 3)[2]);
				count += quantity;
			}
			ratePlanConfigs.addAll(entry.getValue().get(0).getRatePlanConfigs());
		}

		final int maxBookingAllowed = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		if (count > maxBookingAllowed)
		{
			return;
		}

		propertyData.setRatePlanConfigs(ratePlanConfigs);
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
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
