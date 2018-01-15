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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Handler to populate selected accommodation data from reservation data(cart) to offer request data
 */
public class AccommodationMapRequestHandler implements AncillarySearchRequestHandler
{

	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		final List<ReservationItemData> reservationItems = reservationData.getReservationItems();
		if (CollectionUtils.isEmpty(reservationItems))
		{
			return;
		}
		final SeatMapRequestData seatMapRequest = new SeatMapRequestData();
		offerRequestData.setSeatMapRequest(seatMapRequest);
		final List<SelectedSeatData> selectedSeats = new ArrayList<>();
		seatMapRequest.setSelectedSeats(selectedSeats);
		for (final ReservationItemData reservationItemData : reservationItems)
		{
			final ReservationPricingInfoData reservationPricingInfo = reservationItemData.getReservationPricingInfo();
			if (reservationPricingInfo == null)
			{
				continue;
			}
			seatMapRequest.setSegmentInfoDatas(reservationPricingInfo.getSegmentInfoDatas());
			if (CollectionUtils.isNotEmpty(reservationPricingInfo.getSelectedSeats()))
			{
				selectedSeats.addAll(reservationPricingInfo.getSelectedSeats());
			}
		}
	}

}
