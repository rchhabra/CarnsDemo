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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.ancillary.accommodation.data.ConfiguredAccommodationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.ConfiguredAccommodationFacade;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;


/**
 * Default implementation of ConfiguredAccomodationFacade
 */
public class DefaultConfiguredAccommodationFacade implements ConfiguredAccommodationFacade
{

	private Converter<ConfiguredAccommodationModel, ConfiguredAccommodationData> configuredAccommodationConverter;
	private AccommodationMapService accommodationMapService;

	@Override
	public ConfiguredAccommodationData getAccommodation(final String accommodationUid)
	{
		final ConfiguredAccommodationModel accommodation = accommodationMapService.getAccommodation(accommodationUid);
		if (accommodation != null)
		{
			return configuredAccommodationConverter.convert(accommodation);
		}
		return null;
	}

	/**
	 * @return the configuredAccommodationConverter
	 */
	protected Converter<ConfiguredAccommodationModel, ConfiguredAccommodationData> getConfiguredAccommodationConverter()
	{
		return configuredAccommodationConverter;
	}

	/**
	 * @param configuredAccommodationConverter
	 *           the configuredAccommodationConverter to set
	 */
	public void setConfiguredAccommodationConverter(
			final Converter<ConfiguredAccommodationModel, ConfiguredAccommodationData> configuredAccommodationConverter)
	{
		this.configuredAccommodationConverter = configuredAccommodationConverter;
	}

	/**
	 * @return the accommodationMapService
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * @param accommodationMapService
	 *           the accommodationMapService to set
	 */
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

}
