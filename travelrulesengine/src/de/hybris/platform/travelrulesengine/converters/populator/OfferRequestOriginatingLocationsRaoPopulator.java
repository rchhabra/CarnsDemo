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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;


/**
 * This class populates market location to OfferRequestRao
 */
public class OfferRequestOriginatingLocationsRaoPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		// MOCK OriginatingLocations
		// We assume that the location from which the request is originated is corresponding to the departure location.
		target.setOriginatingLocations(target.getOriginLocations());
	}

}
