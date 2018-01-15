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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.List;
import java.util.Map;


/**
 * Abstract property handler performing validation when requested and implementing methods shared across property
 * handlers. The handlingAttributes method will be implemented by concrete handlers which will use it to inject their
 * own business logic.
 */
public abstract class AbstractDefaultPropertyHandler
{

	protected Boolean validateDayRatesAgainstRequest(
			final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationRequest)
	{
		return Boolean.valueOf(validateDayRateAgainstDates(dayRatesForRoomStayCandidate, accommodationRequest)
				&& validateDayRateAgainstRoomStayCandidates(dayRatesForRoomStayCandidate, accommodationRequest));
	}

	/**
	 * Document validation: check if documents refer to the correct number of room stay candidate that is there must be a
	 * list of documents of each room stay candidate requested
	 *
	 * @param dayRatesForRoomStayCandidate mapping between a room stay reference number and the related documents
	 * @param accommodationRequest         the request dto
	 * @return true if the list of documents is valid, false otherwise
	 */
	protected boolean validateDayRateAgainstRoomStayCandidates(
			final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationRequest)
	{
		return dayRatesForRoomStayCandidate.entrySet().size() == accommodationRequest.getCriterion().getRoomStayCandidates()
				.size();
	}

	/**
	 * Document validation: check of the number of documents returned covers the duration of the journey that is there
	 * must be one and only one document for each room stay candidate, for each day
	 *
	 * @param dayRatesForRoomStayCandidate mapping between a room stay reference number and the related documents
	 * @param accommodationRequest         the request dto
	 * @return true if the list of documents is valid, false otherwise
	 */
	protected boolean validateDayRateAgainstDates(
			final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationRequest)
	{
		final long requestedDuration = getRequestedDuration(accommodationRequest);
		for (final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> entry : dayRatesForRoomStayCandidate.entrySet())
		{
			if (entry.getValue().size() != requestedDuration)
			{
				return false;
			}
		}
		return true;
	}

	protected long getRequestedDuration(final AccommodationSearchRequestData accommodationRequest)
	{
		return TravelDateUtils.getDaysBetweenDates(accommodationRequest.getCriterion().getStayDateRange().getStartTime(),
				accommodationRequest.getCriterion().getStayDateRange().getEndTime());
	}

	protected abstract void handlingAttributes(
			final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final PropertyData propertyData);
}
