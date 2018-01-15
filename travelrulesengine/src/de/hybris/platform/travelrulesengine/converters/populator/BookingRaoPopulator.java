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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.ItineraryRAO;
import de.hybris.platform.travelrulesengine.rao.ReservationItemRAO;
import de.hybris.platform.travelrulesengine.rao.TransportOfferingRAO;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The type Booking rao populator.
 */
public class BookingRaoPopulator implements Populator<OrderModel, BookingRAO>
{

	@Override
	public void populate(final OrderModel order, final BookingRAO bookingRao) throws ConversionException
	{
		final List<AbstractOrderEntryModel> fareProductEntries = order.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null)
				.filter(entry -> (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
						&& entry.getActive())
				.collect(Collectors.toList());

		final Set<Integer> journeys = new HashSet<>();
		fareProductEntries.forEach(entry -> journeys.add(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()));

		bookingRao.setBookingStatus(order.getTransportationOrderStatus());
		populateReservationItems(bookingRao, journeys);

		for (final ReservationItemRAO reservationItem : bookingRao.getReservationItems())
		{
			populateItinerary(reservationItem, fareProductEntries);
		}
	}

	/**
	 * Populate reservation items.
	 *
	 * @param bookingRao
	 * 		the booking rao
	 * @param journeys
	 * 		the journeys
	 */
	protected void populateReservationItems(final BookingRAO bookingRao, final Set<Integer> journeys)
	{
		final List<ReservationItemRAO> reservationItems = new ArrayList<>();
		for (final Integer journeyNumber : journeys)
		{
			final ReservationItemRAO reservationItemRAO = new ReservationItemRAO();
			reservationItemRAO.setOriginDestinationRefNumber(journeyNumber);
			reservationItems.add(reservationItemRAO);
		}
		bookingRao.setReservationItems(reservationItems);
	}

	/**
	 * Populate itinerary.
	 *
	 * @param reservationItem
	 * 		the reservation item
	 * @param fareProductEntries
	 * 		the fare product entries
	 */
	protected void populateItinerary(final ReservationItemRAO reservationItem,
			final List<AbstractOrderEntryModel> fareProductEntries)
	{
		final List<TransportOfferingModel> transportOfferings = getTransportOfferingsFromFareProducts(fareProductEntries,
				reservationItem.getOriginDestinationRefNumber());
		final List<TransportOfferingRAO> transportOfferingRaos = new ArrayList<>();
		for (final TransportOfferingModel transportOffering : transportOfferings)
		{
			final TransportOfferingRAO transportOfferingRao = new TransportOfferingRAO();
			transportOfferingRao.setDepartureTime(transportOffering.getDepartureTime());
			transportOfferingRaos.add(transportOfferingRao);
		}

		final ItineraryRAO itinerary = new ItineraryRAO();
		itinerary.setTransportOfferings(transportOfferingRaos);
		reservationItem.setItinerary(itinerary);
	}

	/**
	 * Gets transport offerings from fare products.
	 *
	 * @param fareProductEntries
	 * 		the fare product entries
	 * @param originDestinationRefNo
	 * 		the origin destination ref no
	 * @return the transport offerings from fare products
	 */
	protected List<TransportOfferingModel> getTransportOfferingsFromFareProducts(
			final List<AbstractOrderEntryModel> fareProductEntries, final int originDestinationRefNo)
	{
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		fareProductEntries.stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNo)
				.filter(entry -> !transportOfferings.containsAll(entry.getTravelOrderEntryInfo().getTransportOfferings()))
				.forEach(entry -> entry.getTravelOrderEntryInfo().getTransportOfferings().stream()
						.filter(transportOffering -> !transportOfferings.contains(transportOffering))
						.forEach(transportOfferings::add));
		return transportOfferings;
	}
}
