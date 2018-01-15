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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible to mark as selected the {@link
 * ItineraryPricingInfoData} in the {@link FareSelectionData} based of the {@link ItineraryPricingInfoData} of the {@link
 * ReservationData}
 */
public class SelectedFareBundlesHandler implements FareSearchHandler
{
	private CartService cartService;
	private ReservationPipelineManager transportReservationSummaryPipelineManager;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (getCartService().hasSessionCart())
		{
			final ReservationData reservationData = getTransportReservationSummaryPipelineManager()
					.executePipeline(getCartService().getSessionCart());

			if (reservationData == null || CollectionUtils.isEmpty(reservationData.getReservationItems()))
			{
				return;
			}

			populateSelectedBundles(fareSelectionData, reservationData);
		}
	}

	/**
	 * Populates the selected attribute of the ItineraryPricingInfo for the selected bundleType
	 *
	 * @param fareSelectionData
	 * 		as the fare selection data
	 * @param reservationData
	 * 		as the reservation data
	 */
	protected void populateSelectedBundles(final FareSelectionData fareSelectionData, final ReservationData reservationData)
	{
		for (final ReservationItemData reservationItem : reservationData.getReservationItems())
		{
			final List<PricedItineraryData> pricedItineraryForOD = fareSelectionData.getPricedItineraries().stream().filter(
					pricedItineraryData -> pricedItineraryData.getOriginDestinationRefNumber() == reservationItem
							.getOriginDestinationRefNumber()).collect(Collectors.toList());

			final Optional<PricedItineraryData> selectedPricedItinerary = pricedItineraryForOD.stream().filter(
					pricedItinerary -> isSamePricedItinerary(pricedItinerary.getItinerary(),
							reservationItem.getReservationItinerary())).findAny();

			if (selectedPricedItinerary.isPresent())
			{
				final Optional<ItineraryPricingInfoData> selectedItineraryPricingInfo = selectedPricedItinerary.get()
						.getItineraryPricingInfos().stream().filter(
								itineraryPricingInfo -> StringUtils.equals(itineraryPricingInfo.getBundleType(),
										reservationItem.getReservationPricingInfo().getItineraryPricingInfo().getBundleType())).findAny();
				selectedItineraryPricingInfo.ifPresent(itineraryPricingInfo -> itineraryPricingInfo.setSelected(true));
			}
		}
	}

	/**
	 * Checks if the two itinerary given as input parameters are the same one. First it checks if the size of the list of {@link
	 * de.hybris.platform.commercefacades.travel.OriginDestinationOptionData} is the same; if it is the same, it then checks if
	 * the
	 * list of {@link TransportOfferingData} codes is the same.
	 *
	 * @param itinerary
	 * 		as the itinerary of the fareSelectionData
	 * @param reservationItinerary
	 * 		as the itinerary of the reservationData
	 *
	 * @return true if the two itineraries are the same, false otherwise
	 */
	protected boolean isSamePricedItinerary(final ItineraryData itinerary, final ItineraryData reservationItinerary)
	{
		if (!(CollectionUtils.size(itinerary.getOriginDestinationOptions()) == CollectionUtils
				.size(reservationItinerary.getOriginDestinationOptions())))
		{
			return false;
		}

		return itinerary.getOriginDestinationOptions().stream().allMatch(
				originDestinationOption -> reservationItinerary.getOriginDestinationOptions().stream().anyMatch(
						odOption -> getTransportOfferingCodes(odOption.getTransportOfferings())
								.containsAll(getTransportOfferingCodes(originDestinationOption.getTransportOfferings()))));
	}

	/**
	 * Returns a list of Strings representing the transportOffering codes from the given list of {@link TransportOfferingData}.
	 *
	 * @param transportOfferings
	 * 		as the list of transport offerings
	 *
	 * @return the list of the transport offering codes
	 */
	protected List<String> getTransportOfferingCodes(final List<TransportOfferingData> transportOfferings)
	{
		return transportOfferings.stream().map(TransportOfferingData::getCode).collect(Collectors.toList());
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 * 		the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the transportReservationSummaryPipelineManager
	 */
	protected ReservationPipelineManager getTransportReservationSummaryPipelineManager()
	{
		return transportReservationSummaryPipelineManager;
	}

	/**
	 * @param transportReservationSummaryPipelineManager
	 * 		the transportReservationSummaryPipelineManager to set
	 */
	@Required
	public void setTransportReservationSummaryPipelineManager(
			final ReservationPipelineManager transportReservationSummaryPipelineManager)
	{
		this.transportReservationSummaryPipelineManager = transportReservationSummaryPipelineManager;
	}
}
