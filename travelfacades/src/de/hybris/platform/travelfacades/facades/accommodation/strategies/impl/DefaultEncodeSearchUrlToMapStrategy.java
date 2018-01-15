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
package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.EncodeSearchUrlToMapStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Concrete implementation of {@link EncodeSearchUrlToMapStrategy}. This class will encode the
 * {@link de.hybris.platform.commercefacades.accommodation.search.CriterionData} from the
 * {@link AccommodationSearchRequestData} into a map having param as key and value as the param value.
 */
public class DefaultEncodeSearchUrlToMapStrategy implements EncodeSearchUrlToMapStrategy
{

	private static final String DESTINATION_LOCATION_NAME = "destinationLocationName";
	private static final String DESTINATION_LOCATION = "destinationLocation";
	private static final String CHECKIN_DATE = "checkInDateTime";
	private static final String CHECKOUT_DATE = "checkOutDateTime";
	private static final String NUMBER_OF_ROOMS = "numberOfRooms";

	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String RADIUS = "radius";

	private static final String ROOM_PREFIX = "r";
	private static final String PROPERTY_NAME_FILTER = "propertyName";
	private static final String SUGGESTION_TYPE = "suggestionType";

	@Override
	public Map<String, String> encode(final AccommodationSearchRequestData request)
	{
		final CriterionData criterion = request.getCriterion();

		if (criterion == null)
		{
			return MapUtils.EMPTY_MAP;
		}

		final Map<String, String> paramMap = new HashMap<>();

		if (criterion.getPosition() != null && criterion.getRadius() != null)
		{
			paramMap.put(LATITUDE, Double.toString(criterion.getPosition().getLatitude()));
			paramMap.put(LONGITUDE, Double.toString(criterion.getPosition().getLongitude()));
			paramMap.put(RADIUS, Double.toString(criterion.getRadius().getValue()));
		}

		if (criterion.getAddress() != null)
		{
			if (StringUtils.isNotEmpty(criterion.getAddress().getLine1()))
			{
				paramMap.put(DESTINATION_LOCATION, criterion.getAddress().getLine1());
			}

			if (StringUtils.isNotEmpty(criterion.getAddress().getLine2()))
			{
				paramMap.put(DESTINATION_LOCATION_NAME, criterion.getAddress().getLine2());
			}
		}

		if (StringUtils.isNotBlank(criterion.getSuggestionType()))
		{
			paramMap.put(SUGGESTION_TYPE, criterion.getSuggestionType());
		}

		if (criterion.getStayDateRange() != null)
		{
			paramMap.put(CHECKIN_DATE, TravelDateUtils.convertDateToStringDate(criterion.getStayDateRange().getStartTime(),
					TravelservicesConstants.DATE_PATTERN));

			paramMap.put(CHECKOUT_DATE, TravelDateUtils.convertDateToStringDate(criterion.getStayDateRange().getEndTime(),
					TravelservicesConstants.DATE_PATTERN));
		}

		if (CollectionUtils.isNotEmpty(criterion.getRoomStayCandidates()))
		{
			paramMap.put(NUMBER_OF_ROOMS, Integer.toString(CollectionUtils.size(criterion.getRoomStayCandidates())));

			for (int i = 0; i < CollectionUtils.size(criterion.getRoomStayCandidates()); i++)
			{
				// build the rooms params based on the different passenger types
				final String roomNum = ROOM_PREFIX + i;

				final StringBuilder roomGuestQty = new StringBuilder();
				final RoomStayCandidateData roomStayCandidate = criterion.getRoomStayCandidates().get(i);
				for (final PassengerTypeQuantityData ptq : roomStayCandidate.getPassengerTypeQuantityList())
				{
					roomGuestQty.append(ptq.getQuantity() + "-" + ptq.getPassengerType().getCode() + ",");
				}

				paramMap.put(roomNum, roomGuestQty.substring(0, roomGuestQty.length() - 1));
			}
		}

		if (StringUtils.isNotEmpty(criterion.getPropertyFilterText()))
		{
			paramMap.put(PROPERTY_NAME_FILTER, criterion.getPropertyFilterText());
		}
		return paramMap;
	}
}
