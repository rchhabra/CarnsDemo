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

import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.LegInfoRAO;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This class populates departure time for each leg in OfferRequestRao
 */
public class OfferRequestRaoLegInfoRaoPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		target.setTripType(source.getItineraries().get(0).getTripType());

		final List<LegInfoRAO> legInfos = new ArrayList<>();
		source.getItineraries().forEach(itinerary -> legInfos.add(createLegInfo(itinerary.getOriginDestinationOptions().get(0))));
		target.setLegInfos(legInfos);
	}

	/**
	 * Create leg info leg info rao. Remove time information to be consistent with the rule applied for the flight search in which
	 * the time is not available.
	 *
	 * @param originDestinationOptionData
	 * 		the origin destination option data
	 * @return the leg info rao
	 */
	protected LegInfoRAO createLegInfo(final OriginDestinationOptionData originDestinationOptionData)
	{
		final LegInfoRAO legInfo = new LegInfoRAO();
		legInfo.setReferenceNumber(originDestinationOptionData.getOriginDestinationRefNumber());

		final Date departureDate = originDestinationOptionData.getTransportOfferings().stream()
				.sorted(Comparator.comparing(TransportOfferingData::getDepartureTime)).collect(Collectors.toList()).get(0)
				.getDepartureTime();

		final Calendar cal = Calendar.getInstance();
		cal.setTime(departureDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		legInfo.setDepartureTime(cal.getTime());
		return legInfo;
	}

}
