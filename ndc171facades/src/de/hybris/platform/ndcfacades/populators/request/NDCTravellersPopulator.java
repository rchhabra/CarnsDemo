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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * The NDC Travellers Populator for {@link AirShoppingRQ}
 * Create a list of PassengerTypeQuantityData based on the AnonymousTraveler information
 */
public class NDCTravellersPopulator implements Populator<AirShoppingRQ, FareSearchRequestData>
{
	private NDCPassengerTypeService passengerTypeService;
	private ConfigurationService configurationService;
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		final List<PassengerTypeQuantityData> passengerTypeList = new ArrayList<>();

		if (Objects.nonNull(airShoppingRQ.getDataLists()) && Objects.nonNull(airShoppingRQ.getDataLists().getPassengerList())
				&& CollectionUtils.isNotEmpty(airShoppingRQ.getDataLists().getPassengerList().getPassenger()))
		{
			final List<PassengerType> passengers = airShoppingRQ.getDataLists().getPassengerList().getPassenger().stream()
					.filter(passenger -> Objects.nonNull(passenger)).collect(Collectors.toList());

			final Map<String, Long> passengersTypeCount = passengers.stream().map(PassengerType::getPTC).collect(Collectors.toList())
					.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

			passengersTypeCount.forEach((ptc, count) -> {
				final PassengerTypeModel passengerTypeModel = getPassengerTypeService().getPassengerType(ptc);

				if (passengerTypeModel == null)
				{
					throw new ConversionException(
							getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_PASSENGER_TYPE));
				}
				final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
				final PassengerTypeData passengerTypeData = getPassengerTypeConverter().convert(passengerTypeModel);

				passengerTypeQuantityData.setPassengerType(passengerTypeData);
				passengerTypeQuantityData.setQuantity(count.intValue());
				passengerTypeList.add(passengerTypeQuantityData);
			});
		}

		fareSearchRequestData.setPassengerTypes(passengerTypeList);
	}

	/**
	 * Gets passenger type converter.
	 *
	 * @return the passenger type converter
	 */
	protected Converter<PassengerTypeModel, PassengerTypeData> getPassengerTypeConverter()
	{
		return passengerTypeConverter;
	}

	/**
	 * Sets passenger type converter.
	 *
	 * @param passengerTypeConverter
	 * 		the passenger type converter
	 */
	@Required
	public void setPassengerTypeConverter(
			final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
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
}
