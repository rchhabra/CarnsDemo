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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;


/**
 * The type Guest count reverse populator.
 */
public class GuestCountReversePopulator implements Populator<PassengerTypeQuantityData, GuestCountModel>
{

	private PassengerTypeService passengerTypeService;

	@Override
	public void populate(final PassengerTypeQuantityData source, final GuestCountModel target) throws ConversionException
	{
		target.setPassengerType(getPassengerTypeService().getPassengerType(source.getPassengerType().getCode()));
		target.setQuantity(source.getQuantity());
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passengerTypeService
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 * 		the passengerTypeService to set
	 */
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

}
