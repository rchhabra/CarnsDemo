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

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;


/**
 * Populator to populate the PassenegerType from the PassenegerTypeModel
 */
public class PassengerTypePopulator implements Populator<PassengerTypeModel, PassengerTypeData>
{

	@Override
	public void populate(final PassengerTypeModel source, final PassengerTypeData target) throws ConversionException
	{
		if (source != null)
		{
			target.setCode(source.getCode());
			target.setName(source.getName());
			target.setMinAge(source.getMinAge());
			target.setMaxAge(source.getMaxAge());
		}
	}

}
