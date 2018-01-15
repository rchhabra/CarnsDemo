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
import de.hybris.platform.ndcfacades.ndc.FlightArrivalType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;


/**
 * NDC Flight Segment Arrival Populator set Arrival for {@link ListOfFlightSegmentType} based on transportOfferingData arrival
 */
public class NDCFlightSegmentArrivalPopulator implements Populator<TransportOfferingData, ListOfFlightSegmentType>
{

	@Override
	public void populate(final TransportOfferingData transportOfferingData, final ListOfFlightSegmentType listOfFlightSegmentType)
			throws ConversionException
	{
		final FlightArrivalType arrival = new FlightArrivalType();
		final FlightArrivalType.AirportCode arrivalAirportCode = new FlightArrivalType.AirportCode();

		if (Objects.nonNull(transportOfferingData.getArrivalTime()))
		{
			arrival.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOfferingData.getArrivalTime(),
					transportOfferingData.getArrivalTimeZoneId()));
			arrival.setTime(NdcFacadesUtils.dateToTimeString(transportOfferingData.getArrivalTime()));
		}

		arrival.setAirportName(transportOfferingData.getSector().getDestination().getName());

		arrivalAirportCode.setValue(transportOfferingData.getSector().getDestination().getCode());
		arrival.setAirportCode(arrivalAirportCode);
		listOfFlightSegmentType.setArrival(arrival);
	}
}
