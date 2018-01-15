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

package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Required;

/**
 * An abstract class to populates NDC travelers for {@link ServiceListRQ} and {@link BaggageListRQ}
 */
public abstract class NDCRQTravellersPopulator
{
	private NDCPassengerTypeService passengerTypeService;
	private ConfigurationService configurationService;
	private Converter<PassengerTypeModel, TravellerData> passengerToTravellerConverter;

	/**
	 * Gets travellers from ndcrq.
	 *
	 * @param passengerList
	 *           the passengerList
	 *
	 * @return the travellers from ndcrq
	 */
	protected List<TravellerData> getTravellersFromNDCRQ(final List<PassengerType> passengers)
	{
		final List<TravellerData> travellers = new ArrayList<>();
		final Map<String, Long> passengerTypeCountMap = passengers.stream()
				.collect(Collectors.groupingBy(PassengerType::getPTC, Collectors.counting()));

		passengerTypeCountMap.forEach((ptc, count) -> {
			final PassengerTypeModel passengerTypeModel = getPassengerTypeService().getPassengerType(ptc);

			if (passengerTypeModel == null)
			{
				throw new ConversionException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_PASSENGER_TYPE));
			}

			LongStream.rangeClosed(1, count).forEach(index -> {
				final TravellerData travellerData = new TravellerData();
				//convert PassengerTypeModel to TravellerData
				getPassengerToTravellerConverter().convert(passengerTypeModel, travellerData);
				travellers.add(travellerData);
			});
		});
		return travellers;
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passenger type service
	 */
	protected NDCPassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 * 		the passenger type service
	 */
	@Required
	public void setPassengerTypeService(final NDCPassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
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

	/**
	 * Gets passenger to traveller converter.
	 *
	 * @return the passenger to traveller converter
	 */
	protected Converter<PassengerTypeModel, TravellerData> getPassengerToTravellerConverter()
	{
		return passengerToTravellerConverter;
	}

	/**
	 * Sets passenger to traveller converter.
	 *
	 * @param passengerToTravellerConverter
	 * 		the passenger to traveller converter
	 */
	@Required
	public void setPassengerToTravellerConverter(final Converter<PassengerTypeModel, TravellerData> passengerToTravellerConverter)
	{
		this.passengerToTravellerConverter = passengerToTravellerConverter;
	}

}
