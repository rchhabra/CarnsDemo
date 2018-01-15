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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Check what roomStay and what ratePlan have been selected in the reservedRoomStays and set the selected property to true.
 */
public class PackageSelectedRoomStaysHandler implements PackageResponseHandler
{
	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		if (!packageResponseData.isAvailable())
		{
			return;
		}

		final List<ReservedRoomStayData> reservedRoomStays = packageResponseData.getAccommodationPackageResponse()
				.getAccommodationAvailabilityResponse().getReservedRoomStays();

		if (CollectionUtils.isEmpty(reservedRoomStays))
		{
			return;
		}

		final List<RoomStayData> roomStays = packageResponseData.getAccommodationPackageResponse()
				.getAccommodationAvailabilityResponse().getRoomStays();

		for (final ReservedRoomStayData reservedRoomStay : reservedRoomStays)
		{
			final Optional<RoomStayData> selectedRoomStay = roomStays.stream().filter(
					roomStay -> roomStay.getRoomStayRefNumber().equals(reservedRoomStay.getRoomStayRefNumber()) && StringUtils
							.equals(roomStay.getRoomTypes().get(0).getCode(), reservedRoomStay.getRoomTypes().get(0).getCode()))
					.findFirst();

			selectedRoomStay.ifPresent(roomStayData -> roomStayData.getRatePlans().stream().filter(
					ratePlanData -> StringUtils.equals(ratePlanData.getCode(), reservedRoomStay.getRatePlans().get(0).getCode()))
					.findFirst().ifPresent(ratePlanData -> ratePlanData.setSelected(Boolean.TRUE)));
		}
	}
}
