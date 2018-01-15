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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.travelservices.strategies.CalculateTotalPriceForChangeDatesStrategy;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;


public class DefaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy implements CalculateTotalPriceForChangeDatesStrategy
{

	@Override
	public BigDecimal calculate(final AccommodationAvailabilityResponseData accommodationAvailabilityResponse,
			final String orderCode)
	{
		if (accommodationAvailabilityResponse == null || CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return null;
		}

		BigDecimal totalPayablePrice = BigDecimal.valueOf(0);

		for (final RoomStayData roomStayData : accommodationAvailabilityResponse.getRoomStays())
		{
			if (!(roomStayData instanceof ReservedRoomStayData))
			{
				return null;
			}

			final ReservedRoomStayData reservedRoomStayData = (ReservedRoomStayData) roomStayData;

			if (reservedRoomStayData.getTotalRate() == null)
			{
				return null;
			}

			totalPayablePrice = totalPayablePrice.add(reservedRoomStayData.getTotalRate().getActualRate().getValue());
		}

		return totalPayablePrice;
	}

}
