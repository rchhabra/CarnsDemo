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

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageReservedRoomStayHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * This handler removes out of stock room options
 */
public class FilterOutOfStockRoomStaysHandler implements PackageReservedRoomStayHandler
{

	@Override
	public void handle(final PackageRequestData packageRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		if (CollectionUtils.isEmpty(accommodationAvailabilityResponseData.getReservedRoomStays()))
		{
			return;
		}

		final List<ReservedRoomStayData> reservedRoomStays = accommodationAvailabilityResponseData.getReservedRoomStays().stream()
				.filter(reservedRoomStay -> !reservedRoomStay.getNonModifiable()).collect(Collectors.toList());

		reservedRoomStays.forEach(reservedRoomStay ->
		{
			final List<RoomStayData> outOfStockRoomStays = accommodationAvailabilityResponseData.getRoomStays().stream()
					.filter(roomStay -> !Objects.equals(roomStay.getRoomStayRefNumber(), reservedRoomStay.getRoomStayRefNumber()))
					.filter(roomStay -> roomStay.getRoomTypes().stream().anyMatch(roomType -> StringUtils
							.equalsIgnoreCase(roomType.getCode(), reservedRoomStay.getRoomTypes().get(0).getCode())) &&
							roomStay.getRatePlans().get(0).getAvailableQuantity() <= 1).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(outOfStockRoomStays))
			{
				accommodationAvailabilityResponseData.getRoomStays().removeAll(outOfStockRoomStays);
			}
		});

	}

}
