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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;


/**
 * The type Search params originating locations rao populator. This class should connect to a service that is able to retrieve the
 * Originating Locations based on the IP address set in Session.
 */
public class SearchParamsOriginatingLocationsRaoPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{

	@Override
	public void populate(final FareSearchRequestData source, final SearchParamsRAO target)
			throws ConversionException
	{
		// MOCK OriginatingLocations
		// We assume that the location from which the request is originated is corresponding to the departure location.
		target.setOriginatingLocations(target.getOriginLocations());
	}
}
