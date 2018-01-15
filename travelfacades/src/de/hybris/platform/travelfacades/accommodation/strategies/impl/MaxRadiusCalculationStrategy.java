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

package de.hybris.platform.travelfacades.accommodation.strategies.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationRadiusCalculationStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
* Implementation of AccommodationMaxRadiusCalculationStrategy.
*/
public class MaxRadiusCalculationStrategy implements AccommodationRadiusCalculationStrategy
{
	private static final Logger LOG = Logger.getLogger(MaxRadiusCalculationStrategy.class);

	private ConfigurationService configurationService;
	private Double defaultRadius;

	@Override
	public Double calculateRadius(final List<String> placeTypes)
	{
		Double maxRadius = 0d;
		String radiusProp;
		for (final String placeType : placeTypes)
		{
			Double radius = 0d;
			radiusProp = TravelfacadesConstants.ACCOMMODATION_AUTOSUGGESTION_RADIUS + StringUtils.trim(placeType);
			try
			{
				radius = getConfigurationService().getConfiguration().getDouble(radiusProp);
			}
			catch (final NoSuchElementException ex)
			{
				LOG.debug("Radius not found for property :" + radiusProp, ex);
				radius = getDefaultRadius();
			}
			maxRadius = maxRadius > radius ? maxRadius : radius;
		}
		return maxRadius;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the defaultRadius
	 */
	protected Double getDefaultRadius()
	{
		return defaultRadius;
	}

	/**
	 * @param defaultRadius the defaultRadius to set
	 */
	@Required
	public void setDefaultRadius(final Double defaultRadius)
	{
		this.defaultRadius = defaultRadius;
	}
}
