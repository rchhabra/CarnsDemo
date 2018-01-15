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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the list of {@link RoomStayData} that
 * are linked to the {@link AccommodationOfferingModel}
 */
public class SelectedRoomStaysHandler extends AbstractRoomStaysHandler
{
	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		final StayDateRangeData stayDateRange = availabilityRequestData.getCriterion().getStayDateRange();

		final List<RoomStayData> roomStays = new ArrayList<>(availabilityRequestData.getCriterion().getRoomStayCandidates().size());

		availabilityRequestData.getCriterion().getRoomStayCandidates().forEach(roomStayCandidate -> {
			if (StringUtils.isEmpty(roomStayCandidate.getAccommodationCode()))
			{
				final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
				roomStayData.setRoomStayRefNumber(roomStayCandidate.getRoomStayCandidateRefNumber());
				roomStays.add(roomStayData);
			}
			final AccommodationModel accommodation = getAccommodationService()
					.getAccommodationForAccommodationOffering(accommodationOfferingCode, roomStayCandidate.getAccommodationCode());
			roomStays.add(createRoomStayData(accommodation, stayDateRange, roomStayCandidate));
		});
		accommodationAvailabilityResponseData.setRoomStays(roomStays);
	}
}
