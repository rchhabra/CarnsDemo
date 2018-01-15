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
 */

package de.hybris.platform.travelfacades.util;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * This class contains some reusable methods required for Transport Offering population
 */
public class TransportOfferingUtils
{
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_MINUTES = "transport.offering.status.result.minutes";
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_HOURS = "transport.offering.status.result.hours";
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_DAYS = "transport.offering.status.result.days";

	private TransportOfferingUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Converts the duration of transport offering from Long to a format required by view
	 *
	 * @param duration
	 * 		- duration of the transport offering in Long
	 * @return duration in a view-friendly format
	 */
	public static Map<String, Integer> getDurationMap(final Long duration)
	{
		final Long durationInMinutes = duration / (1000 * 60);
		final Map<String, Integer> durationMap = new LinkedHashMap<>();
		final int days = (int) (durationInMinutes / 60) / 24;
		if (days > 0)
		{
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_DAYS, days);
		}
		final int hours = (int) (durationInMinutes / 60) % 24;
		if (hours > 0)
		{
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_HOURS, hours);
		}
		durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_MINUTES, (int) (durationInMinutes % 60));

		return durationMap;
	}

	/**
	 * Method calculates duration of the whole journey by summing durations of all flights and breaks between arrivals
	 * and departures of connected flights
	 *
	 * @param transportOfferings
	 * 		- list of transport offerings creating the whole leg of the journey
	 * @return duration of the journey
	 */
	public static Map<String, Integer> calculateJourneyDuration(final List<TransportOfferingData> transportOfferings)
	{
		final int numberOfTranportOfferings = CollectionUtils.size(transportOfferings);
		if (numberOfTranportOfferings > 1)
		{
			Long summedDuration = 0L;
			for (int i = 0; i < numberOfTranportOfferings; i++)
			{
				summedDuration += transportOfferings.get(i).getDurationValue();
				if (i > 0)
				{
					final ZonedDateTime departureUtcTime = TravelDateUtils.getUtcZonedDateTime(
							transportOfferings.get(i).getDepartureTime(), transportOfferings.get(i).getDepartureTimeZoneId());
					final ZonedDateTime arrivalUtcTime = TravelDateUtils.getUtcZonedDateTime(
							transportOfferings.get(i - 1).getArrivalTime(), transportOfferings.get(i - 1).getArrivalTimeZoneId());
					summedDuration += Duration.between(arrivalUtcTime, departureUtcTime).toMillis();
				}
			}
			return TransportOfferingUtils.getDurationMap(summedDuration);
		}
		else if (numberOfTranportOfferings == 1)
		{
			return transportOfferings.get(0).getDuration();
		}

		return null;
	}

	/**
	 * Calculates duration from duration map
	 *
	 * @param durationMap
	 * 		the duration map
	 * @return Long value of duration map
	 */
	public static Long getDuration(final Map<String, Integer> durationMap)
	{
		Long duration = 0L;
		if (durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_MINUTES) != null)
		{
			duration += durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_MINUTES).longValue() * 60000;
		}
		if (durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_HOURS) != null)
		{
			duration += durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_HOURS).longValue() * 3600000;
		}
		if (durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_DAYS) != null)
		{
			duration += durationMap.get(TRANSPORT_OFFERING_STATUS_RESULT_DAYS).longValue() * 86400000;
		}
		return duration;
	}

	/**
	 * Compares 2 lists of Transport Offerings to see if they have the same elements
	 *
	 * @param transportOfferings1
	 * 		the transport offerings 1
	 * @param transportOfferings2
	 * 		the transport offerings 2
	 * @return true if lists have the same elements
	 */
	public static boolean compareTransportOfferings(final List<TransportOfferingData> transportOfferings1,
			final List<TransportOfferingData> transportOfferings2)
	{
		if (transportOfferings1.size() == transportOfferings2.size())
		{
			final List<String> offerTOCodes = new ArrayList<String>(transportOfferings1.size());
			transportOfferings1.forEach(offerTO -> offerTOCodes.add(offerTO.getCode()));

			final List<String> toCodes = new ArrayList<String>(transportOfferings2.size());
			transportOfferings2.forEach(to -> toCodes.add(to.getCode()));

			if (toCodes.containsAll(offerTOCodes))
			{
				return true;
			}
		}
		return false;
	}

}
