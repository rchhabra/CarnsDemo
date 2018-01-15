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
package de.hybris.platform.travelfacades.facades.accommodation.populators;

import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class will populate the {@link GuestOccupancyData}
 */
public class GuestOccupancyPopulator<SOURCE extends GuestOccupancyModel, TARGET extends GuestOccupancyData> implements
		Populator<SOURCE, TARGET>
{

	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setQuantityMin(source.getQuantityMin());
		target.setQuantityMax(source.getQuantityMax());
		target.setPassengerType(getPassengerTypeConverter().convert(source.getPassengerType()));
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
			Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}
}
