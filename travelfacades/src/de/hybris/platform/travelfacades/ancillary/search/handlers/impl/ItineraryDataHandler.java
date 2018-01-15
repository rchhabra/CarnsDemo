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

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Handler class to populate Itinerary data in OfferRequestData from ReservationData.
 */
public class ItineraryDataHandler implements AncillarySearchRequestHandler
{

	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		reservationData.getReservationItems().forEach(reservationItem -> {
			if (Optional.ofNullable(offerRequestData.getItineraries()).isPresent())
			{
				offerRequestData.getItineraries().add(reservationItem.getReservationItinerary());
			}
			else
			{
				final List<ItineraryData> itineraries = new ArrayList<ItineraryData>();
				itineraries.add(reservationItem.getReservationItinerary());
				offerRequestData.setItineraries(itineraries);
			}

		});

	}

}
