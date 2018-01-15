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
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for instantiating Reservation Items (one per leg) and populating travellers, itinerary
 * and origin destination ref number in them.
 */
public class ReservationItemHandler extends AbstractReservationItemHandler implements ReservationHandler
{
	private Converter<TravellerModel, TravellerData> travellerDataConverter;
	private TravellerService travellerService;
	private TravellerSortStrategy travellerSortStrategy;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		final List<ReservationItemData> reservationItems = new ArrayList<>();

		if (Objects.isNull(abstractOrderModel) || CollectionUtils.isEmpty(abstractOrderModel.getEntries()))
		{
			reservationData.setReservationItems(reservationItems);
			return;
		}

		final List<AbstractOrderEntryModel> fareProductEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> entry.getProduct() instanceof FareProductModel
						&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
								|| entry.getProduct() instanceof FareProductModel)
						&& entry.getActive())
				.collect(Collectors.toList());

		createReservationItems(fareProductEntries, reservationItems);

		final Map<Integer, List<TravellerModel>> travellersMap = getTravellerService().getTravellersPerLeg(abstractOrderModel);

		reservationItems.forEach(reservationItem -> reservationItem
				.setReservationItinerary(createItinerary(fareProductEntries, reservationItem.getOriginDestinationRefNumber(),
						travellersMap.get(reservationItem.getOriginDestinationRefNumber()))));

		reservationData.setReservationItems(reservationItems);

	}

	/**
	 * Populates details of itinerary for a leg such as route, duration, origin destination options and travellers
	 *
	 * @param fareProductEntries
	 * 		the fare product entries
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param travellerList
	 * 		the traveller list
	 * @return itinerary itinerary data
	 */
	protected ItineraryData createItinerary(final List<AbstractOrderEntryModel> fareProductEntries,
			final int originDestinationRefNumber, final List<TravellerModel> travellerList)
	{
		final ItineraryData itinerary = new ItineraryData();
		TravelRouteModel travelRoute = null;

		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		int maxOriginDestinationRefNum = 0;
		for (final AbstractOrderEntryModel entry : fareProductEntries)
		{
			if (Objects.nonNull(entry.getTravelOrderEntryInfo()))
			{
				if (entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber)
				{
					if (Objects.isNull(travelRoute))
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
				maxOriginDestinationRefNum = entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber();
			}
		}
		TravelRouteData travelRouteData = new TravelRouteData();
		if (Objects.nonNull(travelRoute))
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
		final List<TravellerData> travellers = Converters.convertAll(travellerList, getTravellerDataConverter());
		itinerary.setTravellers(getTravellerSortStrategy().applyStrategy(travellers));
		itinerary.setTripType(maxOriginDestinationRefNum > 0 ? TripType.RETURN : TripType.SINGLE);
		return itinerary;
	}

	/**
	 * Gets traveller data converter.
	 *
	 * @return the travellerDataConverter
	 */
	protected Converter<TravellerModel, TravellerData> getTravellerDataConverter()
	{
		return travellerDataConverter;
	}

	/**
	 * Sets traveller data converter.
	 *
	 * @param travellerDataConverter
	 * 		the travellerDataConverter to set
	 */
	@Required
	public void setTravellerDataConverter(final Converter<TravellerModel, TravellerData> travellerDataConverter)
	{
		this.travellerDataConverter = travellerDataConverter;
	}

	/**
	 * Gets traveller service.
	 *
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 * 		the travellerService to set
	 */
	@Required
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * Gets traveller sort strategy.
	 *
	 * @return the travellerSortStrategy
	 */
	protected TravellerSortStrategy getTravellerSortStrategy()
	{
		return travellerSortStrategy;
	}

	/**
	 * Sets traveller sort strategy.
	 *
	 * @param travellerSortStrategy
	 * 		the travellerSortStrategy to set
	 */
	@Required
	public void setTravellerSortStrategy(final TravellerSortStrategy travellerSortStrategy)
	{
		this.travellerSortStrategy = travellerSortStrategy;
	}
}
