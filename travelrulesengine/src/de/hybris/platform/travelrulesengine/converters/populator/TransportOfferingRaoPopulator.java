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

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.TransportOfferingRAO;


/**
 * Populates TransportOfferingRAO attributes from TransportOfferingData.
 */
public class TransportOfferingRaoPopulator implements Populator<TransportOfferingData, TransportOfferingRAO>
{

	@Override
	public void populate(final TransportOfferingData source, final TransportOfferingRAO target) throws ConversionException
	{
		target.setTransportOfferingCode(source.getCode());
		target.setValid(Boolean.TRUE);
		target.setDepartureTime(source.getDepartureTime());
	}

}
