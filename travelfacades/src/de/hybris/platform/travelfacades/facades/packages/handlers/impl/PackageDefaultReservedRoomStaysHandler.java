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
 */

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageReservedRoomStayHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Concrete implementation of {@link PackageReservedRoomStayHandler} responsible for populating reserved room stays on
 * accommodation availability response based on the cheapest available rate plan of the cheapest room stay which
 * fulfills the guest occupancy specified in the request.
 */
public class PackageDefaultReservedRoomStaysHandler implements PackageReservedRoomStayHandler
{
	@Override
	public void handle(final PackageRequestData packageRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final List<RoomStayCandidateData> roomStayCandidates = packageRequestData.getAccommodationPackageRequest()
				.getAccommodationSearchRequest().getCriterion().getRoomStayCandidates();
		// This handler should only be executed if number of reserved room stays populated using configured rate plans are not same as the number of rooms requested
		if (CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays())
				&& CollectionUtils.size(accommodationAvailabilityResponseData.getReservedRoomStays().stream()
						.filter(reservedRoomStay -> !reservedRoomStay.getNonModifiable())
						.collect(Collectors.toList())) == CollectionUtils.size(roomStayCandidates))
		{
			return;
		}

		final List<RoomStayData> roomStayDatas = accommodationAvailabilityResponseData.getRoomStays();

		//find out the cheapest roomStayData for every roomStayCandidateData.
		if (CollectionUtils.isNotEmpty(roomStayCandidates))
		{
			final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>(roomStayCandidates.size());
			List<Integer> existingRoomStayRefNumbers = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays())
					&& CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays().stream()
							.filter(reservedRoomStayData -> !reservedRoomStayData.getNonModifiable()).collect(Collectors.toList())))
			{
				reservedRoomStays.addAll(accommodationAvailabilityResponseData.getReservedRoomStays());
				existingRoomStayRefNumbers = accommodationAvailabilityResponseData.getReservedRoomStays().stream()
						.filter(reservedRoomStayData -> !reservedRoomStayData.getNonModifiable())
						.map(RoomStayData::getRoomStayRefNumber).collect(Collectors.toList());
			}

			final Map<Integer, List<RoomStayData>> roomStayDataMap = getRoomStayDataMap(roomStayDatas, existingRoomStayRefNumbers);
			roomStayDataMap.entrySet().forEach(entry -> {
				final ReservedRoomStayData cheapestRoomStayData = getCheapestRoomStayData(entry.getValue(), reservedRoomStays);
				if (cheapestRoomStayData != null)
				{
					reservedRoomStays.add(cheapestRoomStayData);
				}
			});
			if (CollectionUtils.size(packageRequestData.getAccommodationPackageRequest().getAccommodationAvailabilityRequest()
					.getCriterion().getRoomStayCandidates()) == CollectionUtils.size(reservedRoomStays))
			{
				if (CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays()))
				{
					final List<ReservedRoomStayData> nonModifiableReservedRoomStays = accommodationAvailabilityResponseData
							.getReservedRoomStays().stream().filter(reservedRoomStayData -> reservedRoomStayData.getNonModifiable())
							.collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(nonModifiableReservedRoomStays))
					{
						reservedRoomStays.addAll(nonModifiableReservedRoomStays);
					}
				}
				accommodationAvailabilityResponseData.setReservedRoomStays(reservedRoomStays);
			}
		}
	}

	/**
	 * This method groups the List of {@link RoomStayData} by its roomStayRefNumber
	 *
	 * @param roomStays
	 * @return grouped map of roomStays
	 */
	protected Map<Integer, List<RoomStayData>> getRoomStayDataMap(final List<RoomStayData> roomStays,
			final List<Integer> existingRoomStayRefNumbers)
	{
		if (CollectionUtils.isNotEmpty(existingRoomStayRefNumbers))
		{
			final List<RoomStayData> remainingRoomStays = new ArrayList<>();
			final List<Integer> roomStayRefNumbers = roomStays.stream().map(RoomStayData::getRoomStayRefNumber).distinct()
					.collect(Collectors.toList());
			roomStayRefNumbers.removeAll(existingRoomStayRefNumbers);
			for (final int refNumber : roomStayRefNumbers)
			{
				roomStays.forEach(roomStay -> {
					if (roomStay.getRoomStayRefNumber() == refNumber)
					{
						remainingRoomStays.add(roomStay);
					}
				});
			}
			return remainingRoomStays.stream().collect(Collectors.groupingBy(RoomStayData::getRoomStayRefNumber));
		}
		return roomStays.stream().collect(Collectors.groupingBy(RoomStayData::getRoomStayRefNumber));
	}

	/**
	 * This method returns the cheapest {@link ReservedRoomStayData} with cheapest {@link RatePlanData}
	 *
	 * @param roomStays
	 * @return
	 */
	protected ReservedRoomStayData getCheapestRoomStayData(final List<RoomStayData> roomStays,
			final List<ReservedRoomStayData> reservedRoomStays)
	{
		RoomStayData cheapestRoomStayData = null;
		RatePlanData cheapestRatePlanData = null;
		for (final RoomStayData roomStayData : roomStays)
		{

			for (final RatePlanData ratePlanData : roomStayData.getRatePlans().stream()
					.filter(ratePlan -> isRatePlanAvailable(ratePlan, roomStayData, reservedRoomStays)).collect(Collectors.toList()))
			{

				if (Objects.isNull(ratePlanData.getActualRate()))
				{
					continue;
				}
				else if (Objects.isNull(cheapestRatePlanData)
						|| ratePlanData.getActualRate().getValue().compareTo(cheapestRatePlanData.getActualRate().getValue()) < 0)
				{
					cheapestRatePlanData = ratePlanData;
					cheapestRoomStayData = roomStayData;
				}
			}
		}

		if (Objects.isNull(cheapestRatePlanData) || Objects.isNull(cheapestRoomStayData))
		{
			return null;
		}

		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setRoomStayRefNumber(cheapestRoomStayData.getRoomStayRefNumber());
		reservedRoomStayData.setRoomTypes(cheapestRoomStayData.getRoomTypes());
		reservedRoomStayData.setCheckInDate(cheapestRoomStayData.getCheckInDate());
		reservedRoomStayData.setCheckOutDate(cheapestRoomStayData.getCheckOutDate());
		reservedRoomStayData.setRatePlans(Collections.singletonList(cheapestRatePlanData));
		reservedRoomStayData.setNonModifiable(Boolean.FALSE);
		return reservedRoomStayData;
	}

	protected boolean isRatePlanAvailable(final RatePlanData ratePlan, final RoomStayData roomStayData,
			final List<ReservedRoomStayData> reservedRoomStays)
	{
		final List<String> roomTypeCodes = roomStayData.getRoomTypes().stream().map(RoomTypeData::getCode)
				.collect(Collectors.toList());
		final long reserved = reservedRoomStays.stream().filter(reservedRoomStay -> reservedRoomStay.getRoomTypes().stream()
				.anyMatch(roomTypeData -> roomTypeCodes.contains(roomTypeData.getCode()))).count();
		return ratePlan.getAvailableQuantity() - reserved > 0;
	}
}
