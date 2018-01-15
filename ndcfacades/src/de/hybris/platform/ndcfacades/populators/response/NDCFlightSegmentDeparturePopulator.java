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
import de.hybris.platform.ndcfacades.ndc.Departure;
import de.hybris.platform.ndcfacades.ndc.FlightDepartureType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;


/**
 * NDC Flight Segment Departure Populator set departure for {@link ListOfFlightSegmentType} based on transportOfferingData origin
 */
public class NDCFlightSegmentDeparturePopulator implements Populator<TransportOfferingData, ListOfFlightSegmentType>
{

	@Override
	public void populate(final TransportOfferingData transportOfferingData, final ListOfFlightSegmentType listOfFlightSegmentType)
			throws ConversionException
	{
		final Departure departure = new Departure();
		final FlightDepartureType.AirportCode departureAirportCode = new FlightDepartureType.AirportCode();

		if (Objects.nonNull(transportOfferingData.getDepartureTime()))
		{
			departure.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOfferingData.getDepartureTime(),
					transportOfferingData.getDepartureTimeZoneId()));
			departure.setTime(NdcFacadesUtils.dateToTimeString(transportOfferingData.getDepartureTime()));
		}

		departure.setAirportName(transportOfferingData.getSector().getOrigin().getName());

		departureAirportCode.setValue(transportOfferingData.getSector().getOrigin().getCode());
		departure.setAirportCode(departureAirportCode);
		listOfFlightSegmentType.setDeparture(departure);
	}
}
