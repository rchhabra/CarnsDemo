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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeQuantityRAO;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeRAO;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populates Passenger Type Quantity attribute on SearchParamsRAO
 */
public class SearchParamsPassengerTypeQuantityRaoPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{
	private Converter<PassengerTypeData, PassengerTypeRAO> passengerTypeRaoConverter;

	@Override
	public void populate(final FareSearchRequestData fareSearchRequestData, final SearchParamsRAO searchParamsRAO)
			throws ConversionException
	{
		final List<PassengerTypeQuantityRAO> passengerTypeQuantities = new ArrayList<>();

		fareSearchRequestData.getPassengerTypes().forEach(
				passengerTypeQuantityData -> passengerTypeQuantities.add(createPassengerTypeQuantityRAO(passengerTypeQuantityData)));

		searchParamsRAO.setPassengerTypeQuantities(passengerTypeQuantities);
	}

	/**
	 * Create passenger type quantity rao passenger type quantity rao.
	 *
	 * @param passengerTypeQuantityData
	 * 		the passenger type quantity data
	 * @return the passenger type quantity rao
	 */
	protected PassengerTypeQuantityRAO createPassengerTypeQuantityRAO(final PassengerTypeQuantityData passengerTypeQuantityData)
	{
		final PassengerTypeQuantityRAO passengerTypeQuantityRAO = new PassengerTypeQuantityRAO();
		passengerTypeQuantityRAO.setQuantity(passengerTypeQuantityData.getQuantity());
		passengerTypeQuantityRAO
				.setPassengerType(getPassengerTypeRaoConverter().convert(passengerTypeQuantityData.getPassengerType()));

		return passengerTypeQuantityRAO;
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
	 * Gets passenger type rao converter.
	 *
	 * @return the passenger type rao converter
	 */
	protected Converter<PassengerTypeData, PassengerTypeRAO> getPassengerTypeRaoConverter()
	{
		return passengerTypeRaoConverter;
	}
}
