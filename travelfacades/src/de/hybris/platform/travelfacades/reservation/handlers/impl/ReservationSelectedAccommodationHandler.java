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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Handler to provide selected accommodations from cart to {@link ReservationData}
 */
public class ReservationSelectedAccommodationHandler implements ReservationHandler
{

	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		final List<ReservationItemData> reservationItems = reservationData.getReservationItems();
		if (CollectionUtils.isEmpty(reservationItems))
		{
			return;
		}

		final List<SelectedAccommodationModel> selectedAccommodations = abstractOrderModel.getSelectedAccommodations();
		if (CollectionUtils.isEmpty(selectedAccommodations))
		{
			return;
		}

		for (final ReservationItemData reservationItemData : reservationItems)
		{
			final ItineraryData reservationItinerary = reservationItemData.getReservationItinerary();
			final List<TransportOfferingData> transportOfferings = getTranspOffersRelatedToItinerary(reservationItinerary);
			if (CollectionUtils.isEmpty(transportOfferings))
			{
				continue;
			}
			final ReservationPricingInfoData reservationPricingInfo = reservationItemData.getReservationPricingInfo();
			createSelectedSeats(selectedAccommodations, transportOfferings, reservationPricingInfo);
		}
	}

	/**
	 * Create selected seats.
	 *
	 * @param selectedAccommodations
	 * 		the selected accommodations
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param reservationPricingInfo
	 * 		the reservation pricing info
	 */
	protected void createSelectedSeats(final List<SelectedAccommodationModel> selectedAccommodations,
			final List<TransportOfferingData> transportOfferings, final ReservationPricingInfoData reservationPricingInfo)
	{
		final List<SelectedSeatData> selectedSeats = new ArrayList<>();
		reservationPricingInfo.setSelectedSeats(selectedSeats);

		for (final SelectedAccommodationModel selectedAccommodation : selectedAccommodations)
		{
			for (final TransportOfferingData transportOfferingData : transportOfferings)
			{
				if (selectedAccommodation.getTransportOffering().getCode().equals(transportOfferingData.getCode()))
				{
					final SelectedSeatData selectedSeatData = new SelectedSeatData();
					selectedSeatData.setSeatNumber(selectedAccommodation.getConfiguredAccommodation().getIdentifier());
					selectedSeatData.setTransportOffering(transportOfferingData);
					selectedSeatData.setTraveller(travellerDataConverter.convert(selectedAccommodation.getTraveller()));
					selectedSeats.add(selectedSeatData);
				}
			}
		}
	}

	/**
	 * Gets transp offers related to itinerary.
	 *
	 * @param reservationItinerary
	 * 		the reservation itinerary
	 * @return transp offers related to itinerary
	 */
	protected List<TransportOfferingData> getTranspOffersRelatedToItinerary(final ItineraryData reservationItinerary)
	{
		final List<OriginDestinationOptionData> originDestinationOptions = reservationItinerary.getOriginDestinationOptions();
		if (CollectionUtils.isEmpty(originDestinationOptions))
		{
			return Collections.emptyList();
		}
		final List<TransportOfferingData> allTransportOfferings = new ArrayList<>();
		for (final OriginDestinationOptionData originDestinationOptionData : originDestinationOptions)
		{
			final List<TransportOfferingData> transportOfferings = originDestinationOptionData.getTransportOfferings();
			if (CollectionUtils.isEmpty(transportOfferings))
			{
				continue;
			}
			allTransportOfferings.addAll(transportOfferings);
		}
		return allTransportOfferings;
	}

	/**
	 * Gets traveller data converter.
	 *
	 * @return the traveller data converter
	 */
	protected Converter<TravellerModel, TravellerData> getTravellerDataConverter()
	{
		return travellerDataConverter;
	}

	/**
	 * Sets traveller data converter.
	 *
	 * @param travellerDataConverter
	 * 		the traveller data converter
	 */
	public void setTravellerDataConverter(final Converter<TravellerModel, TravellerData> travellerDataConverter)
	{
		this.travellerDataConverter = travellerDataConverter;
	}
}
