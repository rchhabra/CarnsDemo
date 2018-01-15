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

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeQuantityRAO;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeRAO;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Passenger type quantity cart rao populator.
 */
public class PassengerTypeQuantityCartRaoPopulator implements Populator<AbstractOrderModel, CartRAO>
{
	private Converter<PassengerTypeData, PassengerTypeRAO> passengerTypeRaoConverter;
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Override
	public void populate(final AbstractOrderModel source, final CartRAO target)
			throws ConversionException
	{
		final List<PassengerTypeQuantityRAO> passengerTypeQuantities = new ArrayList<>();

		if(CollectionUtils.isEmpty(source.getEntries()))
		{
			return;
		}

		final Set<TravellerModel> travellerModels = source.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream())
				.collect(Collectors.toSet());

		if (CollectionUtils.isEmpty(travellerModels))
		{
			return;
		}

		passengerTypeQuantities.addAll(createPassengerTypeQuantityRAO(travellerModels));

		target.setPassengerTypeQuantities(passengerTypeQuantities);
	}


	/**
	 * Create passenger type quantity rao passenger type quantity rao.
	 *
	 * @param travellerModels
	 * 		the traveller models
	 * @return the passenger type quantity rao
	 */
	protected List<PassengerTypeQuantityRAO> createPassengerTypeQuantityRAO(final Set<TravellerModel> travellerModels)
	{
		final List<PassengerTypeQuantityRAO> passengerTypeQuantityRAOs = new LinkedList<>();

		final Map<PassengerTypeModel, Integer> passengerTypeQuantities = travellerModels.stream()
				.filter(traveller -> Objects.nonNull(traveller.getInfo()) && traveller.getInfo() instanceof PassengerInformationModel)
				.collect(Collectors.toMap(traveller -> ((PassengerInformationModel) traveller.getInfo()).getPassengerType(), x -> 1,
						(oldValue, newValue) -> oldValue + 1));

		for (final Entry<PassengerTypeModel, Integer> passengerTypeQuantity : passengerTypeQuantities.entrySet())
		{
			final PassengerTypeQuantityRAO passengerTypeQuantityRAO = new PassengerTypeQuantityRAO();
			passengerTypeQuantityRAO.setQuantity(passengerTypeQuantity.getValue());
			passengerTypeQuantityRAO.setPassengerType(
					getPassengerTypeRaoConverter().convert(getPassengerTypeConverter().convert(passengerTypeQuantity.getKey())));

			passengerTypeQuantityRAOs.add(passengerTypeQuantityRAO);
		}

		return passengerTypeQuantityRAOs;
	}

	/**
	 * Gets passenger type rao converter.
	 *
	 * @return the passenger type rao converter
	 */
	protected Converter<PassengerTypeData, PassengerTypeRAO> getPassengerTypeRaoConverter()
	{
		return passengerTypeRaoConverter;
	}

	/**
	 * Sets passenger type rao converter.
	 *
	 * @param passengerTypeRaoConverter
	 * 		the passenger type rao converter
	 */
	@Required
	public void setPassengerTypeRaoConverter(final Converter<PassengerTypeData, PassengerTypeRAO> passengerTypeRaoConverter)
	{
		this.passengerTypeRaoConverter = passengerTypeRaoConverter;
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
	public void setPassengerTypeConverter(final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}
}
