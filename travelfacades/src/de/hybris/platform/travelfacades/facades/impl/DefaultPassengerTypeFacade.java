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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Facade that provides Passenger Type specific services. The facade uses the PassengerTypeService to get
 * PassengerTypeModel and uses converter/populators to transfer to PassengerTypeData types.
 *
 */

public class DefaultPassengerTypeFacade implements PassengerTypeFacade
{

	private PassengerTypeService passengerTypeService;
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Override
	public List<PassengerTypeData> getPassengerTypes()
	{

		final List<PassengerTypeModel> ptModels = getPassengerTypeService().getPassengerTypes();

		return Converters.convertAll(ptModels, getPassengerTypeConverter());
	}

	protected Converter<PassengerTypeModel, PassengerTypeData> getPassengerTypeConverter()
	{
		return passengerTypeConverter;
	}

	@Required
	public void setPassengerTypeConverter(final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}

	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	@Required
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

}
