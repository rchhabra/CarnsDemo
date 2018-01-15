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

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;


/**
 * Populator that populates {@link PassengerTypeModel} to {@link TravellerData}
 */
public class NDCPassengerToTravellerPopulator implements Populator<PassengerTypeModel, TravellerData>
{

	@Override
	public void populate(final PassengerTypeModel source, final TravellerData target) throws ConversionException
	{
		final PassengerInformationData pid = new PassengerInformationData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode(source.getNdcCode());
		pid.setPassengerType(passengerType);
		target.setTravellerInfo(pid);
	}

}
