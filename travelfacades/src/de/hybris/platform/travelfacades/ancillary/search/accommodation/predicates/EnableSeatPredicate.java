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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.predicates;

import de.hybris.platform.commercefacades.travel.seatmap.data.SeatAvailabilityData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;

import java.util.function.BiPredicate;


public class EnableSeatPredicate implements BiPredicate<SeatAvailabilityData, SeatInfoData>
{
	@Override
	public boolean test(final SeatAvailabilityData seatAvailability, final SeatInfoData seatInfo)
	{
		if (seatAvailability.getSeatNumber().equals(seatInfo.getSeatNumber()))
		{
			return true;
		}
		return false;
	}
}
