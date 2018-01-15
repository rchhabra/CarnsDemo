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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.AbstractRoomStaysHandler;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} on package details page with the list of
 * {@link RoomStayData} that are linked to the {@link AccommodationOfferingModel}
 */
public class PackageRoomStaysHandler extends AbstractRoomStaysHandler
{
	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		final StayDateRangeData stayDateRange = availabilityRequestData.getCriterion().getStayDateRange();
		final List<AccommodationModel> accommodations = getAccommodationService()
				.getAccommodationForAccommodationOffering(accommodationOfferingCode);
		final List<RoomStayCandidateData> roomStayCandidates = availabilityRequestData.getCriterion().getRoomStayCandidates();
		final List<RoomStayData> updatedRoomStayDatas = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(roomStayCandidates))
		{
			roomStayCandidates.forEach(roomStayCandidate -> {
				final List<RoomStayData> roomStays = new ArrayList<>(accommodations.size());
				accommodations.forEach(accommodation -> roomStays
						.add(createRoomStayData(accommodation, accommodations.indexOf(accommodation), stayDateRange)));

				roomStays.forEach(roomStay -> {
					checkIfEligibleToAdd(updatedRoomStayDatas, roomStay, roomStayCandidate);
				});
			});
		}
		accommodationAvailabilityResponseData.setRoomStays(updatedRoomStayDatas);
	}

	/**
	 * This method add {@link RoomStayData} to roomStayDatas if the guest occupancy for each {@link RoomStayData} greater
	 * than or equal to {@link RoomStayCandidateData}
	 *
	 * @param roomStayDatas
	 * @param roomStay
	 * @param roomStayCandidate
	 */
	protected void checkIfEligibleToAdd(final List<RoomStayData> roomStayDatas, final RoomStayData roomStay,
			final RoomStayCandidateData roomStayCandidate)
	{
		roomStay.getRoomTypes().forEach(roomTypeData -> {
			roomTypeData.getOccupancies().forEach(guestOccupancyData -> {
				if (guestOccupancyData.getPassengerType().getCode().equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
				{
					roomStayCandidate.getPassengerTypeQuantityList().forEach(passengerTypeQuantityData -> {
						if (passengerTypeQuantityData.getPassengerType().getCode()
								.equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT)
								&& guestOccupancyData.getQuantityMax() >= passengerTypeQuantityData.getQuantity())
						{
							roomStay.setRoomStayRefNumber(roomStayCandidate.getRoomStayCandidateRefNumber());
							roomStayDatas.add(roomStay);
						}
					});
				}
			});
		});
	}

}
