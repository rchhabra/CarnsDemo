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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AirlineID;
import de.hybris.platform.ndcfacades.ndc.FlightNumber;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

/**
 * NDC Flight Segment Operating Carrier Populator for {@link ListOfFlightSegmentType}
 */
public class NDCFlightSegmentOperatingCarrierPopulator  implements Populator<TransportOfferingData, ListOfFlightSegmentType>
{
	@Override
	public void populate(final TransportOfferingData transportOfferingData, final ListOfFlightSegmentType listOfFlightSegmentType)
			throws ConversionException
	{

		if (Objects.isNull(transportOfferingData.getNumber()) || Objects.isNull(transportOfferingData.getTravelProvider()))
		{
			throw new ConversionException("Missing flight number or travel provider");
		}

		final String travelProviderCode = transportOfferingData.getTravelProvider().getCode();
		final String transportOfferingNumber = transportOfferingData.getNumber();

		final ListOfFlightSegmentType.OperatingCarrier operatingCarrier = new ListOfFlightSegmentType.OperatingCarrier();
		final AirlineID airlineId = new AirlineID();
		final FlightNumber flightNumber = new FlightNumber();

		airlineId.setValue(travelProviderCode);
		operatingCarrier.setAirlineID(airlineId);

		flightNumber.setValue(transportOfferingNumber);
		operatingCarrier.setFlightNumber(flightNumber);

		listOfFlightSegmentType.setOperatingCarrier(operatingCarrier);
	}
}
