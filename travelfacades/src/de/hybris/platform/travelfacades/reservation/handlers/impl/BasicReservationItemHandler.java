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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * This handler is responsible for instantiating Reservation Items (one per leg),and and populating itinerary and origin
 * destination ref number in them.
 */
public class BasicReservationItemHandler extends AbstractReservationItemHandler implements ReservationHandler
{
	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		final List<ReservationItemData> reservationItems = new ArrayList<>();

		if (abstractOrderModel == null || CollectionUtils.isEmpty(abstractOrderModel.getEntries()))
		{
			reservationData.setReservationItems(reservationItems);
			return;
		}

		final List<AbstractOrderEntryModel> fareProductEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
						&& entry.getActive())
				.collect(Collectors.toList());

		createReservationItems(fareProductEntries, reservationItems);

		reservationItems.forEach(reservationItem -> reservationItem
				.setReservationItinerary(createItinerary(fareProductEntries, reservationItem.getOriginDestinationRefNumber())));

		reservationData.setReservationItems(reservationItems);

	}

	protected ItineraryData createItinerary(final List<AbstractOrderEntryModel> fareProductEntries,
			final int originDestinationRefNumber)
	{
		final ItineraryData itinerary = new ItineraryData();
		TravelRouteModel travelRoute = null;

		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		for (final AbstractOrderEntryModel entry : fareProductEntries)
		{
			if (entry.getTravelOrderEntryInfo() != null
					&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber)
			{
				if (travelRoute == null)
				{
					travelRoute = entry.getTravelOrderEntryInfo().getTravelRoute();
				}
				if (!transportOfferings.containsAll(entry.getTravelOrderEntryInfo().getTransportOfferings()))
				{
					entry.getTravelOrderEntryInfo().getTransportOfferings().stream()
							.filter(transportOffering -> !transportOfferings.contains(transportOffering))
							.forEach(transportOfferings::add);
				}
			}
		}

		TravelRouteData travelRouteData = new TravelRouteData();
		if (null != travelRoute)
		{
			travelRouteData = getTravelRouteConverter().convert(travelRoute);
			itinerary.setRoute(travelRouteData);
		}
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>(1);
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();

		originDestinationOption.setOriginDestinationRefNumber(originDestinationRefNumber);

		final List<TransportOfferingData> transportOfferingDatas = getTransportOfferingFacade().getTransportOfferings(
				Arrays.asList(TransportOfferingOption.STATUS, TransportOfferingOption.TERMINAL), transportOfferings);

		originDestinationOption.setTransportOfferings(transportOfferingDatas);
		originDestinationOption.setTravelRouteCode(travelRouteData.getCode());
		originDestinationOption.setActive(true);
		originDestinationOptions.add(originDestinationOption);
		itinerary.setOriginDestinationOptions(originDestinationOptions);
		itinerary.setDuration(TransportOfferingUtils.calculateJourneyDuration(transportOfferingDatas));
		itinerary.setTravellers(Collections.emptyList());
		return itinerary;
	}

}
