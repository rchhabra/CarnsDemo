/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;



/**
 * The Reserved room stays handler concrete implementation of {@link AccommodationDetailsHandler}. This handler will
 * provide on the {@link AccommodationAvailabilityResponseData} the information of the {@link ReservedRoomStayData}
 */
public class ReservedRoomStaysHandler implements AccommodationDetailsHandler
{
	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final List<ReservedRoomStayData> reservedRoomStays = availabilityRequestData.getReservedRoomStays();
		if (CollectionUtils.isNotEmpty(reservedRoomStays))
		{
			reservedRoomStays.sort(Comparator.comparing(RoomStayData::getRoomStayRefNumber));
		}
		accommodationAvailabilityResponseData.setReservedRoomStays(reservedRoomStays);
	}
}
