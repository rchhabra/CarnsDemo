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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Handler to populate selected accommodation from offer request data to offer response data
 */
public class SelectedAccommodationHandler implements AncillarySearchHandler
{

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{

		if (offerRequestData.getSeatMapRequest() == null
				|| CollectionUtils.isEmpty(offerRequestData.getSeatMapRequest().getSelectedSeats()))
		{
			return;
		}
		if (offerResponseData.getSeatMap() == null || CollectionUtils.isEmpty(offerResponseData.getSeatMap().getSeatMap()))
		{
			return;
		}

		final List<SelectedSeatData> selectedSeats = offerRequestData.getSeatMapRequest().getSelectedSeats();
		final List<SeatMapData> seatMap = offerResponseData.getSeatMap().getSeatMap();
		for (final SeatMapData seatMapData : seatMap)
		{
			final List<SelectedSeatData> selectedSeatsForSeatMap = new ArrayList<>();
			seatMapData.setSelectedSeats(selectedSeatsForSeatMap);
			for (final SelectedSeatData selectedSeat : selectedSeats)
			{
				if (seatMapData.getTransportOffering().getCode().equals(selectedSeat.getTransportOffering().getCode()))
				{
					selectedSeatsForSeatMap.add(selectedSeat);
				}
			}
		}
	}

}
